/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.createbulk;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author Alex O'Ree
 */
public class AES {

    public static String GEN(int keysize) {
        KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(keysize);
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return asHex(raw);
        } catch (Exception ex) {

        }
        return null;
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
    //default key
    private final static String something2 = "dde284c781d60ca0b56c4b23eec85217951dc99869402abd42c7dcc9080d60aa";
    
    public static void main(String[] args){
        System.out.println(GEN(128));
    }
}
