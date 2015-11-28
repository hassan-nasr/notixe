/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.social.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.model.Person;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.domain.model.image.ImageConversion;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.google.GetGooglePermission;
import com.noktiz.ui.web.google.GoogleInfoParseError;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 *
 * @author sina
 */
public class GoogleUtils {

    protected static final String CLIENT_ID = SystemConfigManager.getCurrentConfig().getProperty("GoogleClientID");
    protected static final String CLIENT_SECRET = SystemConfigManager.getCurrentConfig().getProperty("GoogleClientSecret");
    protected static final HttpTransport TRANSPORT = new NetHttpTransport();
    protected static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    private static final long MAX_EXPIRE_TIME = 500;

    public void connectUser(UserFacade userFacade, String email, String id, String access_token, Person me) throws ResultException {

//        email = EmailAddressUtils.normalizeEmail(email);
//        User duplicateUser = User.loadUserWithEmail(email,false);
//        if(duplicateUser != null){
//            if(userFacade.getUser().getId()==null) {
//                userFacade.setUser(duplicateUser);
//                duplicateUser.setActive(true);
//            }
//            if(!duplicateUser.equals(userFacade.getUser())){
//                throw new ResultException(new Result(new Result.Message("this account is already connected to another user", Result.Message.Level.error), false), null);
//            }
//        }
//        else {
            Long duplicate = User.findIdWithEmailId(email);
            Long googleDuplicate = User.findIdWithGoogleId(id);

            if (userFacade.getUser().getId() == null) {
                if (duplicate != null && googleDuplicate != null && duplicate.equals(googleDuplicate)) {
                    User user = User.loadUserWithEmailId(email, false);
                    userFacade.setUser(user);
                    user.setActive(true);
                } else if (duplicate != null && googleDuplicate != null && !duplicate.equals(googleDuplicate)) {
                    throw new ResultException(new Result(new Result.Message("message.duplicate_google_account", Result.Message.Level.error), false), null);
                } else if (duplicate == null && googleDuplicate != null) {
                    User user = User.loadUserWithGoogleId(id);
                    userFacade.setUser(user);
                    userFacade.getUser().setActive(true);
                } else if (duplicate != null && googleDuplicate == null) {
                    User user = User.loadUserWithEmailId(email, false);
                    userFacade.setUser(user);
                    userFacade.getUser().setActive(true);
                } else {
                    RegisterWithGoogle(userFacade, me);
                }
            } else {
            }
        if ((duplicate != null && googleDuplicate != null && !duplicate.equals(googleDuplicate)) ||
                (duplicate != null && !userFacade.getUser().getId().equals(duplicate)) ||
                (googleDuplicate != null && !userFacade.getUser().getId().equals(googleDuplicate))) {
            throw new ResultException(new Result(new Result.Message("message.duplicate_google_account", Result.Message.Level.error), false), null);
        }
//        }
        SystemConfig systemConfig = SystemConfigManager.getCurrentConfig();
        userFacade.getCredential().getGoogleInfo().setGoogle_access_token(access_token);
        userFacade.getCredential().getGoogleInfo().setGoogle_id(id);
        userFacade.getCredential().getGoogleInfo().setPrimary_email(email);
        userFacade.getCredential().getGoogleInfo().setGoogle_url(me.getUrl());
        userFacade.save();
    }

