package com.noktiz.ui.web.dashboard.notif;

import com.noktiz.domain.entity.rate.*;
import com.noktiz.domain.entity.subscribe.RateSubscriptionManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.bootstrap.BootstrapAjaxCheckBox;
import com.noktiz.ui.web.info.PopupInfo;
import com.noktiz.ui.web.rate.answer.RateFriendContextPanel;
import com.noktiz.ui.web.widget.SimplePeopleList;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasan on 2014-11-05.
 */
public class RateInviteExtendedNotificationPanel extends BasePanel {
    private final UserFacade senderFacade;
    NotificationRateInvite notificationRateInvite;
    RateManager rateManager = new RateManager();
    List<Rate> previousRates = new ArrayList<>();
    Rate currentRate = null;
    RateContext rateContext;
//    RateSubscription subscription= null;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    public RateInviteExtendedNotificationPanel(String id, Model<NotificationRateInvite> model) {
        super(id);
        notificationRateInvite = model.getObject();
        rateContext= notificationRateInvite.getRateContext();
        senderFacade = new UserFacade(rateContext.getUser());
//        subscription = rateSubscriptionManger.load(getUserInSite().getUser(),rateContext);
        Image image = new Image("image", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(senderFacade, ImageManagement.ImageSize.large));
        add(image);
        Label title = new Label("title",rateContext.getTitle());
        add(title);
        Label description = new Label("description",rateContext.getDescription());
        add(description);
        Label header = new Label("header",getHeader(rateContext));
        header.setEscapeModelStrings(true);
        add(header);
        SimplePeopleList invitees= new SimplePeopleList("invitees",new ArrayList(rateContext.getInvitedUsers()),2,true,"invitees:");
        add(invitees);
        previousRates = rateManager.loadLastRatesDoneByUserInContext(getUserInSite(),rateContext,0,5);
        if(previousRates.size()>0 && rateContext.isInCurrentRange(previousRates.get(0)))
            currentRate=previousRates.remove(0);
        else
            currentRate= new Rate(getUserInSite().getUser(),null,null,rateContext,null);

        RateFriendContextPanel answer = new RateFriendContextPanel("answer",notificationRateInvite,currentRate,false);
        add(answer);
        WebMarkupContainer history = new WebMarkupContainer("history");
        add(history);
        if(previousRates.size()>0) {
            String acalss = RandomStringUtils.random(10,false,true);
            final WebMarkupContainer accordionBody = new WebMarkupContainer("accordion-body");
            history.add(accordionBody);
            accordionBody.add(new AttributeAppender("class", " " + acalss + " "));
            final WebMarkupContainer accordionHead = new WebMarkupContainer("accordion-head");/*{
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new AttributeModifier("href", accordionBody.getOutputMarkupId()));
            }
        };*/
            accordionHead.add(new AttributeAppender("href", "." + acalss));
            history.add(accordionHead);
            ListView previousRatingsList = new ListView("previousRates", previousRates) {
                @Override
                protected void populateItem(ListItem item) {
                    Rate prev = (Rate) item.getDefaultModelObject();
                    item.add(new RateFriendContextPanel("prev", notificationRateInvite, prev, true));
                }
            };
            accordionBody.add(previousRatingsList);
            answer.add(new AttributeAppender("class", " noradB "));
        }else
            history.setVisible(false);

        CheckBox subscriptionCheckbox = new BootstrapAjaxCheckBox("subscribe",Model.of(notificationRateInvite.isSubscribed())) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                notificationRateInvite.setSubscribed((Boolean)getDefaultModelObject());
                notificationRateInvite.update();
            }
        };
        add(subscriptionCheckbox);
        PopupInfo subscribeInfo = new PopupInfo("infoSubscribe", new StringResourceModel("weWillNotify",null), "left", null);
        add(subscribeInfo);
        if(rateContext.getTimeBetweenRatings().equals(SimplePeriod.SingleTime)){
            subscriptionCheckbox.setVisible(false);
            subscribeInfo.setVisible(false);
        }
        if(notificationRateInvite.isSubscribed()){
            subscribeInfo.setVisible(false);
        }
    }

    private IModel<String> getHeader(RateContext rateContext) {
//        StringBuilder ret = new StringBuilder();
        return new StringResourceModel("periodicRequestFromFriend",null,new Object[]{
                rateContext.getUser().getName(),
                new StringResourceModel("SimplePeriod."+rateContext.getTimeBetweenRatings().toString(),null)});
    }
}
