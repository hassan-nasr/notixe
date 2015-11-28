package com.noktiz.domain.social.facebook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.social.FriendProvider;
import com.noktiz.domain.social.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hasan on 7/18/14.
 */
public class FacebookListedFriendProvider implements FriendProvider {
    String listName;

    public FacebookListedFriendProvider(String listName) {
        this.listName = listName;
    }

    @Override
    public ArrayList<SocialConnection> getConnections(UserFacade user) throws AccessDeniedException, IOException {
        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        ArrayList<SocialConnection> ret = new ArrayList<SocialConnection>();
        try {
            URL url = new URL("https://graph.facebook.com/v2.0/"+facebookId+"/friends?access_token=" +access_token);
            String content = Utils.getHTML(url);
            System.out.println(content);
            JsonObject head = parser.parse(content).getAsJsonObject();
            JsonArray friends = head.getAsJsonArray("data");
            ret.addAll(extractFriends(friends, user.getUser()));
            while(head.has("paging") &&head.getAsJsonObject("paging").has("next")) {
                url = new URL(head.getAsJsonObject("paging").getAsJsonPrimitive("next").getAsString());
                head = parser.parse(Utils.getHTML(url)).getAsJsonObject();
                ret.addAll(extractFriends(head.getAsJsonArray("friends"), user.getUser()));
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
            SocialConnection c = new SocialConnection(user);
            c.setContext(SocialConnection.Context.Facebook);
            c.setName(friend.getAsJsonObject().get("name").getAsString());
            c.setSid(friend.getAsJsonObject().get("id").getAsString());
            ret.add(c);
        }
        return ret;
    }

}
