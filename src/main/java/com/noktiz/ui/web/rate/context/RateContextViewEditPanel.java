package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.model.Result;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel;
import com.noktiz.ui.web.rate.RatingsPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by hasan on 2014-11-03.
 */
public class RateContextViewEditPanel extends BasePanel {
    private RateContext rateContext;
    RateContextManager rateContextManager = new RateContextManager();
    final Label title;
    final Label description;
    final Label inviteeString;
    Label createDate;
    Label inactive;

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/bootstrap-modal/js/bootstrap-modal.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/bootstrap-modal/js/bootstrap-modalmanager.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "/assets/plugins/bootstrap-modal/css/bootstrap-modal.css")));
    }

    public RateContextViewEditPanel(String id, final RateContext rateContext1, boolean small) {
        super(id);
        this.rateContext = rateContext1;
        final NotificationFeedbackPanel feedbackPanel = new NotificationFeedbackPanel("feedback");
        add(feedbackPanel);
        feedbackPanel.setOutputMarkupId(true);
        final WebMarkupContainer view = new WebMarkupContainer("view");
        add(view);
        view.setOutputMarkupId(true);

        title = new Label("title",new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject() {
                return  rateContext.getTitle();
            }

            @Override
            public void detach() {

            }
        });
        view.add(title);
        title.setEscapeModelStrings(true);
        title.setOutputMarkupId(true);
        description = new Label("description", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject() {
                return getDescription(rateContext);
            }

            @Override
            public void detach() {

            }
        });
        view.add(description);
        description.setEscapeModelStrings(false);
        description.setOutputMarkupId(true);
        WebMarkupContainer extra = new WebMarkupContainer("extra");
        view.add(extra);
        final Label period = new Label("period", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject() {
                setEscapeModelStrings(false);
                return getPeriodDescription(rateContext);
            }

            @Override
            public void detach() {

            }
        });
        extra.add(period);
        period.setEscapeModelStrings(false);
        period.setOutputMarkupId(true);
        inviteeString = new Label("invitee", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject() {
                return getInviteeDescription(rateContext);
            }

            @Override
            public void detach() {

            }
        });
        extra.add(inviteeString);
        inviteeString.setEscapeModelStrings(false);
        inviteeString.setOutputMarkupId(true);
        createDate = new Label("createDate", new AbstractReadOnlyModel<Object>() {
            @Override
            public String getObject() {
                return getCreateDescription(rateContext);
            }

            @Override
            public void detach() {

            }
        });
        extra.add(createDate);

