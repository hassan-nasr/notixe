/*
 * ThreadPage.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
package com.noktiz.ui.web.thread;

import com.noktiz.domain.entity.*;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.rate.RatingsOfPanel;
import com.noktiz.ui.web.rate.RateFriendPanel;
import com.noktiz.ui.web.thread.ThreadInfoPanel;
import com.noktiz.ui.web.thread.ThreadPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
//import com.sun.xml.internal.ws.api.WebServiceFeatureFactory;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@AuthorizeInstantiation("USER")
public class ThreadPage extends BaseUserPage {
        ThreadPanel threadPanel;
        private boolean expand=false;
//        WebMarkupContainer rate= new WebMarkupContainer("rate");
        WebMarkupContainer wmc= new WebMarkupContainer("wmc");
//        ThreadInfoPanel threadInfo= new ThreadInfoPanel("threadInfo",new Model<ThreadFacade>()) {
//
//            @Override
//            public void onBlock(AjaxRequestTarget art) {
//
//            }
//
//            @Override
//            public void onUnblock(AjaxRequestTarget art) {
//
//            }
//        };
    
        
    public ThreadPage() {
        super("conversation");
        add(wmc);
        wmc.setOutputMarkupId(true);
//        wmc.add(threadInfo);
//        threadInfo.setVisible(false);
        threadPanel = new ThreadPanel("threadList",this);
        wmc.add(threadPanel);
        threadPanel.setOutputMarkupId(true);
        if(expand){
            threadPanel.add(new AttributeModifier("class", "span8 "));
        }

//        wmc.add(rate);
//        rate.setOutputMarkupId(true);
//        RateFriendPanel rateFriendPanel = new RateFriendPanel("ratePanel");
//        add(rateFriendPanel);
//        RatingsOfPanel ratingsOfPanel = new RatingsOfPanel("ratesOfPanel");
//        rate.add(ratingsOfPanel);
//	add(new Label("label", new PropertyModel((UserSession)getSession(),"user.email")));
    }

    public static PageParameters getThreadPageParam(com.noktiz.domain.entity.Thread thread ) {
        return new PageParameters().add("threadId", thread.getId());
    }

    public void expandThreads(AjaxRequestTarget target,ThreadFacade tf){
        if(true)return;
        if(threadPanel!=null){
            threadPanel.add(new AttributeModifier("class", "span8 "));
        }else{
            expand=true;
        }
//        threadInfo.setDefaultModelObject(tf);
//        threadInfo.setVisible(true);
        if(target!=null){
            target.add(wmc);
        }
    }
    public void shortenThreads(AjaxRequestTarget target){
        if(true)return;
//        threadInfo.setVisible(false);
        if(threadPanel!=null)threadPanel.add(new AttributeModifier("class", "span12 "));
        if(target!=null){
            target.add(wmc);
        }
    }

}
