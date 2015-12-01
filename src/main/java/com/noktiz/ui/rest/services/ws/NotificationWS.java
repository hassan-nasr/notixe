package com.noktiz.ui.rest.services.ws;

import com.noktiz.domain.entity.notifications.*;
import com.noktiz.domain.entity.rate.NotificationRateInvite;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.NotificationView;
import com.noktiz.ui.rest.services.response.RateInviteView;
import com.noktiz.ui.rest.services.response.SimpleResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hassan on 30/11/2015.
 */
@Path("/")
public class NotificationWS extends BaseWS  {
    @GET
    @Path("/notifications")
    @Produces("application/json")
    public String notifications(/*@QueryParam("userId")Long userId*/ @QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        final List<BaseNotification> baseNotifications = BaseNotification.loadByUser(userInSite.getUser(), from, count);
        List<NotificationView> ret = new ArrayList<>();
        for (BaseNotification baseNotification : baseNotifications) {
            if (baseNotification instanceof NotificationEndorse)
                ret.add(new NotificationView((NotificationEndorse) baseNotification));
            if (baseNotification instanceof NotificationNewMessage)
                ret.add(new NotificationView((NotificationNewMessage) baseNotification));
            if (baseNotification instanceof NotificationAddFriend)
                ret.add(new NotificationView((NotificationAddFriend) baseNotification));
            if (baseNotification instanceof NotificationThanks)
                ret.add(new NotificationView((NotificationThanks) baseNotification));
            if (baseNotification instanceof NotificationRate)
                ret.add(new NotificationView((NotificationRate) baseNotification));
            if (baseNotification instanceof NotificationRateInvite)
                ret.add(new NotificationView((NotificationRateInvite) baseNotification));
        }
        return getJsonCreator().getJson(ret);
    }
    @GET
    @Path("/rateInvites")
    @Produces("application/json")
    public String rateInvite(@QueryParam("userId")Long userId, @QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        final List<BaseNotification> baseNotifications = BaseNotification.loadByUserInUpdateList(getUserInSite().getUser(),from,true,false,count);
        List<RateInviteView> ret = new ArrayList<>();
        for (BaseNotification baseNotification : baseNotifications) {
            if (baseNotification instanceof NotificationRateInvite)
                ret.add(new RateInviteView((NotificationRateInvite) baseNotification));
        }
        return getJsonCreator().getJson(ret);
    }


}
