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
import org.apache.juddi.rmi.JNDIRegistration;
import org.apache.juddi.rmi.RMIRegistration;
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
                        if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_JNDI_REGISTRATION, false)) {
                                try {
                                        JNDIRegistration.getInstance().unregister();
                                } catch (NamingException e) {
                                        log.error("Unable to Register jUDDI services with JNDI. " + e.getMessage(), e);
                                }
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
                        log.info("Starting jUDDI registry...");
                        registry = new Registry();
                        replicationNotifier = new ReplicationNotifier();
                        AppConfig.triggerReload();
                        if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_SUBSCRIPTION_NOTIFICATION, true)) {
                                subscriptionNotifier = new SubscriptionNotifier();
                        }
                        if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_JNDI_REGISTRATION, false)) {
                                try {
                                        int port = AppConfig.getConfiguration().getInteger(Property.JUDDI_RMI_PORT, 0);
                                        JNDIRegistration.getInstance().register(port);
                                } catch (NamingException e) {
                                        log.error("Unable to Register jUDDI services with JNDI. " + e.getMessage(), e);
                                }
                        }
                        if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_RMI_REGISTRATION, false)) {
                                try {
                                        int rmiport = AppConfig.getConfiguration().getInteger(Property.JUDDI_RMI_REGISTRY_PORT, 1099);
                                        int port = AppConfig.getConfiguration().getInteger(Property.JUDDI_RMI_PORT, 0);
                                        RMIRegistration.getInstance(rmiport).register(port);
                                } catch (Exception e) {
                                        log.error("Unable to Register jUDDI services with RMI Registry. " + e.getMessage(), e);
                                }
                        }

                        log.info("jUDDI registry started successfully.");
                }
        }

}
