package com.noktiz.ui.web.google;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.home.HomePage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.friend.PersonList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Arrays;

/**
 * Created by sina on 11/6/14.
 */
public class GoogleLogin extends Panel {

    PersonList friendList;
    GetGooglePermission googleConnect;
    WebMarkupContainer wmc;
    WebMarkupContainer gcv;
    NotificationFeedbackPanel feedbackPanel;
    public GoogleLogin(String id) {
        super(id);
        feedbackPanel = new NotificationFeedbackPanel("feedback");
        add(feedbackPanel);
        feedbackPanel.setOutputMarkupId(true);
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        final UserFacade user = new UserFacade(new User());
        googleConnect = new GetGooglePermission("googleConnect", Arrays.asList(
                GetGooglePermission.Scope.basicProfile,
                GetGooglePermission.Scope.email
        ),user, false,getString("EnterWithGoogle")) {
            @Override
            protected void onAccessGranted(AjaxRequestTarget art) {
                ((UserSession)getSession()).signIn(user);
                setResponsePage(HomePage.class);
                //this.setVisible(false);
            }

            @Override
            protected void onAccessRejected(AjaxRequestTarget target, Result e) {
                e.displayInWicket(feedbackPanel);
                target.add(this);
                target.add(feedbackPanel);
//                target.add(feedbackPanel);
//                target.add(this);
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        };
        wmc.add(googleConnect);
    }

}
