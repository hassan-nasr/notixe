/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.google;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.social.google.GoogleFriendProvider;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.friend.fb.SocialFriendListViewer;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author sina
 */
public class ShowGoogleContacts extends Panel{
    public ShowGoogleContacts(String id){
        super(id);
        UserFacade userInSite = ((UserSession)Session.get()).getUser();
        User user = (User) HSF.get().getCurrentSession().load(User.class,userInSite.getUser().getId());

        UserFacade userFacade = new UserFacade(user);
        add(new PersonList("fbfriends", Model.of(new SocialFriendListViewer(userFacade, new GoogleFriendProvider())), null, 5, null, true));
    }
}
