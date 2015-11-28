/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.google;

import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.google.GoogleFriendProvider;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.friend.fb.SocialFriendListViewer;

import java.util.Arrays;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author sina
 */
public class GoogleSmartFriendPanel extends Panel {

    PersonList friendList;
    GetGooglePermission googleConnect;
    WebMarkupContainer wmc;
    WebMarkupContainer gcv;
    
    public GoogleSmartFriendPanel(String id, final UserFacade user) {
        super(id);
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);        
        gcv = new WebMarkupContainer("googleContactViwer");
        gcv.setVisible(false);
        wmc.add(gcv);
        googleConnect = new GetGooglePermission("googlePermitionGetter",Arrays.asList(
                GetGooglePermission.Scope.basicProfile,
                GetGooglePermission.Scope.email,
                GetGooglePermission.Scope.contacts
        ),user) {
            @Override
            protected void onAccessGranted(AjaxRequestTarget art) {
                showFriends(user,art);
                this.setVisible(false);
            }

            @Override
            protected void onAccessRejected(AjaxRequestTarget target, Result e) {
                e.displayInWicket(feedbackPanel);
                target.add(this);
                target.add(feedbackPanel);
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        };
        wmc.add(googleConnect);
    }
    
    private void showFriends(UserFacade user, AjaxRequestTarget target) {
        Panel friendsPanel = new PersonList("googleContactViwer", Model.of(new SocialFriendListViewer(user, new GoogleFriendProvider())), null, 500, null, true);
        wmc.addOrReplace(friendsPanel);
        gcv.setVisible(true);
        if(target!=null) {
            target.add(wmc);
        }
    }
    
}
