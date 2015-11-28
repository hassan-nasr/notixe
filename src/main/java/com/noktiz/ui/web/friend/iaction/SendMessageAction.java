/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend.iaction;

import com.noktiz.domain.entity.User;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.thread.ThreadPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public class SendMessageAction extends  PersonListProvider.IPerson.IAction {

    User user;

    public SendMessageAction(User user) {
        this.user = user;
    }

    @Override
    public IModel<String> getActionTitle() {
        return new StringResourceModel("SendMessage", null);
    }

    @Override
    public boolean onAction(AjaxRequestTarget art, Component caller) {
        PageParameters p = new PageParameters();
        p.add("to", user.getEmail());
        caller.setResponsePage(ThreadPage.class, p);
        return false;
    }

    @Override
    public IModel<String> getButtonClass() {
        return Model.of(" ");
    }
}