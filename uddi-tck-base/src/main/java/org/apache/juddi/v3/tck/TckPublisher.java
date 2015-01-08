/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.tck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class TckPublisher {

        private static Properties tckProperties = new Properties();
        public final static String JOE_PUBLISHER_XML = "uddi_data/joepublisher/publisher.xml";
        //public final static String TMODEL_PUBLISHER_XML = "uddi_data/tmodels/publisher.xml";
        public final static String SAM_SYNDICATOR_XML = "uddi_data/samsyndicator/publisher.xml";
        public final static String MARY_PUBLISHER_XML = "uddi_data/marypublisher/publisher.xml";

        private static Log logger = LogFactory.getLog(TckPublisher.class);

        static {
                String s = System.getProperty("tck.properties");
                InputStream inputSteam = null;
                try {
                        File f = null;
                        if (s != null && s.length() != 0) {
                                f = new File(s);
                        }
                        if (f == null || !f.exists()) {
                                f = new File("tck.properties");
                        }
                        if (f.exists()) {

                                inputSteam = new FileInputStream(f);
                                logger.info("Loading tck.properties from " + f.getAbsolutePath());
                        } else {
                                inputSteam = TckPublisher.class.getResourceAsStream("/tck.properties");
                                logger.info("Loading tck.properties as a classpath resource, probable within uddi-tck-base.jar");
                        }
                        tckProperties.load(inputSteam);
                } catch (IOException ioe) {
                        ioe.printStackTrace();
                } finally {
                        if (inputSteam != null) {
                                try {
                                        inputSteam.close();
                                } catch (Exception ex) {
                                }
                        }
                }
        }
        
        /**
         * is enabled for UDDI v3 tests
         * @return 
         */
        public final static boolean isEnabled(){
             return Boolean.parseBoolean(tckProperties.getProperty("uddiv3.enabled", "false"));
        }

        public final static String getRootPublisherId() {
                return tckProperties.getProperty(Property.ROOT_PUBLISHER);
        }

        public final static String getRootPassword() {
                return tckProperties.getProperty(Property.ROOT_PASSWORD);
        }

        public final static String getUDDIPublisherId() {
                return tckProperties.getProperty(Property.UDDI_PUBLISHER);
        }

        public final static String getUDDIPassword() {
                return tckProperties.getProperty(Property.UDDI_PASSWORD);
        }

        public final static String getJoePublisherId() {
                return tckProperties.getProperty(Property.JOE_PUBLISHER);
        }

        public final static String getJoePassword() {
                return tckProperties.getProperty(Property.JOE_PASSWORD);
        }

        public final static String getTModelPublisherId() {
                return tckProperties.getProperty(Property.TMODEL_PUBLISHER);
        }

        public final static String getTModelPassword() {
                return tckProperties.getProperty(Property.TMODEL_PASSWORD);
        }

        public final static String getSamPublisherId() {
                return tckProperties.getProperty(Property.SAM_PUBLISHER);
        }

        public final static String getSamPassword() {
                return tckProperties.getProperty(Property.SAM_PASSWORD);
        }

        public final static String getMaryPublisherId() {
                return tckProperties.getProperty(Property.MARY_PUBLISHER);
        }

        public final static String getMaryPassword() {
                return tckProperties.getProperty(Property.MARY_PASSWORD);
        }

        public final static String getRiftSawPublisherId() {
                return tckProperties.getProperty(Property.RIFTSAW_PUBLISHER);
        }

        public final static String getRiftSawPassword() {
                return tckProperties.getProperty(Property.RIFTSAW_PASSWORD);
        }

        public static boolean isUDDIAuthMode() {
                String x = tckProperties.getProperty("auth_mode");
                if (x != null && x.equalsIgnoreCase("uddi")) {
                        return true;
                }
                return false;
        }

        public static boolean isReplicationEnabled() {
                String x = tckProperties.getProperty("replication.enabled");
                if (x != null && x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isInquiryRestEnabled() {
                String x = tckProperties.getProperty("rest.enabled");
                if (x != null && x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isValueSetAPIEnabled() {
                String x = tckProperties.getProperty("vsv.enabled");
                if (x != null && x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isSubscriptionEnabled() {
                String x = tckProperties.getProperty("sub.enabled");
                if (x != null && x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isCustodyTransferEnabled() {
                String x = tckProperties.getProperty("transfer.enabled");
                if (x != null && x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isJUDDI() {
                String x = tckProperties.getProperty("isJuddi");
                if (x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isRMI() {
                String x = tckProperties.getProperty("rmi.enabled");
                if (x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isLoadTest() {
                String x = tckProperties.getProperty("loadtest.enable");
                if (x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static boolean isBPEL() {
                String x = tckProperties.getProperty("bpel.enable");
                if (x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }

        public static int getMaxLoadServices() {
                String x = tckProperties.getProperty("loadtest.maxbusinesses");
                if (x != null) {
                        try {
                                return Integer.parseInt(x);
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                }

                return 1100;
        }

        public static Properties getProperties() {
                return tckProperties;
        }

        /**
         * time in seconds, default is 60
         *
         * @return the subscription timeout in seconds or the default is not defined
         */
        public static int getSubscriptionTimeout() {
                String x = tckProperties.getProperty("sub.timeout");
                if (x != null) {
                        try {
                                return Integer.parseInt(x);
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                }

                return 60;
        }

        public static boolean isSMTPEnabled() {
                String x = tckProperties.getProperty("smtp.notify.enabled");
                if (x.equalsIgnoreCase("true")) {
                        return true;
                }
                return false;
        }
}
