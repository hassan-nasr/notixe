/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.notifications;

import com.noktiz.domain.entity.*;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.entity.notifications.NotificationAddFriend;
import com.noktiz.domain.entity.notifications.NotificationNewMessage;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.thread.ThreadPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

/**
 *
 * @author Hossein
 */
public class NotificationPanelNewMessage extends BaseNotificationPanel {

    public NotificationPanelNewMessage(String id, IModel<NotificationNewMessage> obj) {
        super(id,obj);
        final Message messageObj = obj.getObject().getMessage();
        final Thread thread = obj.getObject().getMessage().getThread();

        BookmarkablePageLink viewButton = new BookmarkablePageLink("action1", ThreadPage.class, ThreadPage.getThreadPageParam(thread));
        IModel<String> actionTitle = Model.of("view");

        UserFacade senderFacade = messageObj.getSenderFacade();
        IModel<String> title = new StringResourceModel("receiveMessage", null,new Object[]{escapeHtml(senderFacade.getName())});
        IModel<String> message = Model.of(thread.getTitle());
        Image img = new Image("photo", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(senderFacade, ImageManagement.ImageSize.medium));
        init(title,message,img,null,viewButton,actionTitle);
    }
}
