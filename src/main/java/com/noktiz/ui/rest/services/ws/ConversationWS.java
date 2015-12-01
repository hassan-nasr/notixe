package com.noktiz.ui.rest.services.ws;

import com.noktiz.domain.entity.Message;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.*;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.MessageView;
import com.noktiz.ui.rest.services.response.SimpleResponse;
import com.noktiz.ui.rest.services.response.ThreadView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hassan on 30/11/2015.
 */
@Path("/conversation")
public class ConversationWS extends BaseWS  {
    @GET
    @Path("/thread")
    @Produces("application/json")
    public String thread(@QueryParam("conversationId") Long conversationId,@QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        List<ThreadView> myThreadList = new ArrayList<>();
        if(conversationId == null){
            for (ThreadFacade threadFacade : userInSite.getMyThreadList(from, count)){
                myThreadList.add(new ThreadView(threadFacade));
            }
        }
        else
            myThreadList.add(new ThreadView(new ThreadFacade(Thread.loadThreadWithId(conversationId),userInSite)));
        return getJsonCreator().getJson(myThreadList);
    }
    @GET
    @Path("/message")
    @Produces("application/json")
//    TODO:api handle  messageId from count
    public String message(@QueryParam("messageId")Long messageId,@QueryParam("conversationId") Long conversationId,@QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        if(messageId!=null){
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unsupported");
        }
        if(conversationId==null){
            return createSimpleResponse(SimpleResponse.Status.Failed,"You should set conversationId or messageId");
        }

        List<MessageView> myThreadList = new ArrayList<>();

        final ThreadFacade threadFacade = new ThreadFacade(Thread.loadThreadWithId(conversationId), userInSite);
        for (MessageFacade messageFacade : threadFacade.getMessages()){
            myThreadList.add(new MessageView(messageFacade,threadFacade));
        }
        return getJsonCreator().getJson(myThreadList);
    }

    @GET
    @Path("/sendMessage")
    @Produces("application/json")
    public String sendMessage(@QueryParam("conversationId")Long conversationId, @QueryParam("message") String message, @QueryParam("dir") String dirs){

        try {
            UserFacade userInSite = getUserInSite();

            if (userInSite == null)
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unauthenticated");
            ThreadFacade threadFacade = new ThreadFacade(Thread.loadThreadWithId(conversationId), getUserInSite());
            Message.DIR dir;
            try {
                dir = Message.DIR.valueOf(dirs);
            } catch (Exception e) {
                dir = Message.DIR.ltr;
            }
            Result result = new Result();
            final ResultWithObject<MessageFacade> draft = threadFacade.draft(getUserInSite(), threadFacade.getParticipant(), message, dir);
            result.addRequired(draft);
            if(result.result)
                result.addRequired(draft.object.publish());
            if(result.result)
                return getJsonCreator().getJson(new SimpleResponse(SimpleResponse.Status.Success, "Message Sent", new MessageView(draft.object,threadFacade)));
            else
                return createSimpleResponse(SimpleResponse.Status.Failed,result.messages);
        }catch (Exception e){
            e.printStackTrace();
            return createSimpleResponse(SimpleResponse.Status.Failed,e.getMessage());
        }
    }
    @GET
    @Path("/markRead")
    @Produces("application/json")
//    TODO:api messageId
    public String markRaad(@QueryParam("conversationId")Long conversationId, @QueryParam("messageId") Long messageId){

        try {
            UserFacade userInSite = getUserInSite();

            if (userInSite == null)
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unauthenticated");
            ThreadFacade threadFacade = new ThreadFacade(Thread.loadThreadWithId(conversationId),getUserInSite());
            final Result result;
            if(messageId==null){
                result = threadFacade.markAllAsRead();
            }
            else
                return createSimpleResponse(SimpleResponse.Status.Failed,"Unimplemented");
            if(result.result)
                return createSimpleResponse(SimpleResponse.Status.Success,"Done");
            else
                return createSimpleResponse(SimpleResponse.Status.Failed,result.messages);
        }catch (Exception e){
            e.printStackTrace();
            return createSimpleResponse(SimpleResponse.Status.Failed,e.getMessage());
        }
    }


    @GET
    @Path("/createConversation")
    @Produces("application/json")
    public String createConversation(@QueryParam("userId")Long receiverId, @QueryParam("title") String title, @QueryParam("message") String message, @QueryParam("dir") String dirs){

        try {
            UserFacade userInSite = getUserInSite();

            if (userInSite == null)
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unauthenticated");
            UserFacade receiver = new UserFacade(User.load(receiverId));
            Message.DIR dir;
            try {
                dir = Message.DIR.valueOf(dirs);
            } catch (Exception e) {
                dir = Message.DIR.ltr;
            }
//            Result result = new Result();
            final ResultWithObject<ThreadFacade> thread = getUserInSite().startThread(receiver, title, message,dir);
            if(thread.result)
                return getJsonCreator().getJson(new SimpleResponse(SimpleResponse.Status.Success,"Created",new ThreadView(thread.object)));
            else
                return createSimpleResponse(SimpleResponse.Status.Failed,thread.messages);
        }catch (Exception e){
            e.printStackTrace();
            return createSimpleResponse(SimpleResponse.Status.Failed,e.getMessage());
        }
    }

    @GET
    @Path("/replyToRate")
    @Produces("application/json")
    public String replyToRate(@QueryParam("rateId")Long rateId){

        try {
            UserFacade userInSite = getUserInSite();

            if (userInSite == null)
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unauthenticated");
            final RateManager rateManager = new RateManager();
            Rate rate = rateManager.load(rateId);
            final ThreadFacade replyThread;
            if(!rate.getReceiver().equals(getUserInSite().getUser()))
                return createSimpleResponse(SimpleResponse.Status.Failed,"not.allowd");
            else if (rate.getThread() != null) {
                return createSimpleResponse(SimpleResponse.Status.Failed, "alread.created");
            }else{
                replyThread = rateManager.createReplyThread(rate);
            }
            return getJsonCreator().getJson(new SimpleResponse(SimpleResponse.Status.Success, "Conversation Created", new ThreadView(replyThread)));

        }catch (Exception e){
            e.printStackTrace();
            return createSimpleResponse(SimpleResponse.Status.Failed,e.getMessage());
        }
    }

}
