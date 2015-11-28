package com.noktiz.domain.social.facebook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.noktiz.domain.Utils.UrlRawContent;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.social.FacebookSocialConnection;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.Utils.UrlContent;
import com.noktiz.domain.persistance.HSF;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.hibernate.cfg.Configuration;

import javax.imageio.ImageIO;

/**
 * Created by hasan on 8/9/14.
 */
public class FacebookUtils {
    @Deprecated
    public static String publishPost(UserFacade user,  String message,String placeId, List<String> tags){
        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        try {
            URL url = new URL("https://graph.facebook.com/v2.0/"+facebookId+"/feed");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            StringBuilder messageBody = new StringBuilder();
            messageBody.append("access_token=").append(access_token);
            messageBody.append("&message=").append(URLEncoder.encode(message));
            if(placeId!=null)
                messageBody.append("&location=").append(placeId);
            StringBuilder csids = new StringBuilder();
            if(tags!=null)
                for (int i = 0; i < tags.size(); i++) {
                    String connection = tags.get(i);
                    csids.append(connection);
                    if (i!=tags.size()-1)
                        csids.append(",");
                }
            if(!csids.toString().isEmpty())
                messageBody.append("&tags=").append(csids.toString());
            urlConnection.setRequestProperty("Content-Length", String.valueOf(messageBody.length()));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            System.out.println("messageBody = " + messageBody);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            PrintStream writer = new PrintStream(urlConnection.getOutputStream());
            writer.print(messageBody.toString());
            writer.close();
            Scanner in = new Scanner(urlConnection.getInputStream());
            StringBuilder result = new StringBuilder();
            while(in.hasNextLine()){
                result.append(in.nextLine()).append("\n");
            }
            JsonParser parser = new JsonParser();
            JsonObject head = parser.parse(result.toString()).getAsJsonObject();
            String postID = head.getAsJsonObject("id").getAsString();
            System.err.println("result:"  +result);
            return postID;
        } catch (MalformedURLException e) {
            Logger.getLogger(FacebookUtils.class).error(e);
            return null;
        } catch (Exception e) {
            Logger.getLogger(FacebookUtils.class).error(e);
            e.printStackTrace();
            return null;
        }
    }

    public static FacebookClient getFacebookClient(User user){
        return new DefaultFacebookClient(user.getCredential().getFacebookInfo().getFacebook_access_token(),new WithProxyWebRequester(),new DefaultJsonMapper());
    }
    public static String publishPost2(User user,  String message,String placeId, List<String> tags, String visibility) throws ResultException {
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        try {
            StringBuilder csids = new StringBuilder();
            if(tags!=null)
                for (int i = 0; i < tags.size(); i++) {
                    String connection = tags.get(i);
                    csids.append(connection);
                    if (i!=tags.size()-1)
                        csids.append(",");
                }
            FacebookType publishMessageResponse =
                    getFacebookClient(user).publish("me/feed", FacebookType.class,
                            Parameter.with("message", message),
                            Parameter.with("privacy", "{'value':'" + visibility + "'}"),
                            Parameter.with("tags", csids.toString()),
                            Parameter.with("place", placeId)
                    );
            return publishMessageResponse.getId();

        }  catch (Exception e) {
            Logger.getLogger(FacebookUtils.class).error(e);
            throw new ResultException(new Result(new Result.Message("message.could_not_connect_to_facebook", Result.Message.Level.error),false),e);
        }
    }

    public static Map<String,String> extractPostIds(User user, String postId) throws ResultException{
        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        try {
            Map<String,String> ret = new HashMap<>();
            URL url = new URL("https://graph.facebook.com/v2.0/"+postId+"?access_token=" +access_token);
            UrlContent result = new UrlContent(url);
            if(result.getResponseCode()!=200)
                throw new IOException(result.getResponseMessage());
            JsonParser parser = new JsonParser();
            JsonElement content = parser.parse(result.getContent());
            JsonArray story_tags = content.getAsJsonObject().get("with_tags").getAsJsonObject().get("data").getAsJsonArray();
            for (JsonElement story_tag : story_tags) {
                ret.put(story_tag.getAsJsonObject().get("id").getAsString(), story_tag.getAsJsonObject().get("name").getAsString());
            }
            return ret;
        } catch (IOException e) {
            Logger.getLogger(FacebookUtils.class).error(e);
            throw new ResultException(new Result(new Result.Message("message.could_not_connect_to_facebook", Result.Message.Level.error),false),e);
        }
    }

    public static Set<String> getUserPermissions(User user) {
        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        String facebookId = user.getCredential().getFacebookInfo().getFacebook_id();
        if(access_token == null || facebookId == null)
            return new HashSet<>();
        try {
            Set<String> ret = new HashSet<>();
            URL url = new URL("https://graph.facebook.com/v2.0/"+facebookId+"/permissions?access_token=" +access_token);
            UrlContent result = new UrlContent(url);
            if(result.getResponseCode()!=200)
                throw new IOException("could not connect to facebook");
            JsonParser parser = new JsonParser();
            JsonElement content = parser.parse(result.getContent());
            JsonArray permissions = content.getAsJsonObject().get("data").getAsJsonArray();
            for (JsonElement friend : permissions) {
                if(friend.getAsJsonObject().get("status").getAsString().equals("granted"))
                    ret.add(friend.getAsJsonObject().get("permission").getAsString());
            }
            return ret;
        } catch (IOException e) {

            Logger.getLogger(FacebookUtils.class).error(e);
            return new HashSet<>();
        }
    }

