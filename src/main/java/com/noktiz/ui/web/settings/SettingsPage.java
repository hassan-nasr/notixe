/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.settings;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 *
 * @author Hossein
 */

@AuthorizeInstantiation("USER")
public final class SettingsPage extends BaseUserPage {

    WebMarkupContainer feedbackWmc;
    UserFacade uf;

    public SettingsPage( ) {
        this(((UserSession) Session.get()).getUser());
    }

    public SettingsPage(UserFacade us) {
        super("settings");
        this.uf=us;
        feedbackWmc = new WebMarkupContainer("feedbackWmc");
        feedbackWmc.setOutputMarkupId(true);
        add(feedbackWmc);
        NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        feedbackWmc.add(feedback);

        //        NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
//        feedbackWmc.add(feedback);

        NotificationSettingsPanel nsp = new NotificationSettingsPanel("notificationSetting", uf) {
            @Override
            public void onAjax(AjaxRequestTarget target) {
                SettingsPage.this.onAjax(target);
            }
        };
        add(nsp);
        PersonalInfoSettings accountSetting = new PersonalInfoSettings("accountSetting", uf) {
            @Override
            public void onAjax(AjaxRequestTarget target) {
                SettingsPage.this.onAjax(target);
            }
        };
        add(accountSetting);
        PictureSetting pictureSetting = new PictureSetting("pictureSetting", uf) {
            public void onAjax(AjaxRequestTarget target) {
                SettingsPage.this.onAjax(target);
            }
        };
        add(pictureSetting);
        PasswordSettings passwordSetting = new PasswordSettings("passwordSetting", uf,true) {
            @Override
            public void onAjax(AjaxRequestTarget target) {
                SettingsPage.this.onAjax(target);
            }
        };
        add(passwordSetting);

//        LoadInfoFromFacebook getFromFacebook = new LoadInfoFromFacebook("getFromFacebook", this);
//        add(getFromFacebook);
//        add(new WebMarkupContainer("getFromFacebook"));
    }

    private void onAjax(AjaxRequestTarget target) {
        target.add(feedbackWmc);
    }
}
