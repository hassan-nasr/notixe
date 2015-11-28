/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.auth;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.http.WebResponse;

/**
 *
 * @author Hossein
 */
public final class SignOutPage extends WebPage {

    public SignOutPage() {
        super();
        ((UserSession) Session.get()).invalidate(((WebResponse) getResponse()));
        setResponsePage(SignInPage.class);
    }
}
