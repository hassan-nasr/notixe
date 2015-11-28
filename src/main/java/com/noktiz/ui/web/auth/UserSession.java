/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.auth;

import com.noktiz.domain.Utils.CipherUtils;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.i18n.Local;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 *
 * @author hossein
 */
public class UserSession extends AuthenticatedWebSession {

    private final String staySingInSecret = "this is a sample secret456789012";
    UserFacade user;
    Integer timezoneOffset=0;
    private long staySignedInTime=2*7*24*60*60*1000;
    private String remember = "remember";
    Result result;
    public UserSession(Request request) {
        super(request);
    }

    @Override
    public UserRoles getRoles() {
        if (isSignedIn())
        {
            return user.getUserRoles();
        }
        return null;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

//    @Override
//    public boolean isSignedIn() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    public UserFacade getUser() {
        return user;
    }

    public void setUser(UserFacade user) {
        this.user = user;
    }

    
    /**
     *
     * @return
     
    @Override
    public boolean isSignedIn() {
        return !(user == null);
    }
    */

    @Override
    public boolean authenticate(String userName, String password) {
        ResultWithObject<UserFacade> load = UserFactory.loadUserWithEmail(userName,true,true);
        UserFacade user = load.object;
        if (user != null && user.doesPasswordMatch(password)) {
            this.user = user;
            user.getPersonalInfo();
            user.resetLoginAttempt();
            result= new Result(new ArrayList<Result.Message>(), Boolean.TRUE);
            return true;
        } else {
            result = new Result();
            result.result = false;
            if(user !=null){
                if(user.addWrongLoginAttempt())
                    result.messages.add(new Result.Message("message.AccountIsLockedTryAgainMessage", Result.Message.Level.error));
                else
                result.messages.add(new Result.Message("message.invalid_password_again_forget", Result.Message.Level.error));
            }
            else
                result=load;

            return false;
        }
    }

    public final boolean signIn(UserFacade user)
    {
        if(user.getUser()==null)
            return false;
        if(!user.getUser().getActive())
            return false;
        this.user=user;
        bind();
        signIn(true);
        prepareSignedInUser();
        return true;
    }
    public boolean signIn(String username, String password, Boolean staySignedIn, WebResponse response){
        boolean ret = signIn(username, password);
        if(ret)
            prepareSignedInUser();
        if(ret && staySignedIn){
            staySignedIn(response);
        }
        return ret;

    }

    private void prepareSignedInUser() {
        setLocal();
    }

    public void setLocal() {
        Local local = getUser().getPersonalInfo().getLocal();
        Locale locale;
        if(local == null)
            locale=new Locale("en","US");
        else {
            String language = local.getLanguage();
            String country = local.getCountry();

            if (country == null)
                locale = new Locale(language);
            else
                locale = new Locale(language, country);
        }
        setLocale(locale);
    }

    private void staySignedIn(WebResponse response) {
        String s= getUser().getUser().getEmail() + "#"+(new Date().getTime()+staySignedInTime)+"#"+RandomStringUtils.random(30,true,true);
        String encryptedString = CipherUtils.encrypt(s);
        Cookie cookie = null;
        try {
            cookie = new Cookie(remember, URLEncoder.encode(encryptedString, "UTF8"));
            cookie.setMaxAge((int) (staySignedInTime/1000));
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(this.getClass()).error(e);
        }

    }

//    private String encrypt(String s, String aeskey){
//        try {
//            Cipher c = Cipher.getInstance("AES");
//            SecretKeySpec key = new SecretKeySpec(aeskey.getBytes("UTF8"),"AES");
//            c.init(Cipher.ENCRYPT_MODE, key);
//            return Base64.encodeBase64String(c.doFinal(s.getBytes("UTF8")));
//        } catch (UnsupportedEncodingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException e) {
//            Logger.getLogger(this.getClass()).error(e);
//        }
//        return null;
//    }
//    private String decrypt(String s, String aeskey) {
//        try {
//            Cipher c = Cipher.getInstance("AES");
//            SecretKeySpec key = new SecretKeySpec(aeskey.getBytes("UTF8"),"AES");
//            c.init(Cipher.DECRYPT_MODE, key);
//            return new String(c.doFinal(Base64.encodeBase64String(s.getBytes("UTF8")).getBytes()),"UTF8");
//        } catch (UnsupportedEncodingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
//            Logger.getLogger(this.getClass()).error(e);
//        }
//        return null;
//    }

    public boolean signInWithStaySignedInCookie(WebRequest w,WebResponse webResponse){
        try {

            Cookie cookie = w.getCookie(remember);
            String decrypted = CipherUtils.decrypt(URLDecoder.decode(cookie.getValue(), "UTF8"));
            String[] parts = decrypted.split("#", 3);
            if (parts.length != 3)
                return false;
            if (!(Long.parseLong(parts[1]) > new Date().getTime())) {
                return false;
            }
            User user1 = User.loadUserWithEmail(parts[0], false);
            boolean ret = signIn(new UserFacade(user1));
            if (ret)
                staySignedIn(webResponse);
            return ret;
        }catch (Exception e){
            return false;
        }
    }

    public void invalidate(WebResponse response) {
        super.invalidate();
        Cookie cookie = new Cookie(remember, "");
        cookie.setPath("/");
        response.clearCookie(cookie);
    }
    public Result popLastResult(){
        Result ret = result;
        result = null;
        return ret;
    }
}
