package com.noktiz.domain.social.facebook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.noktiz.domain.Utils.UrlContent;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.social.FacebookSocialConnection;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.social.FriendProvider;
import com.noktiz.domain.social.Utils;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hasan on 7/18/14.
 */
public class FacebookTaggableFriendProvider implements FriendProvider {
    @Override
    public ArrayList<SocialConnection> getConnections(UserFacade user) throws AccessDeniedException, IOException {

        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        ArrayList<SocialConnection> ret = new ArrayList<SocialConnection>();
//        ret.add(new SocialConnection(user, SocialConnection.Context.Facebook,"123123123",false, "userA"));
//        ret.add(new SocialConnection(user, SocialConnection.Context.Facebook,"qweqweqwe",false, "userB"));
//        if(true)
//            return ret;
        try {
            URL url = new URL("https://graph.facebook.com/v2.0/"+facebookId+"/taggable_friends?fields=id,name,picture.width(80).height(80)&access_token=" +access_token);
            String content = new UrlContent(url).getContent();
            JsonObject head = parser.parse(content).getAsJsonObject();
            JsonArray friends = head.getAsJsonArray("data");
            ret.addAll(extractFriends(friends, user.getUser()));
            while(head.has("paging") &&head.getAsJsonObject("paging").has("next")) {
                url = new URL(head.getAsJsonObject("paging").getAsJsonPrimitive("next").getAsString());
                head = parser.parse(new UrlContent(url).getContent()).getAsJsonObject();
                ret.addAll(extractFriends(head.getAsJsonArray("data"), user.getUser()));
            }
            return ret;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return ret;
    }

    @Override
    public SocialConnection getReverse(SocialConnection connection) {
        return null;
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


    public void inviteUser(User user, FacebookSocialConnection connections) throws ResultException {
//        if(true)
//            return;
        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        List<String > ids = new ArrayList<>();
//        for (SocialConnection connection : connections) {
            ids.add(connections.getSid());
//        }
        String postId = FacebookUtils.publishPost2(user,"hey " + connections.getName() + " I've just added you as trusted friend in "+ SystemConfigManager.getCurrentConfig().getAppName() + new Random().nextInt(),"106432896058894",ids,"SELF");
//        String postId = "10204345908713752_10204535655817311";
//        System.out.println("postId = " + postId);
        Map<String, String> postIds = FacebookUtils.extractPostIds(user, postId);
//        for (SocialConnection connection : connections) {
//            connection.setSid(null);
//        }
        for (Map.Entry<String, String> s : postIds.entrySet()) {
//            for (SocialConnection connection : connections) {
                if(/*connection.getSid() == null &&*/ connections.getName().equals(s.getValue())){
                    connections.setSid(s.getKey());
                }
//            }
        }
    }

}
