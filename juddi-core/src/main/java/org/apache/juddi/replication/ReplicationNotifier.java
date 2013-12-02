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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.ws.BindingProvider;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.ServiceCounterLifecycleResource;
import org.apache.juddi.api.impl.UDDIReplicationImpl;
import org.apache.juddi.api.impl.UDDIServiceCounter;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.v3.client.UDDIService;
import org.uddi.repl_v3.ChangeRecordIDType;
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
        private int maxTries = AppConfig.getConfiguration().getInt(Property.JUDDI_NOTIFICATION_MAX_TRIES, 3);
        private long badListResetInterval = AppConfig.getConfiguration().getLong(Property.JUDDI_NOTIFICATION_LIST_RESET_INTERVAL, 1000l * 3600); //one hour
        private Boolean alwaysNotify = false;
        private Date desiredDate = null;
        private int lastUpdateCounter;
        private UDDIServiceCounter serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(UDDIReplicationImpl.class);
        private static Map<String, Integer> badNotifications = new ConcurrentHashMap<String, Integer>();
        private static Date lastBadNotificationReset = new Date();

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
                queue.add(change);
        }
        static Queue queue;

        public synchronized void run() {
                //TODO stuff
                log.info("Replication thread trigger");
                if (queue==null)
                        queue = new ConcurrentLinkedQueue();
                while (!queue.isEmpty()) {
                        log.info("Notifying nodes of change records");
                        Object j = queue.poll();
                        List<String> endpoints = new ArrayList<String>(); //TODO getReplicationEndpoints
                        for (int i = 0; i < endpoints.size(); i++) {
                                UDDIReplicationPortType x = new UDDIService().getUDDIReplicationPort();
                                ((BindingProvider)x).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoints.get(i));
                                NotifyChangeRecordsAvailable req = new NotifyChangeRecordsAvailable();
                                String node="UNKNOWN";
                                try {
                                        node = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                                } catch (ConfigurationException ex) {
                                        log.fatal(ex);
                                }
                                req.setNotifyingNode(node);
                                HighWaterMarkVectorType highWaterMarkVectorType = new HighWaterMarkVectorType();
                                String nextWatermark = ""; //TODO get current watermark + 1 toString()
                                //TODO save watermark
                                highWaterMarkVectorType.getHighWaterMark().add(new ChangeRecordIDType(node, 1L));
                                req.setChangesAvailable(highWaterMarkVectorType);
                                try {
                                        x.notifyChangeRecordsAvailable(req);
                                } catch (Exception ex) {
                                        log.warn("Unable to send change notification to ");
                                }
                        }
                }
        }
}
