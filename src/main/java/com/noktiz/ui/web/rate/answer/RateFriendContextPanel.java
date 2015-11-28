/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.rate.answer;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.*;
import com.noktiz.domain.model.Result;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.AutoGrowTextArea;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.bootstrap.RateField;
import com.noktiz.ui.web.info.PopupInfo;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.*;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 *
 * @author hossein
 */
public  class RateFriendContextPanel extends BasePanel {

    private final User receiverObj;
    private final NotificationRateInvite rateInvite;

    private Rate currentRate;
    List<Rate> previousRates;
    RateManager rateManager = new RateManager();
    NotificationFeedbackPanel feedback;
    private RateContext rateContext;
    TextField<Double> rateNum;


    public RateFriendContextPanel(String id, final NotificationRateInvite rateInvite,Rate currentRate1, boolean readOnly){
        super(id);
        this.currentRate = currentRate1;
        add(feedbackPanel);
        this.rateContext = rateInvite.getRateContext();
        this.rateInvite = rateInvite;
        receiverObj= rateContext.getUser();

        Form submitRateForm = new Form("rateForm");
        add(submitRateForm);

        TextArea comment = new AutoGrowTextArea("comment", new PropertyModel(currentRate, "comment"));
        submitRateForm.add(comment);
        Label commentRO = new Label("commentRO", currentRate.getComment());
        submitRateForm.add(commentRO);
        if(readOnly) {
            comment.setVisible(false);
        }
        else {
            commentRO.setVisible(false);
        }
        rateNum = new RateField("rate", new IModel<Double>() {
            @Override
            public Double getObject() {
                if (currentRate.getRate() == null)
                    return null;
                return currentRate.getRate().doubleValue();
            }

            @Override
            public void setObject(Double object) {
                Integer val = null;
                if (object != null) {
                    val = (int)Math.round(object);
                    if(val>5 || val <1)
                        val=null;
//                    val = Math.min(val, 5);
//                    val = Math.max(val, 1);
                }
                currentRate.setRate(val);
            }

            @Override
            public void detach() {
            }
        });
        if(readOnly){
            rateNum.add(new AttributeAppender("disabled","" ));
        }
        submitRateForm.add(rateNum);
        AjaxSubmitLink submitRate = new IndicatingAjaxSubmitLink2("submitRate") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                currentRate.setDate(new Date());
                Result result = rateManager.rateFriend(currentRate, rateInvite);
//                Result result = new Result(new Result.Message("Your response saved successfully", Result.Message.Level.success), true);
                result.displayInWicket(feedbackPanel);
                target.add(feedbackPanel);
            }
        };
        submitRateForm.add(submitRate);
        if(readOnly)
            submitRate.setVisible(false);

        Label date = new Label("date", new StringResourceModel("respondDate",null, new Object[]{getFormattedUserDate(currentRate.getDate())}));
        submitRateForm.add(date);

        if(currentRate.getDate()==null){
            date.setVisible(false);
        }
        if(readOnly){
            submitRateForm.add(new WebMarkupContainer("editInfo").setVisible(false));
        }
        else {
            String editDate = getFormattedUserDate(rateContext.getNextInviteDate(), "MMM dd, HH:mm");
            String info;
            if(rateContext.getTimeBetweenRatings().equals(SimplePeriod.SingleTime))
                info = getString("you can always edit your response");
            else
                info = new StringResourceModel("you can edit your response until ", null, new Object[]{editDate}).getObject();
            submitRateForm.add(new PopupInfo("editInfo", info, "left",null));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/star-rating/css/star-rating.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/bootstrap/star-rating/js/star-rating.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(RateFriendContextPanel.class, "RateFriendContextPanel.js")));
        response.render(OnDomReadyHeaderItem.forScript("enableChangeWidth()"));

    }
}




