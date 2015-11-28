/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.settings;

import com.noktiz.domain.entity.NotificationSettings;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import java.util.ArrayList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Hossein
 */
public abstract class NotificationSettingsPanel extends Panel {

    public NotificationSettingsPanel(String id, final UserFacade user) {
        super(id);
        Form form = new Form("form");
        add(form);
        NotificationSettings.EmailState[] values = NotificationSettings.EmailState.values();
        ArrayList<String> valuesStr= new ArrayList();
        for (NotificationSettings.EmailState state : values) {
            valuesStr.add(state.toString());
        }
        DropDownChoice newMessage = new DropDownChoice("newMessage", new IModel<String>() {
            @Override
            public String getObject() {
                NotificationSettings ns = user.getNotificationSettings();
                return ns.getNewMessageNotification().toString();
            }

            @Override
            public void setObject(String object) {
                NotificationSettings ns = user.getNotificationSettings();
                ns.setNewMessageNotification(NotificationSettings.EmailState.valueOf(object));
            }

            @Override
            public void detach() {
            }
        }
                ,valuesStr,new IChoiceRendererImpl2());
        form.add(newMessage);
        newMessage.setRequired(true);
        DropDownChoice newRate = new DropDownChoice("newRate", new IModel<String>() {
            @Override
            public String getObject() {
                NotificationSettings ns = user.getNotificationSettings();
                return ns.getNewRateNotification().toString();
            }

            @Override
            public void setObject(String object) {
                NotificationSettings ns = user.getNotificationSettings();
                ns.setNewRateNotification(NotificationSettings.EmailState.valueOf(object));
            }

            @Override
            public void detach() {
            }
        }
                ,valuesStr,new IChoiceRendererImpl2());
        form.add(newRate);
        newRate.setRequired(true);
        DropDownChoice friendJoined = new DropDownChoice("friendJoined",
                new IModel<String>() {
            @Override
            public String getObject() {
                NotificationSettings ns = user.getNotificationSettings();
                return ns.getFriendJoinedNotification().toString();
            }

            @Override
            public void setObject(String object) {
                NotificationSettings ns = user.getNotificationSettings();
                ns.setFriendJoinedNotification(NotificationSettings.EmailState.valueOf(object));
            }

            @Override
            public void detach() {
            }
        },valuesStr,new IChoiceRendererImpl2());
        form.add(friendJoined);
        friendJoined.setRequired(true);
        DropDownChoice friendAddYou = new DropDownChoice("friendAddYou",
                new IModel<String>() {
            @Override
            public String getObject() {
                NotificationSettings ns = user.getNotificationSettings();
                return ns.getFriendAddYouNotification().toString();
            }

            @Override
            public void setObject(String object) {
                NotificationSettings ns = user.getNotificationSettings();
                ns.setFriendAddYouNotification(NotificationSettings.EmailState.valueOf(object));
            }

            @Override
            public void detach() {
            }
        },valuesStr,new IChoiceRendererImpl2());
        form.add(friendAddYou);
        friendAddYou.setRequired(true);
        DropDownChoice rateInvite = new DropDownChoice("rateInvite",
                new IModel<String>() {
            @Override
            public String getObject() {
                NotificationSettings ns = user.getNotificationSettings();
                return ns.getRateInviteNotification().toString();
            }

            @Override
            public void setObject(String object) {
                NotificationSettings ns = user.getNotificationSettings();
                ns.setRateInviteNotification(NotificationSettings.EmailState.valueOf(object));
            }

            @Override
            public void detach() {
            }
        },valuesStr,new IChoiceRendererImpl2());
        form.add(rateInvite);
        rateInvite.setRequired(true);
        AjaxSubmitLink submit = new IndicatingAjaxSubmitLink2("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Result save = user.save();
                success(getString("settingsChanged"));
                save.displayInWicket(form);
                onAjax(target);
            }
        };
        form.add(submit);
    }

    public abstract void onAjax(AjaxRequestTarget target);

    class IChoiceRendererImpl2 implements IChoiceRenderer<String> {

        public IChoiceRendererImpl2() {
        }

        @Override
        public Object getDisplayValue(String object) {
            return NotificationSettingsPanel.this.getString(object);
        }

        @Override
        public String getIdValue(String object, int index) {
            return object;
        }
    }    
}
