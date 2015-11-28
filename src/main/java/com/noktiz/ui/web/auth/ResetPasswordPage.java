package com.noktiz.ui.web.auth;

import com.noktiz.ui.web.BasePage;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.home.HomePage;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.settings.PasswordSettings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Created by hasan on 9/13/14.
 */

@AuthorizeInstantiation("USER")
public class ResetPasswordPage extends BaseUserPage{

    WebMarkupContainer feedbackWmc;

    public ResetPasswordPage() {
        super("resetPass");
        setResponsePage(HomePage.class);
    }

    public ResetPasswordPage(Boolean checkLastPass) {
        super("resetPass");
        feedbackWmc = new WebMarkupContainer("feedbackWmc");
        feedbackWmc.setOutputMarkupId(true);
        add(feedbackWmc);
        NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        feedbackWmc.add(feedback);
        PasswordSettings passwordSettings = new PasswordSettings("resetPasswordPanel", getUserInSite(),checkLastPass) {
            @Override
            public void onAjax(AjaxRequestTarget target) {
                target.add(feedbackWmc);
            }
        };
        add(passwordSettings);
    }
}
