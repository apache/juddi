/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * This call is a simple test class that shows how to encrypt stuff in Java
 * using 3DES
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class DES {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    static private KeySpec ks;
    static private SecretKeyFactory skf;
    static private Cipher cipher;
    static byte[] arrayBytes;
    static private String myEncryptionKey;
    static private String myEncryptionScheme;
    static SecretKey key;

    public DES() throws Exception {
    }

    public String encrypt(String clear) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
         String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = clear.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String encryptedString = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(str.getBytes());
            byte[] clear = cipher.doFinal(encryptedText);
            encryptedString = new String(Base64.encodeBase64(clear));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public static void main(String[] args) throws Exception {
        DES des = new DES();
        KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance(DESEDE_ENCRYPTION_SCHEME);
            kgen.init(168);
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            myEncryptionKey = new String(Base64.encodeBase64(raw));
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);

            System.out.println(new String(Base64.encodeBase64(raw)));
            System.out.println(des.encrypt("password"));
            System.out.println(des.decrypt(des.encrypt("password")));
        } catch (Exception ex) {
            ex.printStackTrace();;
        }

    }
}
