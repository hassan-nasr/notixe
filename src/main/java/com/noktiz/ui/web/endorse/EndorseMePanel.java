/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.endorse;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.entity.endorse.EndorseManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author hossein
 */
public  class EndorseMePanel extends BasePanel {

    private String contextObject;
    private TextField<String> context;
    private User receiverObj;
    private UserFacade userFacade;
    private EndorseManager endorseManager = new EndorseManager();
    Form form;

    NotificationFeedbackPanel feedback;

    public EndorseMePanel(String id, User user){
        super(id);
        receiverObj=user;
        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(EndorseMePanel.class, "stylesheet.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/select2/select2.min.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/select2/select2_metro.css")));
        response.render(OnDomReadyHeaderItem.forScript("$('.select2').select2();"));
    }

    IndicatingAjaxSubmitLink2 submit;

    private void init() {
        feedback = new NotificationFeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        userFacade = ((UserSession) getSession()).getUser();
        userFacade.refresh();
        form = new Form("form"){
            @Override
            protected void onSubmit() {
                return;
            }

        };
        add(form);
        submit = new IndicatingAjaxSubmitLink2("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                submitEndorse(target);
            }
        };
        form.add(submit);
        form.setDefaultButton(submit);
        context = new TextField("context", new IModel<String>() {
            @Override
            public String getObject() {
                return contextObject;
            }

            @Override
            public void setObject(String object) {
                contextObject = object;
            }

            @Override
            public void detach() {
            }

        });

        form.add(context) ;
//        context.setRequired(true);
        context.setOutputMarkupId(true);
        Label title = new Label("title", "As far as I know " + receiverObj.getName() + " is: ");
        title.setEscapeModelStrings(true);
        form.add(title);
//        form.add(new AjaxEventBehavior("onsubmit") {
//            @Override
//            protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
//                submitEndorse(ajaxRequestTarget);
//            }
//        });


    }

    private void submitEndorse(AjaxRequestTarget target) {
        Endorse newEndorse=new Endorse(contextObject,userFacade.getUser(),receiverObj);
        try{
            Result res= endorseManager.addEndorse(newEndorse);
            res.displayInWicket(feedback);
            contextObject="";
        } catch (ResultException e) {
            e.getResult().displayInWicket(feedback);
        }
        target.add(context,feedback);
    }

}




