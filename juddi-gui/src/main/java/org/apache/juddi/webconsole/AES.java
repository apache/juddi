/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.webconsole;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <summary> This program uses a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes.</summary> The reinstantiated key
 * is used to initialize a AES cipher for encryption and decryption. source :
 * http://java.sun.com/developer/technicalArticles/Security/AES/AES_v1.html
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class AES {

    public static final String logname = "org.apache.juddi.gui";
    public static final Log log = LogFactory.getLog(logname);

    /**
     * Turns array of bytes into string
     *
     * @param buf	Array of bytes to convert to hex string
     * @return	Generated hex string
     */
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
  
    /**
     * generates an AES based off of the selected key size
     *
     * @param keysize
     * @return may return null if the key is not of a supported size by the
     * current jdk
     */
    public static String GEN(int keysize) {
        KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(keysize);
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return asHex(raw);
        } catch (Exception ex) {
            log.fatal("error generating key", ex);
        }
        return null;
    }

    /**
     * Generate a new AES 256 bit encryption key. Once generated, this key can
     * be used to replace the default key.
     *
     * @return
     */
    public static String GEN() {
        return GEN(256);
    }

    static String EN(String cleartext, String key) throws Exception {
        byte[] raw =//skey.getEncoded();
                hexToBytes(key); //
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(cleartext.getBytes());
        return asHex(encrypted);
    }

    
    static String DE(String ciphertext, String key) throws Exception {
        byte[] raw =//skey.getEncoded();
                hexToBytes(key); //
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(hexToBytes(ciphertext));
        return new String(original);
    }

    /**
     * return true is the supplied key is a valid aes key
     *
     * @param key
     * @return
     */
    public static boolean ValidateKey(String key) {
        try {
            String src = "abcdefghijklmopqrstuvwxyz123567890!@#$%^&*()_+{}|:\">?<,";
            String x = EN(src, key);
            String y = DE(x, key);
            //if the sample text is encryptable and decryptable, and it was actually encrypted
            if (y.equals(src) && !x.equals(y)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            log.info("Key validation failed!", ex);
            return false;
        }
    }

    /**
     * encrypts a password using AES Requires the Unlimited Strength Crypto
     * Extensions
     *
     * @param clear
     * @return
     */
    public static String Encrypt(String clear, String key) {
        if ((clear == null || clear.length() == 0)) {
            return "";
        }
        try {
            return AES.EN(clear, key);
        } catch (Exception ex) {
            log.fatal("Cannot encrypt sensitive information! Check to make sure the unlimited strength JCE is installed " + ex.getMessage());
        }
        return "";
    }

    /**
     * Decrypts a password or other sensitive data If the parameter is null or
     * empty, an empty string is returned. If the parameter is not encrypted or
     * was encrypted using a different key or it fails to decrypt, the original
     * text is returned.
     *
     * @param cipher
     * @return
     */
    public static String Decrypt(String cipher, String key) {
        if ((cipher == null || cipher.length() == 0)) {
            return "";
        }
        try {
            return AES.DE(cipher, key);
        } catch (Exception ex) {
            log.fatal("trouble decrypting data, check to make sure the unlimited strength JCE is installed. If this error occured during deployment, I'll automatically try a smaller key size. " + ex.getMessage());
        }
        return cipher;

    }
}
