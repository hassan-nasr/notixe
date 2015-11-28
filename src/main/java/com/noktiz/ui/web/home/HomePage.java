/*
 * HomePage.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
package com.noktiz.ui.web.home;

import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.base.IAction;
import com.noktiz.ui.web.component.infobox.SimpleInfoBoxWithImage;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel2;
import com.noktiz.ui.web.component.lazy.PortletPanel;
import com.noktiz.ui.web.dashboard.UpdateListHolder;
import com.noktiz.ui.web.friend.FriendListPage;
import com.noktiz.ui.web.friend.suggest.FriendSuggest;
import com.noktiz.ui.web.infobox.SimpleInfoBoxProvider;
import com.noktiz.ui.web.rate.context.PredefinedRateContextSelector;
import com.noktiz.ui.web.rate.context.RateContextsManagePanel;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

//import com.sun.xml.internal.ws.api.WebServiceFeatureFactory;

@AuthorizeInstantiation("USER")
public class HomePage extends BaseUserPage {

    public HomePage() {
        super("home");
        Logger.getLogger(HomePage.class).trace("constructing HomePage");
        UpdateListHolder updateListHolder = new UpdateListHolder("updateList");
        add(updateListHolder);
        ArrayList<IAction> action= new ArrayList<>();
        action.add(new IAction() {
            @Override
            public String getDestinationUrl() {
                return urlFor(FriendListPage.class, new PageParameters().add("sub", "searchFriend")).toString();
            }

            @Override
            public String getActionIcon() {
                return "icon-search";
            }

            @Override
            public IModel<String> getActionTitle() {
                return new StringResourceModel("search",null);
            }
        });
        add(new PortletPanel("friendSuggest", new LazyLoadPanel2("content", new FriendSuggest("content")), new StringResourceModel("AddFriend", null),action, false));
        add(new PortletPanel("rateContext", new RateContextsManagePanel("content", getUserInSite(), true) ,new StringResourceModel("Questions",null),false) );
//        add(new PredefinedRateContextSelector("prcs") {
//            @Override
//            public void mySelectedQuestions(PredefinedQuestion predefinedQuestion, AjaxRequestTarget art) {
//                System.err.println(predefinedQuestion);
//            }
//        });
//        SimpleInfoBoxProvider p1 = new SimpleInfoBoxProvider(
//                "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info1title", "info1text"
//        );
//        p1.setAlignmentClass("aaa");
//        SimpleInfoBoxWithImage simpleInfoBox1 = new SimpleInfoBoxWithImage("infoBox1", p1);
//        add(simpleInfoBox1);
//
//        SimpleInfoBoxProvider p2 = new SimpleInfoBoxProvider(
//                "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info2title", "info2text"
//        );
//        p2.setAlignmentClass("aaa");
//        SimpleInfoBoxWithImage simpleInfoBox2 = new SimpleInfoBoxWithImage("infoBox2", p2);
//        add(simpleInfoBox2);
//        SimpleInfoBoxProvider p3 = new SimpleInfoBoxProvider(
//                "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info3title", "info3text"
//        );
//        p3.setAlignmentClass("bbb");
//        SimpleInfoBoxWithImage simpleInfoBox3 = new SimpleInfoBoxWithImage("infoBox3", p3);
//        add(simpleInfoBox3);
//        SimpleInfoBoxProvider p4 = new SimpleInfoBoxProvider(
//                "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info3title", "info3text"
//        );
//        p4.setAlignmentClass("bbb");
//        SimpleInfoBoxWithImage simpleInfoBox4 = new SimpleInfoBoxWithImage("infoBox4", p4);
//        add(simpleInfoBox4);
    }


}
