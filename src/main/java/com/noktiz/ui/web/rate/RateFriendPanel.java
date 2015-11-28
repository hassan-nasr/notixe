/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.rate;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.*;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.behavior.PressKeyEnableBehavior;
import com.noktiz.ui.web.component.ChangePropertyInput;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.typeAhead.TypeAheadBehavior;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.*;

/**
 *
 * @author hossein
 */
public  class RateFriendPanel extends BasePanel {

    private final RateManager rateManager = new RateManager();
    TextField receiver;
    TextField<String> comment;
    DropDownChoice<Integer> rateNum;
    User receiverObj;
    String commentObj;
    Integer rateNumObj = 0 ;
    UserFacade userFacade;
    Map<String, User> idToUser;
    List<String> othersFriends;
    ChangePropertyInput dir;
    CheckBox anonymous;

    Form form;

    NotificationFeedbackPanel feedback;


    public RateFriendPanel(String id) {
        super(id);
        init();
    }
    public RateFriendPanel(String id, User user){
        super(id);
        receiverObj=user;
        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(RateFriendPanel.class, "stylesheet.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/select2/select2.min.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/select2/select2_metro.css")));
        response.render(OnDomReadyHeaderItem.forScript("$('.select2').select2();"));
    }


    private void init() {
        feedback = new NotificationFeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        userFacade = ((UserSession) getSession()).getUser();
        userFacade.refresh();
        form = new Form("form");
        add(form);
        List<Friendship> othersFriendships = userFacade.getOthersFriends();
        othersFriends = new ArrayList<>();
        idToUser = new HashMap<>();
        for (Friendship friendship : othersFriendships) {
            String text = UserToDisplayText(friendship.getFriendshipOwner());
            idToUser.put(text, friendship.getFriendshipOwner());
        }
        receiver = new TextField("receiver", new IModel() {
            @Override
            public Object getObject() {
                return UserToDisplayText(receiverObj);
            }

            @Override
            public void setObject(Object object) {
                receiverObj = idToUser.get(object);
            }

            @Override
            public void detach() {
            }
        });
        receiver.add(new TypeAheadBehavior() {
            @Override
            protected Collection<String> getChoices(String userInput) {
                return idToUser.keySet();
            }
        });
        form.add(receiver);
        String enableVar = RandomStringUtils.randomAlphabetic(10);
        comment = new TextField<String>("comment", new IModel<String>() {
            @Override
            public String getObject() {
                return commentObj;
            }

            @Override
            public void setObject(String object) {
                commentObj = object;
            }

            @Override
            public void detach() {
            }
        });
        form.add(comment);
        comment.add(new PressKeyEnableBehavior(enableVar));
        rateNum = new DropDownChoice<>("rate", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return rateNumObj;
            }

            @Override
            public void setObject(Integer object) {
                rateNumObj = object;
            }

            @Override
            public void detach() {
            }
        },Arrays.asList(0,1,2,3,4,5));
        form.add(rateNum);
        anonymous = new CheckBox("anonymously", Model.of(true));
        form.add(anonymous);
        final DropDownChoice contextDropDown = new DropDownChoice("context", new Model(new RateContext("hi","hi",userFacade.getUser())), userFacade.getUser().getRateContexts(),new ChoiceRenderer(){
            @Override
            public Object getDisplayValue(Object object) {
                return ((RateContext) object).getTitle();
            }

            @Override
            public String getIdValue(Object object, int index) {
                return ((RateContext) object).getId().toString();
            }
        });

        form.add(contextDropDown);
        AjaxSubmitLink send = new IndicatingAjaxSubmitLink2("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    UserFacade reciver = getReciver();
                    target.add(feedback);
                    if(reciver == null){
                        error(getString("error.receiverNotFound"));
                        return;
                    }
                    if((Integer) rateNum.getDefaultModelObject() == 0){
                        error(getString("error.selectRating"));
                        return;
                    }
                    Rate rate = new Rate(getUserInSite().getUser(), (Integer) rateNum.getDefaultModelObject(), comment.getDefaultModelObjectAsString(), (RateContext) contextDropDown.getDefaultModelObject() , new Date());
                    rate.setShowSender(!(Boolean ) anonymous.getDefaultModelObject());
                    Result r = rateManager.rateFriend(rate,null);
                    if(r.result){
//                        success(getString"You've successfully rate your friend");
                        clearForm();
                        target.add(form);
                    }
                    else {
                        r.displayInWicket(this);
                    }

                } finally {
//                    onAjax(target);
                }
            }
        };
        form.add(send);

    }

    private void clearForm() {
        commentObj ="";
        rateNumObj=4;
        receiverObj=null;
        anonymous = new CheckBox("anonymously", Model.of(true));
        form.addOrReplace(anonymous);
    }

    private String getComment() {
        return comment.getDefaultModelObjectAsString();
    }



    private UserFacade getReciver() {
        User reciverStr = (User) receiverObj;
        if (reciverStr == null) {
            return null;
        }
        return UserFactory.loadUserWithEmailId(EmailAddressUtils.normalizeEmail(reciverStr.getEmail()), true).object;
    }
/*
    protected abstract void onAjax(AjaxRequestTarget target);

    protected abstract void onFinish(AjaxRequestTarget target);*/

    private String UserToDisplayText(User user) {
        if (user != null) {
            return user.getName() + " (" + user.getEmail() + ")";
        }
        return "";
    }
}




