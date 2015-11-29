//package com.noktiz.ui.service;
//
//import com.noktiz.domain.entity.User;
//import com.noktiz.domain.entity.rate.Rate;
//import com.noktiz.domain.model.MessageFacade;
//import com.noktiz.domain.model.ResultWithObject;
//import com.noktiz.domain.model.ThreadFacade;
//import com.noktiz.domain.model.UserFacade;
//import org.codehaus.jackson.node.ArrayNode;
//import org.codehaus.jackson.node.ObjectNode;
//
//import javax.ws.rs.*;
//import java.util.List;
//
///**
// * Created by hossein on 17/08/2015.
// */
//@Path("thread")
//public class ThreadHandler extends BaseHandler {
//    @Produces("application/json")
//    @GET
//    @Path("/id")
//    public String getConversationByList(@QueryParam("senderId") Long senderId, @QueryParam("id") List<Long> ids){
//        User user = User.load(senderId);
//        ObjectNode root = new ObjectNode(factory);
//        if(user==null){
//            setSuccesss(root,false);
//            return root.toString();
//        }
//        UserFacade userFacade = new UserFacade(user);
//        ArrayNode conversations = root.putArray("conversations");
//        for (Long id : ids) {
//            ObjectNode conversation = conversations.addObject();
//            ResultWithObject<ThreadFacade> threadResult = userFacade.getThread(id);
//            if(threadResult.result==false){
//                threadResult.displayInJson(conversation);
//                continue;
//            }
//            getThread(userFacade,threadResult.object,id, conversation);
//        }
//        return root.toString();
//
//    }
//    @Produces("application/json")
//    @GET
//    @Path("/range")
//    public String getConversationFromTo(@QueryParam("senderId") Long senderId, @DefaultValue("0")@QueryParam("from") Long from, @DefaultValue("10")@QueryParam("count")long count){
//        User user = User.load(senderId);
//        ObjectNode root = new ObjectNode(factory);
//        if(user==null){
//            setSuccesss(root,false);
//            return root.toString();
//        }
//        UserFacade userFacade = new UserFacade(user);
//        ArrayNode conversations = root.putArray("conversations");
//        List<ThreadFacade> myThreadList = userFacade.getMyThreadList(from, count);
//        for (ThreadFacade threadFacade : myThreadList){
//            ObjectNode conversation = conversations.addObject();
//            getThread(userFacade,threadFacade,threadFacade.getId(),conversation);
//        }
//        root.put("count",myThreadList.size());
//        return root.toString();
//    }
//
//    private void getThread(UserFacade userFacade, ThreadFacade thread, Long id, ObjectNode conversation) {
//        conversation.put("id",id);
//        ObjectNode conversationBody = new ObjectNode(factory);
//        conversation.put("obj",conversationBody);
//            conversationBody.put("id", thread.getId());
//        conversationBody.put("participant_id", thread.getParticipant().getId());
//        conversationBody.put("myBlock", thread.getMyBlock() != null);
//        conversationBody.put("participantBlock", thread.getParticipantBlock() != null);
//        conversationBody.put("participantBlockBy", thread.getParticipantBlockBy() != null);
//        conversationBody.put("myThank", thread.didIThank());
//        conversationBody.put("participantThank", thread.doParticipantThank());
//        conversationBody.put("canIThank", thread.canIThank(userFacade));
//        conversationBody.put("title", thread.getTitle());
//        conversationBody.put("rate", getRate(null, thread.getRelatedRate()));
//        ArrayNode messages = conversationBody.putArray("messages");
//        getMessages(messages,thread.getMessages());
//        setSuccesss(conversation, true);
//        setMessage(conversation,null);
//    }
//
//    public ObjectNode getRate(ObjectNode node, Rate rate){
//        if(node==null){
//            node=new ObjectNode(factory);
//        }
//        if(rate==null){
//            return node;
//        }
//        node.put("rateId",rate.getId());
//        node.put("comment",rate.getComment());
//        node.put("showSender",rate.isShowSender());
//        node.put("rate",rate.getRate());
//        node.put("time",rate.getDate().getTime());
//        node.put("contextId",rate.getContext().getId());
//        return node;
//    }
//    public ArrayNode getMessages(ArrayNode node, List<MessageFacade> messages){
//        if(node==null){
//            node= new ArrayNode(factory);
//        }
//        if(messages==null){
//            return node;
//        }
//        for (MessageFacade message : messages) {
//            node.add(getMessage(null,message));
//        }
//        return node;
//
//    }
//
//    private ObjectNode getMessage(ObjectNode node, MessageFacade message) {
//        if(node==null){
//            node=new ObjectNode(factory);
//        }
//        if(message==null){
//            return node;
//        }
//        node.put("content",message.getContent());
//        node.put("dir",message.getDir());
//        node.put("isDraft",message.isDraft());
//        node.put("doIRead",message.isReciverRead());
//        node.put("receiverId",message.getReciver().getId());
//        node.put("senderId",message.getSender().getId());
//        node.put("sendDate",message.getSendDate().getTime());
//        node.put("blocked",message.getBlock()!=null);
//        return node;
//    }
//
//
//}
