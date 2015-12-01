package com.noktiz.ui.rest.services.ws;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.privacy.ComplexPersonList;
import com.noktiz.domain.entity.rate.*;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.RateContextView;
import com.noktiz.ui.rest.services.response.RateView;
import com.noktiz.ui.rest.services.response.SimpleResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hassan on 30/11/2015.
 */
@Path("/rateContext")
public class RateContextWS extends BaseWS  {
    @GET
    @Path("/")
    @Produces("application/json")
    public String rateContext(@QueryParam("rateContextId")Long rateContextId, @QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        List<RateContextView> ret = new ArrayList<>();
        if(rateContextId == null){
            final List<RateContext> rateContexts = new RateContextManager().loadRateContextsByUser(getUserInSite(), from, count);
            for (RateContext rateContext : rateContexts) {
                ret.add(new RateContextView(rateContext,getUserInSite()));
            }
        }
        else{
            final RateContext rateContext = new RateContextManager().load(rateContextId);
            if(rateContext == null)
                return createSimpleResponse(SimpleResponse.Status.Failed,"NotFound");
            if(rateContext.getUser().equals(getUserInSite().getUser()))
                ret.add(new RateContextView(rateContext,getUserInSite()));
            else {
                final NotificationRateInvite inviteNotification = new RateContextManager().getInviteNotification(rateContext, getUserInSite().getUser());
                if (inviteNotification != null && !inviteNotification.isDeleted())
                    ret.add(new RateContextView(rateContext, getUserInSite()));
                else
                    return createSimpleResponse(SimpleResponse.Status.Failed, "Unpasteurized");
            }
        }

        return getJsonCreator().getJson(ret);
    }


    @Produces("application/json")
    @Path("/createOrUpdate")
    @GET
    public String rateContext(@QueryParam("rateContextId")Long rateContextId,
                              @QueryParam("title") String title,
                              @QueryParam("description") String description,
                              @QueryParam("invitee") String invitee,
                              @QueryParam("rateState") String state){

        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        try {
            RateContext rateContext;
            ComplexPersonList invitedPerson = extractInvitedPersonFromString(invitee);
            final RateContext.RateState rateState = RateContext.RateState.valueOf(state);
            final RateContextManager rateContextManager = new RateContextManager();
            ResultWithObject<RateContext> resultWithObject;
            if (rateContextId == null) {
                rateContext = new RateContext(title, description, getUserInSite().getUser());
                rateContext.setInvitedPersons(invitedPerson);
                rateContext.setRequireRate(rateState);
                resultWithObject = rateContextManager.saveAsNew(rateContext, true);
            } else {
                rateContext = rateContextManager.load(rateContextId);
                rateContext.setTitle(title);
                rateContext.setDescription(description);
                rateContext.getInvitedPersons().setIncludeMyTrustedFriends(invitedPerson.isIncludeMyTrustedFriends());
                rateContext.getInvitedPersons().setIncludeUsers(invitedPerson.getIncludeUsers());
                rateContext.setRequireRate(rateState);
                try {
                    resultWithObject = rateContextManager.update(rateContext, true);
                } catch (ResultException e) {
                    return createSimpleResponse(SimpleResponse.Status.Failed, e.getResult().messages);
                }
            }
            if (resultWithObject.result) {
                return getJsonCreator().getJson(new SimpleResponse(SimpleResponse.Status.Success, "Done", new RateContextView(resultWithObject.object,getUserInSite())));
            } else
                return createSimpleResponse(SimpleResponse.Status.Failed, resultWithObject.messages);
        }catch (Exception e){
            e.printStackTrace();
            return createSimpleResponse(SimpleResponse.Status.Failed, "Failed");
        }

    }

    @Produces("application/json")
    @Path("/respond")
    @GET
    public String respond(@QueryParam("rateInviteId")Long notificationRateInviteId,
                              @QueryParam("message")String message,
                              @QueryParam("rate")Integer score ){

        try {
            UserFacade userInSite = getUserInSite();
            if (userInSite == null)
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unauthenticated");
            final NotificationRateInvite notification = NotificationRateInvite.load(notificationRateInviteId);
            if (!notification.getOwner().equals(getUserInSite().getUser()))
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unauthenticated");
            RateContext rateContext = notification.getRateContext();
            Rate rate = new Rate(getUserInSite().getUser(), score, message, rateContext, new Date());
            RateManager rateManager = new RateManager();
            ResultWithObject<Rate> result = rateManager.rateFriend(rate, notification);
            if (result.result)
                return getJsonCreator().getJson(new SimpleResponse(SimpleResponse.Status.Success, "Done", new RateView(result.object, getUserInSite())));
            else
                return createSimpleResponse(SimpleResponse.Status.Failed, result.messages);
        }catch (Exception e){
            e.printStackTrace();
            return createSimpleResponse(SimpleResponse.Status.Failed, e.getMessage());
        }

    }


    private ComplexPersonList extractInvitedPersonFromString(String invitee) {
        ComplexPersonList ret = new ComplexPersonList();

        for (String s : invitee.split(",")) {
            if(s.equals("MY_TRUSTED"))
                ret.setIncludeMyTrustedFriends(true);
            else {
                final long l = Long.parseLong(s);
                final User friend = User.load(l);
                if(getUserInSite().doIOwnerTheFriendshipOf(new UserFacade(friend))) {
                    ret.getIncludeUsers().add(friend);
                }
            }
        }
        return ret;
    }


}
