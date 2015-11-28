/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.thread;

import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.behavior.IntroBehavior;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.user.SingleUserImageWithLinkPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author Hossein
 */
public abstract class ThreadInfoPanel extends BasePanel {

    boolean blockstate = false;

    public ThreadInfoPanel(String id, IModel<ThreadFacade> model) {
        super(id, model);
        setOutputMarkupId(true);
        final WebMarkupContainer wmc= new WebMarkupContainer("wmc");
        add(wmc);
        final WebMarkupContainer feedbackMVC= new WebMarkupContainer("feedbackMVC");
        wmc.add(feedbackMVC);
        feedbackMVC.setOutputMarkupId(true);
        NotificationFeedbackPanel feedback= new NotificationFeedbackPanel("feedback");
        feedbackMVC.add(feedback);
        wmc.setOutputMarkupId(true);
        Label friendName = new Label("friendName", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return getObj().getParticipant().getName();
            }
        });
        wmc.add(friendName);
        Label invisiblity = new Label("invisiblity", new AbstractReadOnlyModel() {
            @Override
            public String getObject() {
                if (getObj().getMe().equals(getObj().getStarter())) {
                    return getObj().getThread().isStarterVisible() ? getString("Visible") : getString("Anonymous");
                } else {
                    return getObj().getThread().isTargetVisible() ? getString("Visible") : getString("Anonymous");
                }
            }
        });
        wmc.add(invisiblity);
        invisiblity.add(new AttributeAppender("class", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                if (getObj().getMe().equals(getObj().getStarter())) {
                    return getObj().getThread().isStarterVisible() ? "label label-warning" : "label label-important";
                } else {
                    return getObj().getThread().isTargetVisible() ? "label label-warning" : "label label-important";
                }
            }
        }));
        WebMarkupContainer becomeVisibleWMC = new WebMarkupContainer("becomeVisibleWMC") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (getObj().getMe().equals(getObj().getStarter())) {
                    setVisible(!getObj().getThread().isStarterVisible());
                } else {
                    setVisible(!getObj().getThread().isTargetVisible());
                }
            }
        };
        wmc.add(becomeVisibleWMC);
        becomeVisibleWMC.setOutputMarkupId(true);
        final WebMarkupContainer becomeVisibleModal= new WebMarkupContainer("becomeVisibleModal"){

            @Override
            protected void onConfigure() {
                super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                if (getObj().getMe().equals(getObj().getStarter())) {
                    setVisible(!getObj().getThread().isStarterVisible());
                } else {
                    setVisible(!getObj().getThread().isTargetVisible());
                }
            }
            
        };
        add(becomeVisibleModal);
        becomeVisibleModal.setOutputMarkupId(true);
        WebMarkupContainer becomeVisibleButton = new WebMarkupContainer("becomeVisibleButton"){

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new AttributeModifier("href", "#" + becomeVisibleModal.getMarkupId()));
                
            }
            
        };
        becomeVisibleWMC.add(becomeVisibleButton);
        AjaxLink becomeVisibleAction = new AjaxLink("becomeVisibleAction"){

            @Override
            public void onClick(AjaxRequestTarget target) {
                Result becomeVisible = getObj().becomeVisible();
                if(becomeVisible.result){
                    target.add(wmc);
                }
                becomeVisible.displayInWicket(ThreadInfoPanel.this);
                target.add(feedbackMVC);
                target.appendJavaScript("jQuery('.close').click()");
            }
            
        };
        becomeVisibleModal.add(becomeVisibleAction);
        WebMarkupContainer myImage = new WebMarkupContainer("myImage");
        myImage.add(new AttributeModifier("src", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return urlFor(ImageManagement.getUserImageResourceReferece(),
                        ImageManagement.getUserImageParameter(
                                getObj().getMe(), ImageManagement.ImageSize.medium));

            }
        }));
        wmc.add(myImage);
        WebMarkupContainer friendImage = new WebMarkupContainer("friendImage");
        friendImage.add(new AttributeModifier("src", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return urlFor(ImageManagement.getUserImageResourceReferece(),
                        ImageManagement.getUserImageParameter(getObj().getParticipant(), ImageManagement.ImageSize.medium));
            }

        }));
        wmc.add(friendImage);
        AjaxLink block = new AjaxLink("block") {
            @Override
            public void onClick(AjaxRequestTarget art) {
                if (blockstate) {
                    Result result = getObj().block(getObj().getMe(), true);
                    result.displayInWicket(this);
                    onBlock(art);
                    art.add(wmc);
                } else {
                    Result result = getObj().unBlock();
                    result.displayInWicket(this);
                    onUnblock(art);
                    art.add(wmc);
                }

            }

            @Override
            public void onConfigure() {

                if (getObj().getParticipant().isAnonymous()) {
                    setVisible(true);
                } else {
                    setVisible(false);
                }

            }

        };
        wmc.add(block);
        block.setOutputMarkupId(true);

        Label blockMessage = new Label("blockMessage", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                Block blocked = getObj().getMyBlock();
                if (blocked == null) {
                    blockstate = true;
                    return getString("action.block");
                } else {
                    blockstate = false;
                    return getString("unblock");
                }
            }
        });
        block.add(blockMessage);

        AjaxLink thanks = new AjaxLink("thanks") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Result result = getObj().thanks();
                if (result.result) {
                    target.add(this);
                }
            }

            @Override
            public boolean isEnabled() {
                return !getObj().didIThank();
            }

            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(getObj().canIThank(getObj().getParticipant()));
            }
        };
        wmc.add(thanks);
        thanks.setOutputMarkupId(true);
        Label thanksMessage = new Label("thanksMessage", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                if (getObj().didIThank()) {
                    return "thanked";
                } else {
                    return "thank";
                }
            }
        });
        thanks.add(thanksMessage);
        Label friendThanks = new Label("friendThanks", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return new StringResourceModel("thankedMessage", null,getObj().getParticipant().getName()).getObject();
            }
        }) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(getObj().doParticipantThank());
            }

        };
        wmc.add(friendThanks);
        WebMarkupContainer blocked = new WebMarkupContainer("blocked") {
            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(getObj().getParticipantBlock() != null);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
        };
        wmc.add(blocked);
        WebMarkupContainer iBlocked = new WebMarkupContainer("iBlocked") {
            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(getObj().getMyBlock() != null);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
        };
        wmc.add(iBlocked);
        WebMarkupContainer blockedByCompl = new WebMarkupContainer("blockedByCompl") {
            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(getObj().getParticipantBlock() == null && getObj().getParticipantBlockBy() != null && getObj().isBlockedCompletely() == true);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
        };
        wmc.add(blockedByCompl);
        WebMarkupContainer blockedBy = new WebMarkupContainer("blockedBy") {
            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(getObj().getParticipantBlock() == null && getObj().getParticipantBlockBy() != null && getObj().isBlockedCompletely() == false);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
        };
        wmc.add(blockedBy);
        if(!UserProperties.TRUE.equals(getUserInSite().getProperty(UserProperties.AnonymousConversation_INTRODUCTION_SEEN))) {
            if (getObj().getParticipant().isAnonymous()) {
                getUserInSite().setProperty(UserProperties.AnonymousConversation_INTRODUCTION_SEEN, UserProperties.TRUE);
                thanks.add(new IntroBehavior("intro.thanks",3, IntroBehavior.Position.auto));
                block.add(new IntroBehavior("intro.block",2, IntroBehavior.Position.auto));
                block.add(new IntroBehavior("intro.intro",1, IntroBehavior.Position.front));

            }
        }
    }

    private ThreadFacade getObj() {
        return (ThreadFacade) getDefaultModelObject();
    }

    public abstract void onBlock(AjaxRequestTarget art);

    public abstract void onUnblock(AjaxRequestTarget art);
}
