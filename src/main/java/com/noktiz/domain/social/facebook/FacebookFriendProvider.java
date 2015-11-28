package com.noktiz.domain.social.facebook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;
import com.noktiz.domain.Utils.UrlContent;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.social.FacebookSocialConnection;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.social.FriendProvider;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by hasan on 7/18/14.
 */
public class FacebookFriendProvider implements FriendProvider {

    private boolean showUnRegistered = true;

    public FacebookFriendProvider() {
    }

    public FacebookFriendProvider(boolean showUnRegistered) {
        this.showUnRegistered = showUnRegistered;
    }

    private ArrayList<SocialConnection> getRegisteredConnections(UserFacade user) throws AccessDeniedException, IOException {

        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        ArrayList<SocialConnection> ret = new ArrayList<SocialConnection>();
        try {
            URL url = new URL("https://graph.facebook.com/v2.0/"+facebookId+"/friends?fields=id,name,picture&access_token=" +access_token);
            UrlContent urlContent = new UrlContent(url);
            if(urlContent.getResponseCode()!=200) {
                Logger.getLogger(this.getClass()).error("could not connect to facebook:" + url.toString() +"->"+ urlContent.getResponseCode()+ ":" + urlContent.getContent());
                return ret;
            }
            String content = urlContent.getContent();
            JsonObject head = parser.parse(content).getAsJsonObject();
            JsonArray friends = head.getAsJsonArray("data");
            ret.addAll(extractFriends(friends, user.getUser()));
            while(head.has("paging") &&head.getAsJsonObject("paging").has("next")) {
                url = new URL(head.getAsJsonObject("paging").getAsJsonPrimitive("next").getAsString());
                urlContent = new UrlContent(url);
                if(urlContent.getResponseCode()!=200) {
                    Logger.getLogger(this.getClass()).error("could not connect to facebook:" + url.toString() +"->"+ urlContent.getResponseCode()+ ":" + urlContent.getContent());
                    return ret;
                }
                head = parser.parse(urlContent.getContent()).getAsJsonObject();
                ret.addAll(extractFriends(head.getAsJsonArray("data"), user.getUser()));
                if(false){
                    throw new MalformedJsonException("af");
                }
            }
            return ret;
        } catch (MalformedURLException e ) {
            e.printStackTrace();
        } catch(MalformedJsonException ex){
            ex.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
//            throw e;
        }
        return ret;
    }

    private ArrayList<SocialConnection> extractFriends(JsonArray friends, User user) {
        ArrayList<SocialConnection> ret = new ArrayList<SocialConnection>();
        if (friends == null)
            return ret;
        for (JsonElement friend : friends) {
            SocialConnection c = new FacebookSocialConnection(user);
            c.setContext(SocialConnection.Context.Facebook);
            c.setName(friend.getAsJsonObject().get("name").getAsString());
            c.setSid(friend.getAsJsonObject().get("id").getAsString());
            c.setPictureUrl(friend.getAsJsonObject().get("picture").getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString());
            ret.add(c);
        }
        return ret;
    }

    @Override
    public ArrayList<SocialConnection> getConnections(UserFacade u) throws AccessDeniedException, IOException {

        ArrayList<SocialConnection> ret = new ArrayList<>();
        try {
            ArrayList<SocialConnection> registeredFacebookFriends =getRegisteredConnections(u);
            ArrayList<SocialConnection> facebookFriends;
            if(showUnRegistered)
                facebookFriends = new FacebookTaggableFriendProvider().getConnections(u);
            else
                facebookFriends= registeredFacebookFriends;
            Map<String, SocialConnection> registeredFacebookFriendsMap = new HashMap<>();
            for (SocialConnection registeredFacebookFriend : registeredFacebookFriends) {
                registeredFacebookFriendsMap.put(registeredFacebookFriend.getName(),registeredFacebookFriend);
            }
//            Map<String, SocialConnection> facebookFriendsMap = new HashMap<>();
//            for (SocialConnection facebookFriend : facebookFriends) {
//                facebookFriendsMap.put(facebookFriend.getName(),facebookFriend);
//            }
//
////          loading currentFacebookFriends
//            Set<String> currentFBFriends = new HashSet<>();
//            for (Friendship friendship : u.getFriends()) {
//                String facebook_id = friendship.getFriend().getCredential().getFacebookInfo().getFacebook_id();
//                if(facebook_id!= null)
//                    currentFBFriends.add(facebook_id);
//            }

//            Map<String,SocialConnection> invitedFBFriends = new HashMap<>();
//            for (SocialConnection socialConnection : u.getSocialConnections()) {
//                if (socialConnection.getContext().equals(SocialConnection.Context.Facebook))
//                    if(socialConnection.isInvited())
//                        invitedFBFriends.put(socialConnection.getName(), socialConnection);
//            }
            for (SocialConnection facebookFriend : facebookFriends) {

                if(registeredFacebookFriendsMap.containsKey(facebookFriend.getName()))
                {
                    facebookFriend=registeredFacebookFriendsMap.get(facebookFriend.getName());
                    facebookFriend.setRegisteredUser((User.loadUserByFacebookId(facebookFriend.getSid())));
                    if(facebookFriend.isRegistered()) {
                        if(u.doIOwnerTheFriendshipOf(new UserFacade(facebookFriend.getRegisteredUser())))
                            facebookFriend.setInvited(true);
//                        for (Friendship friendship : u.getFriends()) {
//                            if (friendship.getFriend().equals(facebookFriend.getRegisteredUser()))
//                                facebookFriend.setInvited(true);
//                        }
                    }
                }
//                if(!facebookFriend.isRegistered())
//                    if(invitedFBFriends.containsKey(facebookFriend.getName())) {
//                        facebookFriend.setInvited(true);
//                        facebookFriend.setSid(invitedFBFriends.get(facebookFriend.getName()).getSid());
//                    }
                ret.add(facebookFriend);
            }
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public SocialConnection getReverse(SocialConnection socialConnection) {
        if(socialConnection.getRegisteredUser()==null || socialConnection.getOwner().getCredential().getFacebookInfo().getFacebook_id() == null)
            return null;
        FacebookSocialConnection ret = new FacebookSocialConnection(socialConnection.getRegisteredUser(), socialConnection.getContext(), socialConnection.getOwner().getCredential().getFacebookInfo().getFacebook_id(), false, socialConnection.getOwner().getName());
        ret.setRegisteredUser(socialConnection.getOwner());
        return ret;

    }
}
