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

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.ws.BindingProvider;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.ReplicationConfiguration;

import org.apache.juddi.v3.client.UDDIService;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.NotifyChangeRecordsAvailable;
import org.uddi.v3_service.UDDIReplicationPortType;

/**
 * 
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 *
 */
public class ReplicationNotifier extends TimerTask {

        private Log log = LogFactory.getLog(this.getClass());
        private Timer timer = null;
        private long startBuffer = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_START_BUFFER, 20000l); // 20s startup delay default 
        private long interval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_INTERVAL, 300000l); //5 min default
        private long acceptableLagTime = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_ACCEPTABLE_LAGTIME, 1000l); //1000 milliseconds

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
        }

        @Override
        public boolean cancel() {
                timer.cancel();
                //TODO notify other nodes that i'm going down
                return super.cancel();
        }

        //ReplicationNotifier.Enqueue(this);
        public synchronized static void Enqueue(Object change) {
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue();
                }
                queue.add(change);
        }
        static Queue queue;

        public synchronized void run() {
                log.debug("Replication thread triggered");
                if (queue == null) {
                        queue = new ConcurrentLinkedQueue();
                }
                while (!queue.isEmpty()) {
                        log.info("Notifying nodes of change records " + queue.size());
                        //TODO identify chnage set format
                        Object j = queue.poll();
                        org.uddi.repl_v3.ReplicationConfiguration repcfg = FetchEdges();
                        if (repcfg == null) {
                                log.debug("No replication configuration is defined!");
                                queue.clear();
                                break;
                        }
                        Iterator<CommunicationGraph.Edge> it = repcfg.getCommunicationGraph().getEdge().iterator();

                        while (it.hasNext()) {

                                //for (int i = 0; i < endpoints.size(); i++) {
                                UDDIReplicationPortType x = new UDDIService().getUDDIReplicationPort();
                                CommunicationGraph.Edge next = it.next();
                                next.getMessageReceiver(); //Node ID
                                Node destinationNode = getNode(next.getMessageSender());
                                if (destinationNode == null) {
                                        log.warn(next.getMessageSender() + " node was not found, cannot deliver replication messages");
                                } else {
                                        ((BindingProvider) x).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, destinationNode.getReplicationUrl());
                                        NotifyChangeRecordsAvailable req = new NotifyChangeRecordsAvailable();
                                        String node = "UNKNOWN";
                                        try {
                                                node = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                                        } catch (ConfigurationException ex) {
                                                log.fatal(ex);
                                        }
                                        req.setNotifyingNode(node);
                                        HighWaterMarkVectorType highWaterMarkVectorType = new HighWaterMarkVectorType();
                                        String nextWatermark = ""; //TODO get current watermark + 1 toString()
                                        //TODO save watermark along with change set

                                        highWaterMarkVectorType.getHighWaterMark().add(new ChangeRecordIDType(node, 1L));
                                        req.setChangesAvailable(highWaterMarkVectorType);
                                        try {
                                                x.notifyChangeRecordsAvailable(req);
                                        } catch (Exception ex) {
                                                log.warn("Unable to send change notification to " + next.getMessageSender(), ex);
                                        }
                                }
                        }
                }
        }

        private org.uddi.repl_v3.ReplicationConfiguration FetchEdges() {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = null;
                org.uddi.repl_v3.ReplicationConfiguration item = new org.uddi.repl_v3.ReplicationConfiguration();
                try {
                        tx = em.getTransaction();
                        tx.begin();
                        Query q = em.createQuery("SELECT item FROM ReplicationConfiguration item");
                        q.setMaxResults(1);
                        List<ReplicationConfiguration> results = (List<ReplicationConfiguration>) q.getResultList();
                        //   ReplicationConfiguration find = em.find(ReplicationConfiguration.class, null);
                        if (results != null && !results.isEmpty()) {
                                MappingModelToApi.mapReplicationConfiguration(results.get(0), item);
                        } else {
                                item = null;
                        }
                        tx.commit();
                        return item;
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

        private Node getNode(String messageSender) {
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = null;
                org.uddi.repl_v3.ReplicationConfiguration item = new org.uddi.repl_v3.ReplicationConfiguration();
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
