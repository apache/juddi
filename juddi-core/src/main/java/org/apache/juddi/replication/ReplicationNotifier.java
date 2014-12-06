/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.replication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.bind.JAXB;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.ChangeRecord;
import org.apache.juddi.model.ReplicationConfiguration;
import org.apache.juddi.v3.client.UDDIService;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.repl_v3.Operator;
import org.uddi.v3_service.UDDIReplicationPortType;

/**
 * Handles when local records have been changed, change journal storage and
 * notifications to all remote replication nodes that something has been
 * altered.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 *
 */
public class ReplicationNotifier extends TimerTask {

        private static Log log = LogFactory.getLog(ReplicationNotifier.class);
        private Timer timer = null;
        private long startBuffer = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
        private long interval = 5000;//AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
        private long acceptableLagTime = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_ACCEPTABLE_LAGTIME, 10000l); //1000 milliseconds
        private static String node = null;

        /**
         * default constructor
         *
         * @throws ConfigurationException
         */
        public ReplicationNotifier() throws ConfigurationException {
                super();
                timer = new Timer(true);
                timer.scheduleAtFixedRate(this, startBuffer, interval);
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue();
                }
                node = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID, "UNDEFINED_NODE_NAME");
        }

        @Override
        public boolean cancel() {
                timer.cancel();
                //TODO notify other nodes that i'm going down
                return super.cancel();
        }

        //ReplicationNotifier.Enqueue(this);
        public synchronized static void Enqueue(org.apache.juddi.model.ChangeRecord change) {
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue<ChangeRecord>();
                }
                queue.add(change);
        }
        static Queue<ChangeRecord> queue;

        /**
         *
         * @param j must be one of the UDDI save APIs
         */
        protected void ProcessChangeRecord(ChangeRecord j) {
                //store and convert the changes to database model

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = null;
                try {
                        tx = em.getTransaction();
                        tx.begin();

                        
                        em.persist(j);
                        tx.commit();
                } catch (Exception ex) {
                        log.error("error", ex);
                        if (tx != null && tx.isActive()) {
                                tx.rollback();
                        }
                        JAXB.marshal(MappingModelToApi.mapChangeRecord(j), System.out);
                } finally {
                        em.close();
                }

                log.info("ChangeRecord: " + j.getId() + "," + j.getEntityKey() + "," + j.getNodeID() + "," + j.getOriginatingUSN() + "," + j.getRecordType().toString());
                org.uddi.repl_v3.ReplicationConfiguration repcfg = FetchEdges();

                //TODO figure out what this statement means 7.5.3
                /**
                 * In the absence of a communicationGraph element from the
                 * Replication Configuration Structure (although it's mandatory in the xsd), all nodes listed in the
                 * node element MAY send any and all messages to any other node
                 * of the registry.
                 */
                if (repcfg == null) {
                        log.info("No replication configuration is defined!");
                        return;

                }
                List<String> destinationUrls = new ArrayList<String>();

                for (Operator o:repcfg.getOperator())
                {
                        //no need to tell myself about a change at myself
                        if (!o.getOperatorNodeID().equalsIgnoreCase(node))
                                 destinationUrls.add(o.getSoapReplicationURL());
                }
                /*
                Iterator<String> iterator = repcfg.getCommunicationGraph().getNode().iterator();
                while (iterator.hasNext()) {
                        String next = iterator.next();
                        
                        Node destinationNode = getNode(next);
                        if (destinationNode == null) {
                                log.warn(next + " node was not found, cannot deliver replication messages");
                        } else {
                                destinationUrls.add(destinationNode.getReplicationUrl());
                        }
                }
                Iterator<CommunicationGraph.Edge> it = repcfg.getCommunicationGraph().getEdge().iterator();

                while (it.hasNext()) {
                        //send each change set to the replication node in the graph

                        
                        CommunicationGraph.Edge next = it.next();
                        //next.getMessageReceiver(); //Node ID
                        Node destinationNode = getNode(next.getMessageReceiver());
                        if (destinationNode == null) {
                                log.warn(next.getMessageReceiver() + " node was not found, cannot deliver replication messages");
                        } else {
                                destinationUrls.add(destinationNode.getReplicationUrl());
                        }
                }*/
                for (String s : destinationUrls) {
                        //TODO the spec talks about control messages, should we even support it? seems pointless
                        UDDIReplicationPortType x = new UDDIService().getUDDIReplicationPort();
                        ((BindingProvider) x).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, s);
                        NotifyChangeRecordsAvailable req = new NotifyChangeRecordsAvailable();

                        req.setNotifyingNode(node);
                        HighWaterMarkVectorType highWaterMarkVectorType = new HighWaterMarkVectorType();

                        highWaterMarkVectorType.getHighWaterMark().add(new ChangeRecordIDType(node, j.getId()));
                        req.setChangesAvailable(highWaterMarkVectorType);

                        try {
                                x.notifyChangeRecordsAvailable(req);
                                log.info("Successfully sent change record available message to " + s);
                        } catch (Exception ex) {
                                log.warn("Unable to send change notification to " + s, ex);
                        }
                }
        }

        public synchronized void run() {
                log.debug("Replication thread triggered");
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue();
                }
                //TODO check for replication config changes
                while (!queue.isEmpty()) {
                        //for each change at this node
                        log.info("Replication, Notifying nodes of new change records. " + queue.size() + " remaining");

                        ChangeRecord j = queue.poll();
                        ProcessChangeRecord(j);

                }
        }

        /**
         * returns the latest version of the replication config or null if there
         * is no config
         *
         * @return
         */
        public static org.uddi.repl_v3.ReplicationConfiguration FetchEdges() {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = null;
                org.uddi.repl_v3.ReplicationConfiguration item = new org.uddi.repl_v3.ReplicationConfiguration();
                try {
                        tx = em.getTransaction();
                        tx.begin();
                        Query q = em.createQuery("SELECT item FROM ReplicationConfiguration item order by item.serialNumber DESC");
                        q.setMaxResults(1);
                        ReplicationConfiguration results = (ReplicationConfiguration) q.getSingleResult();
                        //   ReplicationConfiguration find = em.find(ReplicationConfiguration.class, null);
                        if (results != null) {
                                MappingModelToApi.mapReplicationConfiguration(results, item);
                        }

                        tx.commit();
                        return item;
                } catch (Exception ex) {
                        //log.error("error", ex);
                        //no config available
                        if (tx != null && tx.isActive()) {
                                tx.rollback();
                        }
                } finally {
                        em.close();
                }
                return null;
        }

        private Node getNode(String messageSender) {
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = null;
                try {
                        tx = em.getTransaction();
                        tx.begin();
                        Node api = new Node();
                        org.apache.juddi.model.Node find = em.find(org.apache.juddi.model.Node.class, messageSender);
                        if (find != null) {
                                MappingModelToApi.mapNode(find, api);
                        }
                        tx.commit();
                        return api;
                } catch (Exception ex) {
                        log.error("error", ex);
                        if (tx != null && tx.isActive()) {
                                tx.rollback();
                        }
                } finally {
                        em.close();
                }
                return null;
        }
}
