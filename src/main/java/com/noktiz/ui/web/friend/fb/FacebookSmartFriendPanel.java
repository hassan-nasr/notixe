package com.noktiz.ui.web.friend.fb;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.facebook.FacebookFriendProvider;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.social.FacebookConnect;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Created by hasan on 9/5/14.
 */
public class FacebookSmartFriendPanel extends Panel {

    PersonList friendList;
    FacebookConnect facebookConnect;
    WebMarkupContainer wmc;
    public FacebookSmartFriendPanel(String id, final UserFacade user) {
        super(id);
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        ArrayList<String> permission = new ArrayList<>();
        permission.add("user_friends");
        permission.add("public_profile");
        permission.add("user_likes");
        permission.add("publish_actions");
        permission.add("read_stream");
        wmc.add(new WebMarkupContainer("facebookFriendsPanel"));
        facebookConnect = new FacebookConnect("facebookConnect",user,permission,getString("connectToFacebook"),getString("facebookPostPrivacy"),true) {
            @Override
            public void onAccessGranted(AjaxRequestTarget target) {
                showFriends(user,target);
                this.setVisible(false);

            }
        };
        wmc.add(facebookConnect);
    }

    private void showFriends(UserFacade user, AjaxRequestTarget target) {
        Panel friendsPanel = new PersonList("facebookFriendsPanel", Model.of(new SocialFriendListViewer(user, new FacebookFriendProvider())), null, 200, null, true);
        wmc.addOrReplace(friendsPanel);
        if(target!=null) {
            target.add(wmc);
        }
    }
}

