package com.noktiz.domain.Utils;

/**
 * Created by hasan on 31 January 15.
 */

import org.apache.wicket.util.crypt.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


public class CipherUtils
{

    private static byte[] key = {
            0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
    };//"thisIsASecretKey";
    private final String charset = "utf8";

    public static String encrypt(String strToEncrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES");
            final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final String encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
            return encryptedString;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

    public static String decrypt(String strToDecrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES");
            final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt.getBytes("utf8"))));
            return decryptedString;
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return null;
    }


    public String encryptAsymetric(String strToEncrypt, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            final String encryptedString = new String(org.apache.commons.codec.binary.Base64.encodeBase64(cipher.doFinal(strToEncrypt.getBytes(charset))), charset);
            return encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String decryptAsymetric(String strToDecrypt, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            final String decryptedString = new String(cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(strToDecrypt.getBytes(charset))));
            return decryptedString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}