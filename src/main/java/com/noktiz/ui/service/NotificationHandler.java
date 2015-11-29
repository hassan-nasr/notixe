//package com.noktiz.ui.service;
//
//import com.noktiz.domain.entity.User;
//import com.noktiz.domain.entity.notifications.*;
//import com.noktiz.domain.entity.rate.NotificationRateInvite;
//import com.noktiz.domain.model.UserFacade;
//import org.codehaus.jackson.node.ArrayNode;
//import org.codehaus.jackson.node.ObjectNode;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import java.util.List;
//
///**
// * Created by hossein on 17/08/2015.
// */
//@Path("notifications")
//public class NotificationHandler extends BaseHandler {
//    @Produces("application/json")
//    @GET
//    @Path("/new")
//    public String getByIds(@QueryParam("senderId") Long senderId){
//        User user = User.load(senderId);
//        ObjectNode root = new ObjectNode(factory);
//        if(user==null){
//            setSuccesss(root,false);
//            return root.toString();
//        }
//        UserFacade userFacade = new UserFacade(user);
//        ArrayNode conversations = root.putArray("notifications");
//        List<BaseNotification> baseNotifications = userFacade.loadMyNotifications();
//        for (BaseNotification baseNotification : baseNotifications) {
//            ObjectNode jsonNodes = conversations.addObject();
//            addNotification(baseNotification,jsonNodes);
//        }
//        return root.toString();
//
//    }
//
//    private void addNotification(BaseNotification obj, ObjectNode node) {
//        node.put("id",obj.getId());
//        node.put("date",obj.getSendDate().getTime());
//        node.put("seen",obj.getSeen());
//        node.put("type",obj.getType());
//        if (obj instanceof NotificationAddFriend) {
//            NotificationAddFriend naf = (NotificationAddFriend) obj;
//            node.put("friendId",naf.getFriend().getId());
//        } else if (obj instanceof NotificationThanks) {
//            NotificationThanks nt = (NotificationThanks) obj;
//            node.put("threadId",nt.getThread().getId());
//        } else if (obj instanceof NotificationRate) {
//            NotificationRate nt = (NotificationRate) obj;
//            node.put("rateId",nt.getRate().getId());
//        } else if (obj instanceof NotificationEndorse) {
//            NotificationEndorse nt = (NotificationEndorse) obj;
//            node.put("context",nt.getEndorse().getContext());
//            node.put("senderId",nt.getEndorse().getSender().getId());
//        } else if (obj instanceof NotificationNewMessage) {
//            NotificationNewMessage nt = (NotificationNewMessage) obj;
//        } else if (obj instanceof NotificationRateInvite) {
//            node.put("type","Undefined");
//        } else {
//            node.put("type","Undefined");
//        }
//    }
//}
