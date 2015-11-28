/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import com.noktiz.domain.entity.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Hossein
 */
public class PasswordManager {

    private static final String charsetName = "UTF-8";
    private static SecureRandom random = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());

    public static void changePassword(User user, String password) {

        String namak = RandomStringUtils.random(63,false,true) ;
        String bigpassword = namak + password;
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-512");
            byte[] digest = instance.digest(bigpassword.getBytes());
            user.getPersonalInfo().setPassword(new String(digest, charsetName));
//            System.err.println("here");
//            System.out.println("here");
//            System.err.println(charsetName);
//            System.out.println(charsetName);
//            Logger.getRootLogger().error(charsetName);
            user.getPersonalInfo().setNamak(namak);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(PasswordManager.class).fatal(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public static boolean authenticate(User user, String password) {
        if(user.getPersonalInfo().getPassword()==null)
            return false;
        String bigpassword = user.getPersonalInfo().getNamak() + password;
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-512");
            String digest = new String(instance.digest(bigpassword.getBytes()), charsetName);
            if(digest.equals(user.getPersonalInfo().getPassword())){
                return true;
            }
            return false;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordManager.class).fatal(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(PasswordManager.class).fatal(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static Result verifyPassword(String password) {
        if(password.length()<6){
            return new Result(new Result.Message("message.password_limits", Result.Message.Level.error),false);
        }
        return new Result(true);
    }
}
