/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
    return new String(encs);
  }

  public byte[] encrypt(byte[] bytes)
    throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException
  {
    return crypt(Cipher.ENCRYPT_MODE, bytes);
  }

  /**
   * Decrypt the string
   */
  public String decrypt(String str)
    throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException
  {
    byte[] decs = crypt(Cipher.DECRYPT_MODE,str.getBytes());
    return new String(decs);
  }


  public byte[] decrypt(byte[] bytes)
    throws  NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException
  {
    return crypt(Cipher.DECRYPT_MODE,bytes);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    DefaultCryptor cryptor = new DefaultCryptor();
    String encryptedText = cryptor.encrypt("password");
    System.out.println("EnCrypted text [" + encryptedText + "]");

    DefaultCryptor cryptor2 = new DefaultCryptor();
    String decryptedText = cryptor2.decrypt(encryptedText);
    System.out.println("DeCrypted text " + decryptedText);
  }
}