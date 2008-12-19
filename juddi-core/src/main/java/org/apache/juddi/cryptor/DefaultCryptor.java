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

package org.apache.juddi.cryptor;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Anou Manavalan
 */
public class DefaultCryptor implements Cryptor
{
  private PBEKeySpec pbeKeySpec = null;
  private PBEParameterSpec pbeParamSpec = null;
  private SecretKeyFactory keyFac = null;
  private SecretKey pbeKey = null;

  // Salt
  private byte[] salt = {
    (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
    (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
  };

  // Iteration count
  private int count = 20;

  /**
   * Constructor for DefaultCryptor.
   */
  public DefaultCryptor()
    throws NoSuchAlgorithmException,InvalidKeySpecException
  {
    // Create PBE parameter set
    pbeParamSpec = new PBEParameterSpec(salt,count);
    pbeKeySpec = new PBEKeySpec("saagar".toCharArray());
    keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    pbeKey = keyFac.generateSecret(pbeKeySpec);
  }

  /**
   * Encrypt the string
   */
  private byte[] crypt(int cipherMode,byte[] text)
    throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException

  {
    // Create PBE Cipher
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

    // Initialize PBE Cipher with key and parameters
    pbeCipher.init(cipherMode,pbeKey,pbeParamSpec);

    //byte[] text = str.getBytes();

    // Encrypt/Decrypt the string
    byte[] cryptext = pbeCipher.doFinal(text);

    return cryptext;
  }

  /**
   * Encrypt the string
   */
  public String encrypt(String str)
    throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException
  {
    byte[] encs = crypt(Cipher.ENCRYPT_MODE,str.getBytes());
    encs = Base64.encodeBase64(encs);
    return new String(encs);
  }
}