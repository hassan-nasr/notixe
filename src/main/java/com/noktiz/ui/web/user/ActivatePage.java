/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.user;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.home.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 *
 * @author Hossein
 */
public final class ActivatePage extends WebPage {

    public ActivatePage() {
        super();
        IRequestParameters rp = RequestCycle.get().getRequest().getRequestParameters();
        String code = rp.getParameterValue("code").toOptionalString();
        if(code==null){
            code="!!";
        }
        User user=User.loadByActivationCode(code);
        if(user==null)
            //@todo is it a bug?
            user= User.loadUserWithEmail(code,false);
        if(user!=null){

            if(((UserSession)getSession()).isSignedIn()) {
                ((UserSession) getSession()).signOut();
                getSession().invalidate();
            }
            UserFacade userFacade = new UserFacade(user);
            userFacade.activate();
            userFacade.save();
            ((UserSession)getSession()).signIn(new UserFacade(user));
            setResponsePage(getApplication().getHomePage());
//            Label result= new Label("result","Your account activate successfully. Please visit sign in page");
//            add(result);
        }else{
            Label result= new Label("result",getString("activationError"));
            add(result);
        }
        
    }
    
}
