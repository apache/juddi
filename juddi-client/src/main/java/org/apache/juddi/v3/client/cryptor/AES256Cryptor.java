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
 * AES 256 bit encryption. <h1> Requires Unlimited Strength Java Cryptographic
 * Extensions</h1>
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class AES256Cryptor extends AESCryptorAbstract {

        /**
         * Constructor for DefaultCryptor.
         */
        public AES256Cryptor()
             throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
                super();
        }

        @Override
        protected String getKey() {
                //JUDDI-808
                String key = CryptorFactory.loadKeyFromFile("AES256Cryptor");
                if (key != null) {
                        return key;
                } else {
                        return "ef057ce3abd9dd9a161a2888c9d7025f104a42eceda5947b083186e7190fcc46";
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
                return GEN(256);

        }
}
