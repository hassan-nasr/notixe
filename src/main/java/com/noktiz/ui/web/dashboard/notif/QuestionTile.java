package com.noktiz.ui.web.dashboard.notif;

import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.behavior.IntroBehavior;
import com.noktiz.ui.web.behavior.SelectPersonBehavior;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.TooltipBehavior;
import com.noktiz.ui.web.component.bootstrap.RateField;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.info.PopupInfo;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

/**
 * Created by hasan on 2014-11-24.
 */
public class QuestionTile extends BasePanel {

    String searchContent = " searchContent";

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("enableSlimScroll()"));
    }

    public QuestionTile(String id, final RateInviteNotificationPanel.QuestionPerson person,boolean displayIntro) {
        super(id);
        add(feedbackPanel);
        if(displayIntro){
            add(new IntroBehavior("intro.QuestionIntro",20, IntroBehavior.Position.front));
        }
        Form form = new Form("form");
        add(form);
        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        form.add(wmc);
        if (person.isSeparator()) {
            wmc.add(new AttributeAppender("class", " headerTile "));
        } else {
            wmc.add(new AttributeAppender("class", " searchElement "));
        }
        setOutputMarkupId(true);
        CheckBox select = new CheckBox("select", person.isSelect());
        wmc.add(select);
//                wmc.add(new ItemBehavior());
        select.setOutputMarkupId(true);
        {
            String imageLinkUrl = person.getImageLink();
            if (imageLinkUrl == null) {
                imageLinkUrl = "";
            }
            ExternalLink imageLink = new ExternalLink("imgLink", imageLinkUrl);
            wmc.add(imageLink);
            if (imageLinkUrl == "") {
                imageLink.setEnabled(false);
            }

            Image image = new Image("img", "");
            imageLink.add(image);
            String imageUrl = person.getImageUrl();
            image.add(new AttributeModifier("src", imageUrl));

            if (imageUrl == null) {
                imageLink.setVisible(false);
            }
        }
        {
            String linkUrl = person.getTitleLink();
            if (linkUrl == null) {
                linkUrl = "";
            }
            ExternalLink link = new ExternalLink("titleLink", linkUrl) {

                @Override
                protected void onConfigure() {
                    super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                    setVisible(person.getTitleEnable());
                }

            };
            wmc.add(link);
            if (linkUrl == "") {
                link.setEnabled(false);
            }
            Label title = new Label("title", person.getTitle()){
                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    tag.put("title",getDefaultModelObjectAsString());
                }
            };
            link.add(title);
            title.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if ((Boolean)person.getSearchInTitle().getObject()) {
                        return searchContent;
                    }
                    return "";
                }
            }));

        }
        {
            String linkUrl = person.getDescLink();
            if (linkUrl == null) {
                linkUrl = "";
            }
            ExternalLink link = new ExternalLink("descLink", linkUrl) {

                @Override
                protected void onConfigure() {
                    super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                    setVisible(person.getDescEnable());
                }

            };
            wmc.add(link);
            if (linkUrl == "") {
                link.setEnabled(false);
            }

            Label desc = new Label("desc", person.getDesc());

            link.add(desc);

            desc.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if ((Boolean)person.getSearchInDesc().getObject()) {
                        return searchContent;
                    }
                    return "";
                }
            }));
        }
        {
            TextArea inputbox = new TextArea("inputbox", person.getInputBox()) {

                @Override
                protected void onConfigure() {
                    super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                    setVisible(person.getInputBoxEnable());
                }
            };
            if(person.getInputBoxLength() != null){
                inputbox.add(new AttributeModifier("maxlength",person.getInputBoxLength()));
            }
            wmc.add(inputbox);
        }
        {
            String linkUrl = person.getSubtitleLink();
            if (linkUrl == null) {
                linkUrl = "";
            }
            ExternalLink link = new ExternalLink("subtitleLink", linkUrl);
            wmc.add(link);
            if (linkUrl == "") {
                link.setEnabled(false);
            }

            Label subtitle = new Label("subtitle", person.getSubTitle());
            link.add(subtitle);
            if(displayIntro){
                subtitle.add(new IntroBehavior("intro.VisibilityInQuestionIntro",21, IntroBehavior.Position.auto));
            }
            subtitle.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if ((boolean)person.getSearchInSubtitle().getObject()) {
                        return searchContent;
                    }
                    return "";
                }
            }));
        }
        wmc.add(new AttributeAppender("class", person.getBGClass()));
        final String commonFriendsCount = person.getNumberFlag();
        Label commonFriend = new Label("commonFriend", commonFriendsCount);
        commonFriend.add(new TooltipBehavior( new StringResourceModel("sharedFriendInvited",null,new Object[]{commonFriendsCount}), TooltipBehavior.Position.bottom));
        wmc.add(commonFriend);
        if(displayIntro){
            commonFriend.add(new IntroBehavior("intro.CommonFriendsCountIntro",22, IntroBehavior.Position.auto));
        }
        Label period = new Label("period", person.getNotificationRateInvite()
                .getRateContext().getTimeBetweenRatings().getFriendlyName());
        wmc.add(period);
        if(displayIntro ) {
            if (person.getNotificationRateInvite()
                    .getRateContext().getTimeBetweenRatings().getFriendlyName().length() > 0) {
                period.add(new IntroBehavior("intro.PeriodicalQuestionIntro", 23, IntroBehavior.Position.auto));
            }
            else{
                period.add(new IntroBehavior("intro.PeriodicalQuestionIntro", 23, IntroBehavior.Position.front));
            }
        }
        period.add(new TooltipBehavior(new StringResourceModel("periodicRequestFromFriend",null,new Object[]{
                person.getNotificationRateInvite().getSender().getName(),
                new StringResourceModel("SimplePeriod."+person.getNotificationRateInvite().getRateContext().getTimeBetweenRatings().toString(),null)}),
                TooltipBehavior.Position.bottom));

        ListView actions = new ListView("actions", person.getActions()) {
                    @Override
                    protected void populateItem(ListItem actionItem) {
                        final PersonListProvider.IPerson.IAction iaction = (PersonListProvider.IPerson.IAction) actionItem.getDefaultModelObject();
                        actionItem.setRenderBodyOnly(true);
                        AjaxSubmitLink action = new IndicatingAjaxSubmitLink2("action") {
                            @Override
                            public void onSubmit(AjaxRequestTarget target, Form form) {
                                boolean onAction = iaction.onAction(target, this);
                                if (onAction) {
                                    target.add(QuestionTile.this);
                                }
                                target.add(feedbackPanel);
                            }
                            @Override
                            protected void onConfigure() {
                                super.onConfigure();
                                setEnabled(iaction.isActionEnabled());
                                setVisible(iaction.isActionVisible());
                                if(!iaction.isActionEnabled()){
                                    add(new AttributeAppender("class"," disabled"));
                                }
                            }
                        };
                        actionItem.add(action);
                        action.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                            @Override
                            public Object getObject() {
                                if (iaction.getSearchInAction().getObject()) {
                                    return searchContent;
                                }
                                return "";
                            }
                        }));
                        action.add(new AttributeAppender("class", iaction.getButtonClass()));
                        Label actionTitle = new Label("actionTitle", iaction.getActionTitle());
                        action.add(actionTitle);
                    }
                };
        wmc.add(actions);
        actions.setRenderBodyOnly(true);
        ListView otherActionsList = new ListView("otherActionsList", person.getOtherActions()) {
            @Override
            protected void populateItem(ListItem actionItem) {
                final PersonListProvider.IPerson.IAction iaction = (PersonListProvider.IPerson.IAction) actionItem.getDefaultModelObject();
                AjaxSubmitLink action = new IndicatingAjaxSubmitLink2("actionLink") {
                    @Override
                    public void onSubmit(AjaxRequestTarget target, Form form) {
                        boolean onAction = iaction.onAction(target, QuestionTile.this);
                        if (onAction) {
                            target.add(QuestionTile.this);
                        }
                        target.add(feedbackPanel);
                    }

                    @Override
                    public boolean isEnabled() {
                        return iaction.isActionEnabled();
                    }
                };
                actionItem.add(action);
                action.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        if (iaction.getSearchInAction().getObject()) {
                            return searchContent;
                        }
                        return "";
                    }
                }));
                action.add(new AttributeAppender("class", iaction.getButtonClass()));
                action.setBody(iaction.getActionTitle());
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                List object = (List) person.getOtherActions().getObject();
                setVisible(!object.isEmpty());
            }
        };
        wmc.add(otherActionsList);

    }

}
