/*
 * HomePage.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
package com.noktiz.ui.web.thread;

import com.noktiz.ui.web.BaseUserPage;
//import com.sun.xml.internal.ws.api.WebServiceFeatureFactory;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation("USER")
public class ThreadPage_old extends BaseUserPage {

    public ThreadPage_old() {
        super("thread");
//        ThreadPanel threadPanel = new ThreadPanel("threadList");
//        add(threadPanel);
//        RateFriendPanel rateFriendPanel = new RateFriendPanel("ratePanel");
//        add(rateFriendPanel);
//        RatingsOfPanel ratingsOfPanel = new RatingsOfPanel("ratesOfPanel");
//        add(ratingsOfPanel);
//
//        UpdateList updateList = new UpdateList("updateList");
//        add(updateList);
//	add(new Label("label", new PropertyModel((UserSession)getSession(),"user.email")));
    }

}
