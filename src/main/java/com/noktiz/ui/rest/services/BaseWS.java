package com.noktiz.ui.rest.services;

import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.rest.core.JsonCreator.JacksonJsonCreator;
import com.noktiz.ui.rest.services.response.SimpleResponse;
import org.apache.log4j.Logger;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.response.NullResponse;
import org.hibernate.cfg.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * Created by Hassan on 8/15/2015.
 * base ws
 */
public class BaseWS {

    @Context
    private HttpServletRequest request;


    protected static JacksonJsonCreator getJsonCreator() {
        return new JacksonJsonCreator();
    }


    RequestCycle rc;
    public BaseWS() {
        configureSessionFactory();
        rc = new RequestCycle(new RequestCycleContext(new MockWebRequest(null), NullResponse.getInstance(), new BookmarkableMapper(), new DefaultExceptionMapper()));
        HSF.get().onBeginRequest(rc);
    }
    private static boolean configured=false;
    private static void configureSessionFactory(){
        if(!configured){
            HSF.configure(new Configuration().configure().buildSessionFactory());
            configured=true;
        }
    }
    public void onEndRequest(){
        HSF.get().onEndRequest(rc);
    }

    public static String createSimpleResponse(SimpleResponse.Status status, String message)  {
        try {
            return getJsonCreator().getJson(new SimpleResponse(status, message));
        }
        catch (Exception e){
            Logger.getLogger(BaseWS.class).error("error creating json",e);
            throw new RuntimeException(e);
        }
    }

    public static String createSimpleResponse(SimpleResponse.Status status, List<Result.Message> messages)  {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            String s = messages.get(i).getText();
            if(i>0)
                message.append("\n");
            message.append(s);
        }
        try {
            return getJsonCreator().getJson(new SimpleResponse(status, message.toString()));
        }
        catch (Exception e){
            Logger.getLogger(BaseWS.class).error("error creating json",e);
            throw new RuntimeException(e);
        }
    }

    protected UserFacade getUserInSite() {
        try {
            return (UserFacade) request.getAttribute("userInSite");
        } catch (Exception e) {
            return null;
        }
    }
}
