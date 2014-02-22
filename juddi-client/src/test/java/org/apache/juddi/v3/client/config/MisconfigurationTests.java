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

package org.apache.juddi.v3.client.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Alex O'Ree
 */
public class MisconfigurationTests {

        @Test(expected = ConfigurationException.class)
        public void TestMissingClerk() throws ConfigurationException{
                UDDIClient client = new UDDIClient("META-INF/configtests.xml");
                UDDIClerk clerk = client.getClerk("missingClerk");
                Assert.fail("this should have thrown an exception");
        }
        
        @Test(expected = ConfigurationException.class)
        public void TestMissingTransportClass() throws ConfigurationException{
                UDDIClient client = new UDDIClient("META-INF/configtests.xml");
                UDDIClerk clerk = client.getClerk("missingTransport");
                Transport transport = client.getTransport("missingTransport");
                Assert.fail("this should have thrown an exception");
        }
}
