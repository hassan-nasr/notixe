/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.notifications;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.entity.notifications.NotificationEndorse;
import com.noktiz.domain.entity.notifications.NotificationThanks;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

/**
 *
 * @author Hossein
 */
public final class NotificationPanelEndorse extends BaseNotificationPanel {

    public NotificationPanelEndorse(String id, IModel<NotificationEndorse> obj) {
        super(id,obj);
        final Endorse endorse = obj.getObject().getEndorse();
        String messageString = new StringResourceModel("endorse.message",null, new Object[]{escapeHtml(endorse.getSender().getName()), escapeHtml(endorse.getContext())}).getObject();
        String title = getString("endorse.title");
        Image img = new Image("photo", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(new UserFacade(endorse.getSender()), ImageManagement.ImageSize.medium));
        AjaxLink addToProfile;

        final Model<String> actionTitle = new Model<>(getString("addToProfile"));
        if(getUserInSite().getUser().getApprovedEndorseContexts().contains(endorse.getContext())){
            addToProfile = null;
        }
        else{

            addToProfile = new IndicatingAjaxLink2("action1") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    new UserFacade(endorse.getReceiver()).approveEndorseContext(endorse);
                    obj.getObject().setSeen(true);
                    obj.getObject().update();
                    this.setEnabled(false);
                    actionTitle.setObject("Done");
                    target.add(getParent());
                }
            };
        }
        init(Model.of(title),Model.of(messageString),img,null,addToProfile,actionTitle);
    }
}
