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
package org.apache.juddi.v3.client.cryptor;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * AES 128 bit encryption
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class AES128Cryptor extends AESCryptorAbstract {

        /**
         * Constructor for DefaultCryptor.
         */
        public AES128Cryptor()
             throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
                super();
        }

        @Override
        protected String getKey() {
                //JUDDI-808
                String key = CryptorFactory.loadKeyFromFile("AES128Cryptor");
                if (key != null) {
                        return key;
                } else {
                        return "72d93747ba0162f2f2985f5cb3e24b30";
                }
        }

        @Override
        public String encrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
                return super.encrypt(str);
        }

        @Override
        public String decrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
                return super.decrypt(str);
        }

        @Override
        public String newKey() {
                return GEN(128);
        }

}
