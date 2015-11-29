//package com.noktiz.ui.service;
//
//import com.noktiz.domain.persistance.HSF;
//import org.apache.wicket.DefaultExceptionMapper;
//import org.apache.wicket.core.request.mapper.BookmarkableMapper;
//import org.apache.wicket.mock.MockWebRequest;
//import org.apache.wicket.request.cycle.RequestCycle;
//import org.apache.wicket.request.cycle.RequestCycleContext;
//import org.apache.wicket.response.NullResponse;
//import org.codehaus.jackson.node.JsonNodeFactory;
//import org.hibernate.cfg.Configuration;
//
///**
// * Created by hossein on 16/08/2015.
// */
//
//public class BaseHandler{
//    RequestCycle rc;
//    JsonNodeFactory factory = JsonNodeFactory.instance;
//    public BaseHandler() {
//        configureSessionFactory();
//        rc = new RequestCycle(new RequestCycleContext(new MockWebRequest(null), NullResponse.getInstance(), new BookmarkableMapper(), new DefaultExceptionMapper()));
//        HSF.get().onBeginRequest(rc);
//    }
//    private static boolean configured=false;
//    private static void configureSessionFactory(){
//        if(!configured){
//            HSF.configure(new Configuration().configure().buildSessionFactory());
//            configured=true;
//        }
//    }
//    public void onEndRequest(){
//        HSF.get().onEndRequest(rc);
//    }
////    protected void setSuccesss(ObjectNode node, boolean status){
////        node.put("success", status);
////    }
////    protected void setMessage(ObjectNode node, List<Result.Message> messages){
////        ArrayNode messagesNode = node.putArray("message");
////        if(messages !=null){
////            for (Result.Message message : messages) {
////                ObjectNode messageNode = messagesNode.addObject();
////                messageNode.put("level",message.level.toString());
////                messageNode.put("text",message.text);
////            }
////        }
////    }
//}