    public Result fillBasicInfo(User user, boolean override) {


        UserFacade uf = new UserFacade(user);
        String access_token = user.getCredential().getFacebookInfo().getFacebook_access_token();
        if(access_token==null)
            return new Result(new Result.Message("message.access_token_is_null", Result.Message.Level.error),false);
        try {
            URL url = new URL("https://graph.facebook.com/v2.0/me?fields=first_name,last_name,middle_name,email,picture.width(200).height(200),education,age_range,gender,id&access_token=" +access_token);
            UrlContent result = new UrlContent(url);
            if(result.getResponseCode()!=200)
                return new Result(new Result.Message("message.could_not_connect_to_facebook", Result.Message.Level.error),false);
            JsonParser parser = new JsonParser();
            JsonElement content = parser.parse(result.getContent());
            JsonObject basicInfo = content.getAsJsonObject();
            if(override || user.getFirstName()==null)
                user.setFirstName(basicInfo.get("first_name").getAsString());
            String lastName =( basicInfo.has("middle_name")?basicInfo.get("middle_name").getAsString() + " ":"") + basicInfo.get("last_name").getAsString();
            if(override || user.getLastName()==null)
                user.setLastName(lastName);
            String gender = basicInfo.get("gender").getAsString();
            if(override|| user.getGender()==null || user.getGender().equals(User.Gender.Unknown)){
                if(gender.equals("male") || gender.equals("female"))
                    user.setGender(User.Gender.valueOf(gender));
                if(basicInfo.has("email"))
                    user.setEmail(basicInfo.get("email").getAsString());
            }
            if(override || uf.getPicture()==null){
                String pictureUrl=null;
                if(!basicInfo.getAsJsonObject("picture").getAsJsonObject("data").get("is_silhouette").getAsBoolean()){
                    pictureUrl = basicInfo.getAsJsonObject("picture").getAsJsonObject("data").get("url").getAsString();
                }
                if(pictureUrl!=null){
                    BufferedImage image = ImageIO.read(new URL(pictureUrl));
                    uf.setPicture(image);
                }
            }
            return new Result(true);
        } catch (IOException e) {
            Logger.getLogger(FacebookUtils.class).error(e);
            return new Result(new Result.Message("message.could_not_connect_to_facebook", Result.Message.Level.error),false);
        }
    }

    public static String getProfileLink(UserFacade userFacade) {
        return "https://www.facebook.com/app_scoped_user_id/"+userFacade.getCredential().getFacebookInfo().getFacebook_id();
    }

    public static void main(String[] args) throws IOException {
//        HSF.configure(new Configuration().configure().buildSessionFactory());
//        URL url = new URL("https://www.google.com");
//        URLConnection urlConnection = url.openConnection(null);

//        System.out.println(((HttpURLConnection)urlConnection).getResponseCode());
//        System.out.println(result.getContent());

//            HttpClient httpClient = new HttpClient();
//            HttpHost proxy = new HttpHost("127.0.0.1", 8580, Protocol.getProtocol("http"));
//            httpClient.getParams().setParameter("http.route.default-proxy", proxy);
//            int stat = httpClient.executeMethod(new GetMethod("http://www.facebook.com"));
//            System.out.println("stat = " + stat);

//        System.out.println(System.getProperty("http.proxyHost"));
//        String url = "https://graph.facebook.com/v2.0/me/feed";
//        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("localhost",8580)));
//        urlConnection.setDoOutput(true);
//        urlConnection.setDoInput(true);
//        urlConnection.setRequestMethod("POST");
//        PrintStream content = new PrintStream(urlConnection.getOutputStream());
//        String access_token = "CAAUgpZBDBWZC8BAN69CyYaEZA0aaSZCuX3u2y9LpocUgpObnAp19LVQGYExpprmW2r0stqyAIUysYBu0ZAZBLZCKXUITuWyvWnpOA8vzeNN1eMBEy01WkOV7aRPRyeGCAwkvqLX0RsJUsW3Dw70ISX7pxKh1TL6mD9jMbZAP5HoEvnYH7cpY94rb";
//        String message = "hi";
//        String privacy = "{'value':'SELF'}";
//        content.print("access_token="+access_token);
//        content.print("&privacy="+privacy);
//        content.print("&message="+message);
//        content.close();
//        System.out.println(((HttpURLConnection) urlConnection).getResponseCode());
//        Scanner in = new Scanner(urlConnection.getInputStream());
//        while(in.hasNextLine()){
//            System.out.println(in.nextLine());
//        }

        FacebookClient facebookClient = new DefaultFacebookClient("CAAUgpZBDBWZC8BAN69CyYaEZA0aaSZCuX3u2y9LpocUgpObnAp19LVQGYExpprmW2r0stqyAIUysYBu0ZAZBLZCKXUITuWyvWnpOA8vzeNN1eMBEy01WkOV7aRPRyeGCAwkvqLX0RsJUsW3Dw70ISX7pxKh1TL6mD9jMbZAP5HoEvnYH7cpY94rb",new WithProxyWebRequester(),new DefaultJsonMapper());

        FacebookType publishMessageResponse =
                facebookClient.publish("me/feed", FacebookType.class,
                        Parameter.with("message", "RestFB test")
                                .with("privacy", "{'value':'"+"SELF" +"'}")
                                .with("place","106432896058894")
//                                .with("tags", csids.toString())
                );
        publishMessageResponse.getId();
        com.restfb.types.User user = facebookClient.fetchObject("me", com.restfb.types.User
                .class);
        System.out.println("user = " + user);
    }
}
