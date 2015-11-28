/*
 * WicketExamplePage.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
 
package com.noktiz.ui.web;           

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.header.SmartHeaderPanel;

/** 
 *
 * @author hossein
 * @version 
 */

public abstract class BaseUserPage extends BasePage {

    public BaseUserPage(String selectedPage) {
        super(null);
        if(getUserInSite()!=null)
            getUserInSite().refresh();
        else {
            User user = User.newGuestUser();
            getUserSession().setUser(new UserFacade(user));
        }

        add(new SmartHeaderPanel("headerpanel", selectedPage));
//        add(new HeaderPanel2("headerpanel"));
    }
    UserSession getUserSession(){
        return (UserSession) getSession();
    }

    public UserFacade getUserInSite(){
        return getUserSession().getUser();
    }
    public boolean isUserLoggedIn(){
        UserFacade userInSite = getUserInSite();
        if(userInSite ==null){
            return false;
        }
        if(userInSite.getUser()==null){
            return true;
        }
        if(userInSite.getUser().getId()==null){
            return false;
        }
        return true;
    }
}
