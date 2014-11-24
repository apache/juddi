/*
 * Copyright 2014 The Apache Software Foundation.
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
 */

package org.apache.juddi.replication;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.api.impl.API_141_JIRATest;
import org.apache.juddi.api.impl.UDDIReplicationImpl;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import org.uddi.v3_service.UDDIReplicationPortType;

/**
 *
 * @author Alex O'Ree
 */
public class ReplicationNotifierTest {
        private static Log logger = LogFactory.getLog(API_141_JIRATest.class);
        static UDDIReplicationPortType repl = new UDDIReplicationImpl();
        static ReplicationNotifier notifier = null;
        protected static String authInfoJoe = null;
        protected static String authInfoMary = null;
        protected static String authInfoSam = null;
        protected static String authInfoRoot = null;
        protected static String authInfoUDDI = null;
        @AfterClass
        public static void stopManager() throws ConfigurationException {
                Registry.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                Registry.start();
                notifier = new ReplicationNotifier();

        }
        
       
        
        /**
         * Test of getNewChangeRecord method, of class ReplicationNotifier.
         */
        @Test
        public void testGetNewChangeRecord() throws Exception {
                System.out.println("getNewChangeRecord");
                
 
                     
           
                
        }
        
}
