/*
 * HomePage.java
 *
 * Created on June 8, 2014, 2:25 PM
 */

package com.noktiz.ui.web;


import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation("USER")
@Deprecated
public class StartConversation extends BaseUserPage {

    public StartConversation() {
        super("conversation");
//        add(new SendMessage("sendMessage"));
    }

}
