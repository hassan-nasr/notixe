/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.friend.fb;

import com.noktiz.ui.web.BaseUserPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 *
 * @author hossein
 */

@AuthorizeInstantiation("USER")
public final class FacebookFriends extends BaseUserPage {

    public FacebookFriends() {
        super(null);
        FBPersonList vt= new FBPersonList("thread");
        add(vt);
    }
}
