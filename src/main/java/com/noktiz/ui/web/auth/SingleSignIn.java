package com.noktiz.ui.web.auth;

import com.noktiz.domain.entity.token.SingleAccessToken;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.SingleAccessTokenManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.home.HomePage;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * Created by hasan on 9/13/14.
 */
public class SingleSignIn extends BaseUserPage {
    public SingleSignIn() {
        super(null);
        try {
            add(new BookmarkablePageLink<>("home", HomePage.class));
            String code= getRequest().getRequestParameters().getParameterValue("code").toString();
            SingleAccessToken sat = new SingleAccessTokenManager().useToken(code);

            if(((UserSession)getSession()).isSignedIn())
                ((UserSession)getSession()).signOut();
            ((UserSession)getSession()).signIn(new UserFacade(sat.getUser()));
            if(sat.getType().equals(SingleAccessToken.Type.ResetPassword)){
                setResponsePage(new ResetPasswordPage(false));
            }

        } catch (ResultException e) {
            Logger.getLogger(this.getClass()).error(e);
        }

    }
}
