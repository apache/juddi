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

import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class CryptoConfigTest {

    @Test()
    public void EncryptedCredentialsAES128Test() throws Exception {
        UDDIClient clerkManager = new UDDIClient("META-INF/uddi3-enc-aes128.xml");
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
