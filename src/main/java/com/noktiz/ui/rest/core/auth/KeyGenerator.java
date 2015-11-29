package com.noktiz.ui.rest.core.auth;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by hassan on 09/11/2015.
 */
public class KeyGenerator {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
//        generate key
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                RSAPrivateKeySpec.class);

        final String publicKeyFile;
        final String privateKeyFile;
        if (args.length > 0)
            publicKeyFile = args[0];
        else
            publicKeyFile = "public.key";
        if (args.length > 1)
            privateKeyFile = args[1];
        else
            privateKeyFile = "private.key";
        saveToFile(publicKeyFile, pub.getModulus(),
                pub.getPublicExponent());
        saveToFile(privateKeyFile, priv.getModulus(),
                priv.getPrivateExponent());
        testKey(pub.getModulus(), pub.getPublicExponent()
                , priv.getModulus(), pub.getPublicExponent());
    }

    private static void testKey(BigInteger modulus, BigInteger publicExponent, BigInteger privateModulus, BigInteger privatePublicExponent) throws InvalidKeyException, NoSuchAlgorithmException {
//        final RSAPublicKeyImpl rsaPublicKey = new RSAPublicKeyImpl(modulus, publicExponent);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        final RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(privateModulus, new BigInteger(getPublicKeyExponent()));
//        RSAPrivateKey privateKey = (RSAPrivateKey)
//                keyFactory.generatePrivate(rsaPrivateKeySpec);
    }

    public static void saveToFile(String fileName,
                                  BigInteger mod, BigInteger exp) throws IOException {
        PrintStream out = new PrintStream(fileName);
        try {
            out.println(mod);
            out.println(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            out.close();
        }
    }
}
