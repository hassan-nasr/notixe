/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.auth;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePage;
import com.noktiz.ui.web.home.HomePage;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author hossein
 */
public final class SignInPage extends BasePage {


    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(SignInPage.class, "SignInPage.css")));
    }

    public SignInPage() {
        super(new WebMarkupContainer("footerpanel"));
        String continuePage = getRequest().getRequestParameters().getParameterValue("continue").toString(null);
        if(((UserSession) getSession()).signInWithStaySignedInCookie(((WebRequest) getRequest()), (WebResponse) getResponse())) {
            if(continuePage!=null)
                throw new RedirectToUrlException(continuePage);
            setResponsePage(HomePage.class, null);
//            throw new RedirectToUrlException(urlFor(HomePage.class, null).toString());
//            new RestartResponseAtInterceptPageException()
        }
//        Session.get().invalidate();
        add(new CompleteSignInPanel("signIn", continuePage));
//        add(new SimpleInfoBoxWithImage("info1",new SimpleInfoBoxProvider("http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info1title", "info1text")));
//        add(new SimpleInfoBoxWithImage("info2",new SimpleInfoBoxProvider("http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info1title", "info1text")));
//        add(new SimpleInfoBoxWithImage("info3",new SimpleInfoBoxProvider("http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
//                "info1title", "info1text")));
        add( new Image("logo",new PackageResourceReference(Application.class,"Notixe-glow.png")));
//        add( new Image("logo",new PackageResourceReference(Application.class,"logo-white-trans-big.png")));

//        add(new SignIn("signIn"));
    }
}
