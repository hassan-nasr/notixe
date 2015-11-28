/*
 * HeaderPanel.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
package com.noktiz.ui.web.header;

import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.entity.notifications.*;
import com.noktiz.domain.entity.rate.NotificationRateInvite;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.auth.SignOutPage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.behavior.DisableOnClassBehavior;
import com.noktiz.ui.web.behavior.IntroBehavior;
import com.noktiz.ui.web.notifications.*;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 * @author hossein
 * @version
 */
public class HeaderPanel extends BasePanel {

    /**
     * Construct.
     *
     * @param componentName name of the component
     * @param exampleTitle title of the example
     */
    List<BaseNotification> notifications;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(HeaderPanel.class, "HeaderPanel.css")));

//        response.render(CssHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/jquery-slimscroll/jquery.slimscroll.min.js")));
//        response.render(OnDomReadyHeaderItem.forScript("$('.dropdown-menu').slimscroll({\n" +
//"  height: '500px',\n" +
//"  width: '300px'\n" +
//"});"));

    }

    public HeaderPanel(String componentName, String selectedPage) {
        super(componentName);
        WebMarkupContainer header = new TransparentWebMarkupContainer("header");
        add(header);
        header.add(new AttributeAppender("class", " " + (isMobile ? "container-fluid" : "container")));
        selectedPage = selectedPage == null ? "" : selectedPage;
        WebMarkupContainer friendsLI = new WebMarkupContainer("friendsLI");
        add(friendsLI);
        WebMarkupContainer conversationLI = new WebMarkupContainer("threadLI");
        add(conversationLI);
        WebMarkupContainer ratingLI = new WebMarkupContainer("ratingLI");
        add(ratingLI);
        WebMarkupContainer homeLI = new WebMarkupContainer("homeLI");
        add(homeLI);
        if(!UserProperties.TRUE.equals(getUserInSite().getProperty(UserProperties.INTRODUCTION_SEEN))){
            getUserInSite().setProperty(UserProperties.INTRODUCTION_SEEN,UserProperties.TRUE);
            homeLI.add(new IntroBehavior("intro.start",-1, IntroBehavior.Position.front));
            friendsLI.add(new IntroBehavior("intro.friends", 0, IntroBehavior.Position.down));
            conversationLI.add(new IntroBehavior("intro.threads", 1, IntroBehavior.Position.down));
            ratingLI.add(new IntroBehavior("intro.questions", 2, IntroBehavior.Position.down));
            homeLI.add(new IntroBehavior("intro.home", 3, IntroBehavior.Position.down));
            homeLI.add(new IntroBehavior("intro.end",4, IntroBehavior.Position.front));

        }
//        WebMarkupContainer profileLI = new TransparentWebMarkupContainer("profileLI");
//        add(ratingLI);
        WebMarkupContainer toActive = null;
        if (selectedPage.equalsIgnoreCase("friends")) {
            toActive = friendsLI;
        } else if (selectedPage.equalsIgnoreCase("conversation")) {
            toActive = conversationLI;
        } else if (selectedPage.equalsIgnoreCase("rating")) {
            toActive = ratingLI;
        } else if (selectedPage.equalsIgnoreCase("home")) {
            toActive = homeLI;
        }

//        else if (selectedPage.equalsIgnoreCase("profile"))
//            toActive = profileLI;
        if (toActive != null) {
            active(toActive);
        }

        Image image = new Image("logo", new PackageResourceReference(Application.class, "Notixe.png"));
        add(image);
        final UserFacade user = ((UserSession) getSession()).getUser();
        notifications = user.loadMyNotifications();
        final String enable = RandomStringUtils.randomAlphabetic(10);
        final WebMarkupContainer wmc = new WebMarkupContainer("notifications") {
            @Override
            public void renderHead(IHeaderResponse response) {
                response.render(JavaScriptContentHeaderItem.forReference(
                        new PackageResourceReference(HeaderPanel.class, "HeaderPanel.js")));
                String markupId = getMarkupId();
                StringBuilder sb = new StringBuilder();
                sb.append("openNotif('").append(markupId).append("','").
                        append(enable).append("');");
                response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
                response.render(OnDomReadyHeaderItem.forScript("enablescroll()"));
            }
        };
        add(wmc);
//        AjaxLink refreshNotif = new AjaxLink("refreshNotif") {
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                notifications = user.loadMyNotifications();
//                target.add(wmc);
//            }
//        };
//        add(refreshNotif);
        wmc.add(new DisableOnClassBehavior(enable, "open"));
//        refreshNotif.add(new CallPeriodically(120000, "NOTFICATION_REFRESH_ENABLE", false,"NULL"));

        wmc.setOutputMarkupId(true);
        AjaxLink seen = new AjaxLink("seen") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (BaseNotification baseNotification : notifications) {
                    baseNotification.markAsRead();
                }
                notifications = new ArrayList();
                target.add(wmc);
            }

            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(!notifications.isEmpty());
            }
        };
        wmc.add(seen);
        WebMarkupContainer nonotif = new WebMarkupContainer("nonotif") {
            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(notifications.isEmpty());
            }
        };
        wmc.add(nonotif);
        final Label notifCount = new Label("notifCount", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                if (!notifications.isEmpty()) {
                    return notifications.size();
                } else {
                    return "";
                }
            }
        });
        notifCount.setOutputMarkupId(true);
