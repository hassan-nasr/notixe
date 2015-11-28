/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.behavior.OverrideFormSubmitOnEnter;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public final class FriendList extends Panel {

    TextField email;
    PersonList friendList;
    final FriendListProvider friendListProvider;
    NotificationFeedbackPanel feedback;

    public FriendList(String id, final UserFacade user) {
        super(id);
        user.refresh();
        final UserFacade me = user;
        Form form;
        form = new Form("form");
        add(form);
        IndicatingAjaxSubmitLink2 submit = new IndicatingAjaxSubmitLink2("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                try {
                    String mail = EmailAddressUtils.normalizeEmail(email.getDefaultModelObjectAsString());
//                    ResultWithObject<UserFacade> load = UserFactory.loadUserWithEmailId(mail, true);
//                    if (load.result == false) {
//                        Result invite = user.inviteWithEmail(mail);
//
//                        invite.displayInWicket(this);
//                        return;
//                    }
                    ResultWithObject<Friendship> r = me.addFriendByEmail(mail);
                    if (r.result) {
                        friendList.refresh(target,false);
                    }
                    r.displayInWicket(this);
                } finally {
                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                target.add(form);
            }
        };
        form.add(submit);
        form.setDefaultButton(submit);
        email = new EmailTextField("email", new Model());
        form.add(email);
        email.setRequired(true);
        feedback = new NotificationFeedbackPanel("feedback");
        form.add(feedback);
        feedback.setOutputMarkupId(true);
        friendListProvider = new FriendListProvider(me);
        friendList = new PersonList("friendList", Model.of(friendListProvider), null, 30, null, true);
        add(friendList);


//        add(new FBPersonList("fbfriends"));

    }
}
