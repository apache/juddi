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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author Anou Manavalan
 */
public interface Cryptor
{
  /**
   * Encrypt the string, if unable to encrypt, return null
   */
  String encrypt(String str)
    throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException;
           /**
            * decrypts the string
             * @param str
             * @return, if the password can be decrypted, the decrypted value is returned, otherwise the original value is returned<br>
             * In the event that decryption fails, the error message must be logged.
            * @throws NoSuchPaddingException
            * @throws NoSuchAlgorithmException
            * @throws InvalidAlgorithmParameterException
            * @throws InvalidKeyException
            * @throws IllegalBlockSizeException
            * @throws BadPaddingException 
            */
    public String decrypt(String str)  throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException;

    

  
}