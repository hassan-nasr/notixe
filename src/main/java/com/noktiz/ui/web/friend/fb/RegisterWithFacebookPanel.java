package com.noktiz.ui.web.friend.fb;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.facebook.FacebookFriendProvider;
import com.noktiz.ui.web.home.HomePage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.social.FacebookConnect;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Created by hasan on 9/10/14.
 */
public class RegisterWithFacebookPanel extends Panel{


    PersonList friendList;
    FacebookConnect facebookConnect;
    WebMarkupContainer wmc;
    final UserFacade user = new UserFacade(new User());
    public RegisterWithFacebookPanel(String id) {
        super(id);
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        ArrayList<String> permission = new ArrayList<>();
        permission.add("user_friends");
        permission.add("user_likes");
        permission.add("email");
        permission.add("public_profile");
        facebookConnect = new FacebookConnect("facebookConnect",user,permission,getString("enterWithFacebook"),null,false) {
            @Override
            public void onAccessGranted(AjaxRequestTarget target) {
                this.setVisible(false);
                ((UserSession)getSession()).signIn(user);
                setResponsePage(HomePage.class);
            }
        };
        wmc.add(facebookConnect);
    }

}
