/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.auth;

import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.TimezoneOfUserPanel;
import com.noktiz.ui.web.user.RegisterPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public final class SignIn extends Panel {

    TextField username;
    TextField timezone;
    PasswordTextField password;

    public SignIn(String id) {
        super(id);
        Form form;
        form = new Form("form") {
            @Override
            protected void onSubmit() {
                UserSession session = (UserSession) getSession();
                boolean r = session.signIn(username.getDefaultModelObjectAsString(), password.getDefaultModelObjectAsString());
                if (r) {
                    Integer tz=0;
                    try {
                        tz = Integer.valueOf(timezone.getDefaultModelObjectAsString());

                    } catch (NumberFormatException ex) {
                    }
                    session.setTimezoneOffset(-tz);

                    setResponsePage(getApplication().getHomePage());
                } else {
                    String errmsg = getString("loginError", null, "Unable to sign you in");
                    error(errmsg);
                }
            }
        };
        add(form);
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        form.add(feedback);
        username = new TextField("username", new Model());
        form.add(username);
        username.setRequired(true);
        timezone = new TimezoneOfUserPanel("timezone", new Model());
        form.add(timezone);
        username.setRequired(true);
        password = new PasswordTextField("password", new Model());
        form.add(password);
        password.setRequired(true);
        add(new RegisterPanel("signup"));



    }
}
