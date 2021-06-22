/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi;

import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.replication.ReplicationNotifier;
import org.apache.juddi.subscription.SubscriptionNotifier;

public class Registry {

        private static Registry registry = null;
        private static Log log = LogFactory.getLog(Registry.class);
        private static SubscriptionNotifier subscriptionNotifier = null;
        private static ReplicationNotifier replicationNotifier = null;

        /**
         * Singleton.
         */
        private Registry() {
                super();
        }

        /**
         * Stops the registry.
         *
         * @throws ConfigurationException
         */
        public synchronized static void stop() throws ConfigurationException {
                if (registry != null) {
                        log.info("Stopping jUDDI registry...");
                        if (subscriptionNotifier != null) {
                                log.info("Shutting down SubscriptionNotifier");
                                subscriptionNotifier.cancel();
                                subscriptionNotifier = null;
                        }
                        if (replicationNotifier != null) {
                                replicationNotifier.cancel();
                                replicationNotifier = null;
                        }
                        registry = null;
                        log.info("jUDDI shutdown completed.");
                }
        }

        /**
         * Starts the registry.
         *
         * @throws ConfigurationException
         */
        public synchronized static void start() throws ConfigurationException {
                if (registry == null) {
                        log.info("Starting jUDDI registry...This is node " + AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID, ""));
                        registry = new Registry();
                        replicationNotifier = new ReplicationNotifier();
                        AppConfig.triggerReload();
                        if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_SUBSCRIPTION_NOTIFICATION, true)) {
                                subscriptionNotifier = new SubscriptionNotifier();
                        }
                        log.info("jUDDI registry started successfully.");
                }
        }

}