    public Result RegisterWithGoogle(UserFacade userFacade, Person me){
        User usern = new User();
        UserFacade user = new UserFacade(usern);

        user.setFirstName(me.getName().getGivenName());
        user.setLastName(me.getName().getFamilyName());
        user.setEmail(me.getEmails().get(0).getValue());
        user.getUser().setEmail(me.getEmails().get(0).getValue());
        HttpsURL url = null;
        try {
            //TODO: Sina, handle exceptions
            url = new HttpsURL(me.getImage().getUrl());
            url.setQuery("sz","450");
            URL resizeUrl = new URL(url.toString());
            user.setPicture(resizeUrl.openStream(),0,0,450,450);
        } catch (URIException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(me.getGender()!=null) {
            if (me.getGender().equals("male"))
                user.setGender(User.Gender.male);
            else if (me.getGender().equals("female"))
                user.setGender(User.Gender.female);
            else
                user.setGender(User.Gender.Unknown);
        }
        else
            user.setGender(User.Gender.Unknown);
        if(me.getBirthday()!=null) {
            //TODO , Sina fix this
            String date[] = me.getBirthday().split("-");
            Date birthday = new Date();
            birthday.setYear(Integer.parseInt(date[0]));
            birthday.setMonth(Integer.parseInt(date[1]));
            birthday.setDate(Integer.parseInt(date[2]));
        }
        Result result = UserFactory.addUser(user.getUser(),true,true,true);
        userFacade.setUser(user.getUser());
        return result;
    }

    public boolean refreshToken(UserFacade user, List<GetGooglePermission.Scope> permissions){
        String access_Token = (user.getCredential().getGoogleInfo().getGoogle_access_token());
        GoogleCredential credential;
        try{
            if (access_Token != null) {
                GoogleTokenResponse lastToken;
                lastToken = JSON_FACTORY.fromString(access_Token, GoogleTokenResponse.class);
                //System.err.println("time: "+lastToken.getAccessToken());
                URL googleInfoUrl = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + lastToken.getAccessToken());
                HttpURLConnection googleInfoResponse = (HttpURLConnection) googleInfoUrl.openConnection();
                googleInfoResponse.connect();

                int status = googleInfoResponse.getResponseCode();

                if (status == 201 || status == 200) {
                    JsonElement je = (new JsonParser()).parse(new InputStreamReader((InputStream) googleInfoResponse.getContent()));
                    JsonObject jobject = je.getAsJsonObject();
                    if (jobject.get("error") != null) {
                        throw new GoogleInfoParseError();
                    }

                    boolean haveScopes = true;

                    String permisionString = GetGooglePermission.permissionsToString(permissions);
                    for (String permistion : permisionString.split(" ")) {
                        if (!permistion.equals("")) {
                            if (!jobject.get("scope").getAsString().contains(permistion)) {
                                haveScopes = false;
                            }
                        }
                    }
                    if (jobject.get("expires_in").getAsLong() < MAX_EXPIRE_TIME) {
                        haveScopes = false;
                    }

                    if (haveScopes == true) {

                        credential = new GoogleCredential.Builder()
                                .setJsonFactory(JSON_FACTORY)
                                .setTransport(TRANSPORT)
                                .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
                                .setFromTokenResponse(JSON_FACTORY.fromString(
                                        lastToken.toString(), GoogleTokenResponse.class));

                        credential.refreshToken();
                        String accessToken = credential.getAccessToken();
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            return false;
        } catch (GoogleInfoParseError ex) {
            return false;
        }
        return false;
    }



    SocialConnectionManager socialConnectionManager = new SocialConnectionManager();
    GoogleFriendProvider googleFriendProvider = new GoogleFriendProvider();


    public Result addPossibleFriendShips(UserFacade userFacade) {
        List<SocialConnection> current = socialConnectionManager.getCurrentConnections(userFacade.getUser(), SocialConnection.Context.Facebook);
        Set<String> ids = new HashSet<>();
        for (SocialConnection socialConnection : current) {
            ids.add(socialConnection.getSid());
        }

        List<SocialConnection> othersCurrent = socialConnectionManager.getSocialConnectionsToSid(userFacade.getCredential().getGoogleInfo().getPrimary_email(), SocialConnection.Context.Email);
        Set<User> others = new HashSet<>();
        for (SocialConnection socialConnection : othersCurrent) {
            others.add(socialConnection.getOwner());
        }

        try {
            ArrayList<SocialConnection> newConnections = googleFriendProvider.getConnections(userFacade);
            for (SocialConnection newConnection : newConnections) {
                if(!newConnection.isRegistered())
                    continue;
                if(newConnection.getOwner().hasEmail(EmailAddressUtils.normalizeEmail(newConnection.getSid()))) continue;
                if(!ids.contains(newConnection.getSid())){
                    socialConnectionManager.saveAsNew(newConnection);
                }
//                if(!others.contains(newConnection.getRegisteredUser())){
//                    SocialConnection reverse = googleFriendProvider.getReverse(newConnection);
//                    if(reverse!=null)
//                        socialConnectionManager.saveAsNew(reverse);
//                }
            }
            userFacade.setProperty(UserProperties.LastGoogleFriendCheck, String.valueOf(new Date().getTime()));
        } catch (AccessDeniedException | IOException e) {
            Logger.getLogger(this.getClass()).error(e);
            return new Result(false);
        }
        return new Result(true);
    }
}
