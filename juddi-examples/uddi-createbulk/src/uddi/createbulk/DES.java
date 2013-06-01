/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uddi.createbulk;

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
 *
 * @author Alex O'Ree
 */
public class DES {
  private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;

    public DES() throws Exception {
        myEncryptionKey = "rioTEBCe/RAHRs6tTyYxDqettnVbZA6z";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
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
    
    public static void main(String[] args) throws Exception{
        DES des = new DES();
          /*KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance(DESEDE_ENCRYPTION_SCHEME);
            kgen.init(168);
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
             System.out.println(new String(Base64.encodeBase64(raw)));
        } catch (Exception ex) {
            ex.printStackTrace();;
        }*/
        System.out.println(des.encrypt("test"));
    }
}
