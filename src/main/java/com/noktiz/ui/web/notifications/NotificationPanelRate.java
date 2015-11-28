/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.notifications;

import com.noktiz.domain.entity.notifications.NotificationRate;
import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.rate.RatingsPage;
import com.noktiz.ui.web.rate.stars.RealStars;
import com.noktiz.ui.web.thread.ThreadPage;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public final class NotificationPanelRate extends BaseNotificationPanel {

    RateManager rateManager = new RateManager();

    public NotificationPanelRate(String id, IModel<NotificationRate> obj) {
        super(id, obj);
        final Rate rate = obj.getObject().getRate();
//        final WebMarkupContainer action = new WebMarkupContainer("action");
//        add(action);
//        action.setOutputMarkupId(true);
        AjaxLink action1 = new IndicatingAjaxLink2("action1") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (rate.getThread() == null) {
                    try {
                        rateManager.createReplyThread(rate);
                    } catch (ResultException e) {
                        Logger.getLogger(this.getClass()).error(e);
                    }
                }
                setResponsePage(ThreadPage.class, ThreadPage.getThreadPageParam(rate.getThread()));
            }
        };
//        BookmarkablePageLink action2 = new BookmarkablePageLink("action1",RatingsPage.class,new PageParameters().add("contextId",rate.getContext().getId()));
//        action.add(action1);
        Model<String> actionTitle = Model.of("Reply");
        String titleString;
        if(rate.isShowSender())
            titleString = new StringResourceModel("notAnonymousResponse",null,new Object[]{rate.getContext().getTitle(),rate.getSender().getName()}).getObject();
        else
            titleString = new StringResourceModel("anonymousResponse",null,new Object[]{rate.getContext().getTitle()}).getObject();
        String messageString = rate.getComment();
        Image img = null;
        img = new Image("photo", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(
                        new UserFacade(rate.getSender(), !rate.isShowSender()),
                        ImageManagement.ImageSize.medium));
//            add(img);

//        RealStars stars = new RealStars("realStars",rate.getRate(),20);
//        add(stars);
        init(Model.of(titleString),Model.of(messageString),img,(rate.getRate()!=null)?Long.valueOf(rate.getRate()):null,action1,actionTitle);
    }
}
