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

package org.apache.juddi.v3.client.cryptor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alex O'Ree
 */
public class TripleDESCrytorTest {
        
        public TripleDESCrytorTest() {
        }

        /**
         * Test of encrypt method, of class TripleDESCrytor.
         */
        @Test
        public void testEncrypt() throws Exception {
                System.out.println("encrypt");
                String clear = "test";
                TripleDESCrytor instance = new TripleDESCrytor();
                String expResult = "test";
                String result =instance.decrypt(instance.encrypt(clear));
                assertEquals(expResult, result);
        }
        
        /**
         * Test of decrypt method, of class AES128Cryptor. EXTERNAL KEY
         */
        @Test
        public void testExternalKey() throws Exception {
                System.out.println("testExternalKey");
                TripleDESCrytor instance = new TripleDESCrytor();
                String defaultKey=instance.encrypt("test");
                
                System.getProperties().put("juddi.encryptionKeyFile.TripleDESCrytor", "./src/test/resources/JUDDI-808/3des.key");
                instance = new TripleDESCrytor();
                String externalKey=instance.encrypt("test");
                
                System.getProperties().remove("juddi.encryptionKeyFile.TripleDESCrytor");
                assertNotEquals("loading of external key failed", defaultKey, externalKey);
                String str=instance.decrypt(externalKey);
                assertEquals(str, "test");
                
                
        }

        /**
         * Test of newKey method, of class TripleDESCrytor.
         */
        @Test
        public void testNewKey() throws Exception{
                System.out.println("newKey");
                TripleDESCrytor instance = new TripleDESCrytor();
                String result = instance.newKey();
                assertNotNull(result);
        }
        
}
