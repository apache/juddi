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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alex O'Ree
 */
public class AES128CryptorTest {
        
        public AES128CryptorTest() {
        }

        /**
         * Test of getKey method, of class AES128Cryptor.
         */
        @Test
        public void testGetKey() {
                try {
                        System.out.println("getKey");
                        AES128Cryptor instance = new AES128Cryptor();
                        String result = instance.getKey();
                        assertNotNull(result);
                } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(AES128CryptorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                        Logger.getLogger(AES128CryptorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                        Logger.getLogger(AES128CryptorTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                        Logger.getLogger(AES128CryptorTest.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        /**
         * Test of encrypt method, of class AES128Cryptor.
         */
        @Test
        public void testEncrypt() throws Exception {
                System.out.println("encrypt");
                String str = "test";
                AES128Cryptor instance = new AES128Cryptor();
                String result = instance.encrypt(str);
                assertNotEquals(str, result);
        }

        /**
         * Test of decrypt method, of class AES128Cryptor. EXTERNAL KEY
         */
        @Test
        public void testExternalKey() throws Exception {
                System.out.println("testExternalKey");
                AES128Cryptor instance = new AES128Cryptor();
                String result = instance.getKey();
                System.getProperties().put("juddi.encryptionKeyFile.AES128Cryptor", "./src/test/resources/JUDDI-808/aes128.key");
                String expResult = instance.getKey();
                System.getProperties().remove("juddi.encryptionKeyFile.AES128Cryptor");
                assertNotEquals("loading of external key failed", expResult, result);
                String enc = instance.decrypt(instance.encrypt("test"));
                assertEquals(enc, "test");
        }
        
        /**
         * Test of decrypt method, of class AES128Cryptor. 
         */
        @Test
        public void testDecrypt() throws Exception {
                System.out.println("testDecrypt");
                String str = "test";
                AES128Cryptor instance = new AES128Cryptor();
                String expResult = "test";
                String result = instance.decrypt(instance.encrypt(str));
                assertEquals(expResult, result);
        }

        /**
         * Test of newKey method, of class AES128Cryptor.
         */
        @Test
        public void testNewKey() throws Exception{
                System.out.println("newKey");
                AES128Cryptor instance = new AES128Cryptor();
                String result = instance.newKey();
                assertNotNull(result);
        }
        
}