//        seen.add(notifCount);
        wmc.add(notifCount);
        ListView notifs = new ListView("notifs", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return notifications;
            }
        }) {
            @Override
            protected void populateItem(ListItem item) {
                BaseNotification obj = (BaseNotification) item.getDefaultModelObject();
                if (obj instanceof NotificationAddFriend) {
                    NotificationAddFriend naf = (NotificationAddFriend) obj;
                    NotificationPanelAddFriend npaf = new NotificationPanelAddFriend("notif", new Model(naf));
                    npaf.setNotificationCountPanel(notifCount);
                    item.add((Panel) npaf);
                } else if (obj instanceof NotificationThanks) {
                    NotificationThanks nt = (NotificationThanks) obj;
                    NotificationPanelThanks npt = new NotificationPanelThanks("notif", new Model(nt));
                    npt.setNotificationCountPanel(notifCount);
                    item.add(npt);
                } else if (obj instanceof NotificationRate) {
                    NotificationRate nt = (NotificationRate) obj;
                    NotificationPanelRate npt = new NotificationPanelRate("notif", new Model(nt));
                    npt.setNotificationCountPanel(notifCount);
                    item.add(npt);
                } else if (obj instanceof NotificationEndorse) {
                    NotificationEndorse nt = (NotificationEndorse) obj;
                    NotificationPanelEndorse npt = new NotificationPanelEndorse("notif", new Model(nt));
                    npt.setNotificationCountPanel(notifCount);
                    item.add(npt);
                } else if (obj instanceof NotificationNewMessage) {
                    NotificationNewMessage nt = (NotificationNewMessage) obj;
                    NotificationPanelNewMessage npt = new NotificationPanelNewMessage("notif", new Model(nt));
                    npt.setNotificationCountPanel(notifCount);
                    item.add(npt);
                } else if (obj instanceof NotificationRateInvite) {
//                    NotificationEndorse nt= (NotificationEndorse)obj;
//                    NotificationPanelEndorse npt = new NotificationPanelEndorse("notif", new Model(nt));
                    item.add(new WebMarkupContainer("notif").setVisible(false));
                    item.setVisible(false);
                } else {
                    item.add(new Label("notif", Model.of(obj.toString())));
                }
            }
        };
        wmc.add(notifs);
        final ResourceReference iRR = ImageManagement.getUserImageResourceReferece();
        final PageParameters iP = ImageManagement.getUserImageParameter(user, ImageManagement.ImageSize.small);
        final PageParameters biP = ImageManagement.getUserImageParameter(user, ImageManagement.ImageSize.large);
        Image img = new Image("myphoto", iRR, iP);
        add(img);
//        Image bigImg = new Image("mybigphoto", iRR, biP);
//        add(bigImg);
        Label name = new Label("name", user.getName());
        add(name);
        ExternalLink settings = new ExternalLink("settings", "/settings");
        add(settings);
        Link logout = new Link("logout") {
            @Override
            public void onClick() {
                setResponsePage((Class)SignOutPage.class);
            }
        };
        add(logout);

    }

    private void active(WebMarkupContainer toActive) {
        toActive.add(new AttributeAppender("class", " active"));
    }
}
