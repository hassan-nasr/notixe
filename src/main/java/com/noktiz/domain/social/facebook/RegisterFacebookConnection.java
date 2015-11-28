package com.noktiz.domain.social.facebook;

import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.Utils.UrlContent;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by hasan on 8/7/14.
 */
public class RegisterFacebookConnection {
    public Result register(UserFacade userFacade, String id, String access_token) throws ResultException {
        SystemConfig systemConfig = SystemConfigManager.getCurrentConfig();
        User possibleDuplicate = User.loadUserByFacebookId(id);

        if(possibleDuplicate!=null && userFacade.getUser().getId()==null)
            userFacade.setUser(possibleDuplicate);
        if(possibleDuplicate!=null && !possibleDuplicate.equals(userFacade.getUser())){
            throw new ResultException(new Result(new Result.Message("message.duplicate_facebook_account", Result.Message.Level.error),false),null);
        }

        String persistentToken = getPersistentToken(access_token, systemConfig.getProperty("FB_APP_ID"), systemConfig.getProperty("FB_APP_SECRET"));
        if(persistentToken!=null )
            userFacade.getCredential().getFacebookInfo().setFacebook_access_token(persistentToken);
        else
            userFacade.getCredential().getFacebookInfo().setFacebook_access_token(access_token);
        userFacade.getCredential().getFacebookInfo().setFacebook_id(id);
        Result result = new Result(true);
        if(userFacade.getUser().getId()==null)
        {
            result = signUpWithFacebook(userFacade);
            if(!result.result)
                throw new ResultException(result, null);
        }
        result.addRequired(userFacade.save());
        result.addOptional(userFacade.connectToRequestedUsers(SocialConnection.Context.Facebook));
        result.addOptional(addPossibleFriendShips(userFacade));
        return result;
    }
    SocialConnectionManager socialConnectionManager = new SocialConnectionManager();
    FacebookFriendProvider facebookFriendProvider = new FacebookFriendProvider(false);


    public Result addPossibleFriendShips(UserFacade userFacade) {
        List<SocialConnection> current = socialConnectionManager.getCurrentConnections(userFacade.getUser(), SocialConnection.Context.Facebook);
        Set<String> ids = new HashSet<>();
        for (SocialConnection socialConnection : current) {
            ids.add(socialConnection.getSid());
        }

        List<SocialConnection> othersCurrent = socialConnectionManager.getSocialConnectionsToSid(userFacade.getCredential().getFacebookInfo().getFacebook_id(), SocialConnection.Context.Facebook);
        Set<User> others = new HashSet<>();
        for (SocialConnection socialConnection : othersCurrent) {
            others.add(socialConnection.getOwner());
        }

        try {
            ArrayList<SocialConnection> newConnections = facebookFriendProvider.getConnections(userFacade);
            for (SocialConnection newConnection : newConnections) {
                if(true != newConnection.isRegistered())
                    continue;
                if(!ids.contains(newConnection.getSid())){
                    socialConnectionManager.saveAsNew(newConnection);
                }
                if(!others.contains(newConnection.getRegisteredUser())){
                    SocialConnection reverse = facebookFriendProvider.getReverse(newConnection);
                    if(reverse!=null)
                        socialConnectionManager.saveAsNew(reverse);
                }
            }
            userFacade.setProperty(UserProperties.LastFacebookFriendCheck, String.valueOf(new Date().getTime()));
        } catch (AccessDeniedException | IOException e) {
            Logger.getLogger(this.getClass()).error(e);
            return new Result(false);
        }
        return new Result(true);
    }

    private Result signUpWithFacebook(UserFacade userFacade) {
        Result r = new FacebookUtils().fillBasicInfo(userFacade.getUser(), false);
        if(!r.result)
            return r;
        if(userFacade.getEmail()!=null){
            User possibleDuplicate = User.loadUserWithEmailId(userFacade.getEmail(), false);
            if(possibleDuplicate == null)
                return UserFactory.addUser(userFacade.getUser(),true, true,false);
            else {
                possibleDuplicate.getCredential().setFacebookInfo(userFacade.getCredential().getFacebookInfo());
                userFacade.setUser(possibleDuplicate);
                possibleDuplicate.setActive(true);
                return new Result(new Result.Message("message.connect_to_same_facebook_account", Result.Message.Level.info),true);
            }
        }
        else
            return new Result(new Result.Message("message.mail_required", Result.Message.Level.error),false);
    }

    private String getPersistentToken(String access_token, String fb_app_id, String fb_app_secret) {
        try {
            URL url = new URL("https://graph.facebook.com/oauth/access_token?" +
                    "grant_type=fb_exchange_token" +
                    "&client_id="+ fb_app_id+
                    "&client_secret="+fb_app_secret +
                    "&fb_exchange_token="+access_token);
            UrlContent urlContent = new UrlContent(url);
            if(urlContent.getResponseCode()==200){
                return urlContent.getContent().split("&")[0].split("=")[1];
            }
            Logger.getLogger(this.getClass()).error(urlContent.getContent());
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass()).error(e);
        }
        return null;
    }
}

//https://www.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id=1443280285948927&client_secret=461c40cdde051a1cd8a79aa560b07bea&fb_exchange_token=
