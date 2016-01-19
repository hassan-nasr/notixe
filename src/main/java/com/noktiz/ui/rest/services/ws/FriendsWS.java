package com.noktiz.ui.rest.services.ws;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.SimpleResponse;
import com.noktiz.ui.rest.services.response.UserInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hassan on 30/11/2015.
 */
@Path("/friends")
public class FriendsWS extends BaseWS  {
    @GET
    @Path("/myTrustedFriends")
    @Produces("application/json")
    public String myTrustedFriends(/*@QueryParam("userId")Long userId*/ @QueryParam("from") Integer from, @QueryParam("count") Integer cound){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        User userToShow = null;
//        if(userId == null)
            userToShow = userInSite.getUser();
//        else
//            userToShow = User.load(userId);
        if(from == null)
            from =0;
        if(cound == null)
            cound =1000;
        final List<Friendship> friendships = Friendship.loadFriends(userToShow, from, cound);
        List<UserInfo> userInfos = new ArrayList<>();
        for (Friendship friendship : friendships) {
            userInfos.add(new UserInfo(friendship.getFriend(),null));
        }
        return getJsonCreator().getJson(userInfos);
    }

    @GET
    @Path("/friendsTrustInMe")
    @Produces("application/json")
    public String friendsTrustInMe(@QueryParam("userId")Long userId, @QueryParam("from") Integer from, @QueryParam("cuont") Integer cound){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        User userToShow = null;
        if(userId == null)
            userInSite.getUser();
        else
            userToShow=User.load(userId);
        if(from == null)
            from =0;
        if(cound == null)
            cound =1000;
        final List<Friendship> friendships = Friendship.loadImFriendsOf(userToShow, from, cound);
        List<UserInfo> userInfos = new ArrayList<>();
        for (Friendship friendship : friendships) {
            userInfos.add(new UserInfo(friendship.getFriendshipOwner(),null));
        }
        return getJsonCreator().getJson(userInfos);
    }

    @GET
    @Path("/searchFriendByEmail")
    @Produces("application/json")
    public String searchFriendByEmail(@QueryParam("emails")String  emails){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        String[] emailsArray = emails.split(",");
        List<User> users = User.loadUsersWithEmailIds(emailsArray,true);
        List<UserInfo> ret = new ArrayList<>();
        for (User user : users) {
            ret.add(new UserInfo(user,userInSite.getUser()));
        }
        return getJsonCreator().getJson(ret);
    }


}
