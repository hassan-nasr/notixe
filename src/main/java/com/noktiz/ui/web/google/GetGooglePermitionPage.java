/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.google;

import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.friend.PersonList;
import java.util.Arrays;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.Model;

/**
 *
 * @author LABOOOOX
 */
@AuthorizeInstantiation("USER")
public final class GetGooglePermitionPage extends WebPage {
    protected PersonList personList;
    
    public GetGooglePermitionPage() {
        add(new GoogleSmartFriendPanel("showFriendsTest", ((UserSession)getSession()).getUser()));
        //this.add(personList);
    }

    @Override
    public void renderHead(HtmlHeaderContainer container) {
        super.renderHead(container); //To change body of generated methods, choose Tools | Templates.
//        personList.setVisible(false);
    }
    
    
}
