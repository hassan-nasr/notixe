/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package com.noktiz.ui.web.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.google.GoogleUtils;
import java.io.IOException;

import com.noktiz.domain.system.SystemConfigManager;
import org.apache.log4j.Logger;

/**
 *
 * @author sina
 */
public class OAuth2GoogleCredentialBuilder {
    
    protected static final String CLIENT_ID = "368179572834-2cb2ss663jep3n0g3faf40ohq2ci82jp.apps.googleusercontent.com";
    protected static final String CLIENT_SECRET = "Mq9m-rdd8UjBp7t0NGFwh4c4";
    protected static String CLIENT_CODE;
    protected static final HttpTransport TRANSPORT = new NetHttpTransport();
    protected static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    protected static final Gson GSON = new Gson();
    
    public static boolean Build(String CLIENT_CODE, UserFacade user){
        try{
            GoogleTokenResponse tokenResponse =
                    new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY,
                            CLIENT_ID, CLIENT_SECRET, CLIENT_CODE, "postmessage").execute();
            GoogleIdToken idToken = tokenResponse.parseIdToken();

            
            
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(TRANSPORT)
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
                    .setFromTokenResponse(JSON_FACTORY.fromString(
                            tokenResponse.toString(), GoogleTokenResponse.class));
            Plus pService = new Plus.Builder(TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(SystemConfigManager.getCurrentConfig().getAppName())
                    .build();
            Person me = pService.people().get("me").execute();
            
            new GoogleUtils().connectUser(user, me.getEmails().get(0).getValue() , me.getId() , tokenResponse.toString(), me);
            
        } catch (IOException ex) {
            return false;
        } catch (ResultException e) {
            return false;
        }

        return true;
        
    }

}
