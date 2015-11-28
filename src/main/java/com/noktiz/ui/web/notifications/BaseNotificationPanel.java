/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.notifications;

import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.rate.stars.RealStars;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 *
 * @author Hossein
 */
public abstract class BaseNotificationPanel extends UserPanel {

    private Label notificationCountPanel = null;

    public Label getNotificationCountPanel() {
        return notificationCountPanel;
    }

    public void setNotificationCountPanel(Label notificationCountPanel) {

        this.notificationCountPanel = notificationCountPanel;
    }

    private final IModel<? extends BaseNotification> notification;

    public BaseNotificationPanel(String id, IModel<? extends BaseNotification> notification) {
        super(id);
        this.notification = notification;
    }

    public void init(IModel<String> titleModel, IModel<String> messageModel, Image image, Long starsCount, AbstractLink action1p, IModel<String> actionTitle) {
        final WebMarkupContainer action = new WebMarkupContainer("actions");
        setOutputMarkupId(true);
        if (action1p != null) {
            add(action);
            action.setOutputMarkupId(true);
            AbstractLink action1 = action1p;
            action.add(action1);
            action1.add(new Label("actionTitle", actionTitle));
        } else {
            action.setVisible(false);
        }
        add(action);

        Label message = new Label("message", messageModel);
        message.setEscapeModelStrings(false);
        add(message);
        Label title = new Label("title", titleModel);
        title.setEscapeModelStrings(false);
        add(title);
        if (image != null) {
            add(image);
        } else {
            WebMarkupContainer photo = new WebMarkupContainer("photo");
            photo.setVisible(false);
            add(photo);
        }
        if (starsCount != null) {
            RealStars stars = new RealStars("realStars", starsCount, 20);
            add(stars);
        } else {
            add(new WebMarkupContainer("realStars").setVisible(false));
        }

        AjaxLink markRead = new AjaxLink("markRead") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                markAsRead(ajaxRequestTarget);
                BaseNotificationPanel.this.setVisible(false);
                ajaxRequestTarget.add(BaseNotificationPanel.this);
            }
        };
        add(markRead);

    }

    protected void markAsRead(AjaxRequestTarget ajaxRequestTarget) {
        notification.getObject().markAsRead();
        if (notificationCountPanel != null) {
            Serializable currentVal = (Serializable) notificationCountPanel.getDefaultModelObject();
            if (currentVal instanceof Number) {
                currentVal = (int) currentVal - 1;
                if ((int) currentVal == 0) {
                    currentVal = "";
                }
            }
            notificationCountPanel.setDefaultModel(Model.of(currentVal));
            ajaxRequestTarget.add(notificationCountPanel);
        }
    }
}
