/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.notifications;

import com.noktiz.domain.entity.*;
import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.entity.notifications.NotificationAddFriend;
import com.noktiz.domain.entity.notifications.NotificationThanks;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 * @author Hossein
 */
public final class NotificationPanelThanks extends BaseNotificationPanel {

    UserFacade thanker;
    public NotificationPanelThanks(String id, IModel<NotificationThanks> obj) {
        super(id, obj);
        StringResourceModel message = new StringResourceModel("thanks.message", obj, new Object[]{
                new PropertyModel(obj, "thread.title")
        });
//        add(message);
        com.noktiz.domain.entity.Thread thread = obj.getObject().getThread();
        if (thread.getStarter().equals(obj.getObject().getOwner())) {
            thanker = new UserFacade(thread.getTarget(), !thread.isTargetVisible());
        } else if (thread.getTarget().equals(obj.getObject().getOwner())) {
            thanker = new UserFacade(thread.getStarter(), !thread.isStarterVisible());
        }
        IModel title = new StringResourceModel("thanks.title", null, new Object[]{
                new PropertyModel(new Model(thanker), "name")
        });
        Image img = new Image("photo", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(thanker, ImageManagement.ImageSize.medium));
        init(title, message, img, null, null, null);
    }
}