//        AjaxLink sendInvite = new IndicatingAjaxLink2("sendInvite") {
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                Result result = rateContextManager.sendInvites(rateContext);
//                result.displayInWicket(feedbackPanel);
//                target.view.addfeedbackPanel);
//            }
//        };
//        view.add(sendInvite);
        createPublish(small, view);


        setOutputMarkupId(true);

        final WebMarkupContainer editModal = new WebMarkupContainer("modal");
        editModal.setOutputMarkupId(true);

        WebMarkupContainer showEdit = new WebMarkupContainer("showEdit"){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new AttributeModifier("href", "#" + editModal.getMarkupId()));
            }
        };
        view.add(showEdit);
        add(editModal);
        LazyLoadPanel editPanel = new LazyLoadPanel("editPanel", false, new RateContextEditPanel("content", new IModel<RateContext>() {
            @Override
            public RateContext getObject() {
                return rateContext;
            }

            @Override
            public void setObject(RateContext o) {
                rateContext = o;
            }

            @Override
            public void detach() {

            }
        }) {
            @Override
            public void onAjaxSave(AjaxRequestTarget target, Result result) {
                result.displayInWicket(feedbackPanel);
                target.add(feedbackPanel);
                if (result.result) {
                    target.appendJavaScript("$('#"+editModal.getMarkupId()+".close').click()");
                }
                target.add(view);
            }
        });
        editModal.add(editPanel);

        showEdit.add((new AjaxEventBehavior("onmouseup") {
            @Override
            protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                editPanel.load(ajaxRequestTarget);
            }
        }));

        Link showResult = new BookmarkablePageLink("showResult", RatingsPage.class,new PageParameters().add("contextId",rateContext.getId()).add("sub","rates"));
        view.add(showResult);

        inactive = new Label("inactive", new IModel<String>() {

            @Override
            public String getObject() {
                return rateContext.getEnable()?"":getString("Inactive");
            }

            @Override
            public void setObject(String o) {

            }

            @Override
            public void detach() {

            }
        });
        view.add(inactive);
        inactive.setOutputMarkupId(true);

        if(small) {
            extra.setVisible(false);
        }
    }

    public void createPublish(boolean small, final WebMarkupContainer view) {
        WebMarkupContainer publishModal=new WebMarkupContainer("publishModal");
        publishModal.setOutputMarkupId(true);
        add(publishModal);
        if(small){
            publishModal.setVisible(false);
            WebMarkupContainer publishButton = new WebMarkupContainer("publishButton");
            publishButton.setVisible(false);
            view.add(publishButton);
        }
        else {
            LazyLoadPanel publishPanel = new LazyLoadPanel("publishPanel", false, new PredefineQuestionPublisher("content", new IModel<RateContext>() {

                @Override
                public void detach() {

                }

                @Override
                public RateContext getObject() {
                    return rateContext;
                }

                @Override
                public void setObject(RateContext rateContext1) {
                    rateContext = rateContext1;
                }
            }) {
                @Override
                public void onAjaxSave(AjaxRequestTarget target, Result result) {
                    result.displayInWicket(feedbackPanel);
                    target.add(feedbackPanel);
                    if (result.result) {
                        target.appendJavaScript("$('#"+publishModal.getMarkupId()+" .close').click()");
                    }
                    target.add(view);
                }
            });
            publishModal.add(publishPanel);
            WebMarkupContainer publishButton = new WebMarkupContainer("publishButton") {
                //            @Override
                public void onClick(AjaxRequestTarget ajaxRequestTarget) {
//                publishPanel.load(ajaxRequestTarget);
                }

                @Override
                protected void onBeforeRender() {
                    super.onBeforeRender();
                    add(new AttributeModifier("href", "#" + publishModal.getMarkupId()));
                }

            };
            view.add(publishButton);
            publishButton.add((new AjaxEventBehavior("onmouseup") {
                @Override
                protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                    publishPanel.load(ajaxRequestTarget);
                }
            }));
        }
    }

    private String getCreateDescription(RateContext rateContext) {
        return new StringResourceModel("createdOn",null, new String[]{getFormattedUserDate(rateContext.getCreationDate(), "MMM dd yyyy")}).getObject();
    }

    private String getInviteeDescription(RateContext rateContext) {
        StringBuilder ret= new StringBuilder() ;
        ret.append(getString("availableTo"));
        boolean first=true;
        if(rateContext.getInvitedPersons().isIncludeMyTrustedFriends()) {
            ret.append("<b>").append(getString("YourTrustedFriends")).append("</b>");
            first=false;
//            return ret.toString();
        }

        for (User user: rateContext.getInvitedPersons().getIncludeUsers()){
            if(!first) {
                ret.append(", ");
            }
            ret.append("<b>");
            ret.append( user.getName() );
            ret.append("</b>");
            first=false;
        }
        return ret.toString();
    }

    private String getPeriodDescription(RateContext rateContext) {
        return new StringResourceModel("inviteeResponsePeriod",null, new Object[]{getString("SimplePeriod."+rateContext.getTimeBetweenRatings().toString())}).getObject();
    }

    private String getDescription(RateContext rateContext) {
        if(rateContext.getDescription() == null || rateContext.getDescription().isEmpty())
            return "";
        return rateContext.getDescription();
    }
}
