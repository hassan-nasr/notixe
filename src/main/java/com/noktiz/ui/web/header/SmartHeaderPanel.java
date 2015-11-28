/*
 * FooterPanel.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
 
package com.noktiz.ui.web.header;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.base.UserPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

/** 
 *
 * @author hossein
 * @version 
 */

public final class SmartHeaderPanel extends UserPanel {

    public SmartHeaderPanel(String id, String selectedPage) {
        super(id);
        UserFacade userInSite = getUserInSite();
        if(userInSite!=null && userInSite.getUser()!=null && userInSite.getUser().getId()!= null  && userInSite.getUser().getId()>0){
            add(new WebMarkupContainer("guest"));
            add(new HeaderPanel("loggedIn", selectedPage)) ;
        } else {
            add(new WebMarkupContainer("loggedIn"));
            add(new GuestHeaderPanel("guest", selectedPage)) ;
        }
    }

}
