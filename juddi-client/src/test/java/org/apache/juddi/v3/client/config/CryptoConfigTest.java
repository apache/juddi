/*
 * Copyright 2013 The Apache Software Foundation.
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

import org.apache.juddi.v3.client.crypto.CryptorFactory;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Alex O'Ree
 */
public class CryptoConfigTest {

    @Test()
    public void EncryptedCredentialsAES128Test() throws Exception {
        UDDIClient clerkManager = new UDDIClient("META-INF/uddi3-enc-aes128.xml");
        // register the clerkManager with the client side container
        UDDIClientContainer.addClient(clerkManager);
        // a ClerkManager can be a client to multiple UDDI nodes, so 
        // supply the nodeName (defined in your uddi.xml.
        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = clerkManager.getTransport("default");
        // Now you create a reference to the UDDI API
        UDDIClerk c = clerkManager.getClerk("default");
        UDDIClerk c2 = clerkManager.getClerk("medroot");
        Assert.assertNotSame(c.getPassword(), c2.getPassword());
        Assert.assertEquals("root", CryptorFactory.getCryptor(c.getCryptoProvider()).decrypt(c.getRawPassword()));
        Assert.assertEquals("root", c.getPassword());

        Assert.assertEquals("password", CryptorFactory.getCryptor(c2.getCryptoProvider()).decrypt(c2.getRawPassword()));
        Assert.assertEquals("password", c2.getPassword());


    }
}
