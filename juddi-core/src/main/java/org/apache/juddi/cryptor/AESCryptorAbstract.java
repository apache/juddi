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
import javax.crypto.spec.SecretKeySpec;

/**
 * AES bit encryption
 *
 * @author Alex O'Ree
 */
public abstract class AESCryptorAbstract implements Cryptor {

    private static byte[] hexToBytes(String s) {
        //return s.getBytes();
        return hexToBytes(s.toCharArray());
    }
    private static final char[] kDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
        'b', 'c', 'd', 'e', 'f'};

    private static byte[] hexToBytes(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127) {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

    protected  String getKey() {
        return null;
    }

    /**
     * Constructor for DefaultCryptor.
     */
    public AESCryptorAbstract()
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        byte[] raw =//skey.getEncoded();
                hexToBytes(getKey()); //
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
    }

    /**
     * Encrypt the string
     */
    public String encrypt(String cleartext)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        byte[] raw = hexToBytes(getKey()); //
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(cleartext.getBytes());
        return asHex(encrypted);
    }
    
      /**
     * Encrypt the string
     */
    public String decrypt(String str)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        byte[] raw = hexToBytes(getKey()); //
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(hexToBytes(str));
        
        return new String(original);
    }

    private static String asHex(byte buf[]) {
        //return new String(buf);
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }
}