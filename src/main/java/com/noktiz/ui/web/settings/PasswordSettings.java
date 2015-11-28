/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.settings;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Hossein
 */
public abstract class PasswordSettings extends Panel {

    public PasswordSettings(String id, final UserFacade user,boolean checkOld) {
        super(id);
        if(!user.isSetPassword()) {
            checkOld = false;
        }
        final boolean finalCheckOld = checkOld;
        Form form = new Form("form");
        add(form);
        WebMarkupContainer oldPasswordCont = new WebMarkupContainer("oldPasswordCont");
        form.add(oldPasswordCont);
        final PasswordTextField oldPass = new PasswordTextField("oldPass", Model.of(""));
        oldPasswordCont.add(oldPass);
        if(!checkOld) {
            oldPasswordCont.setVisible(false);
        }
        final PasswordTextField newPass = new PasswordTextField("newPass", Model.of(""));
        form.add(newPass);
        final PasswordTextField rePass = new PasswordTextField("rePass", Model.of(""));
        form.add(rePass);
        AjaxSubmitLink submit = new IndicatingAjaxSubmitLink2("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    user.refresh();
                    UserFacade user2= new UserFacade((User)HSF.get().getCurrentSession().get(User.class,user.getUser().getId()));
                    String oldPassStr = oldPass.getDefaultModelObjectAsString();
                    String newPassStr = newPass.getDefaultModelObjectAsString();
                    String rePassStr = rePass.getDefaultModelObjectAsString();
                    if (!rePassStr.equals(newPassStr)) {
                        error(getString("passwordsNotMatch"));
                        return;
                    }
                    boolean doesPasswordMatch;
                    doesPasswordMatch = !finalCheckOld || user2.doesPasswordMatch(oldPassStr);

                    if (!doesPasswordMatch) {
                        error(getString("wrongOldPassword"));
                    } else {
                        Result changePassword = user2.changePassword(newPassStr);
                        if (changePassword.result) {
                            success(getString("passwordChangeSuccess"));
                        } else {
                            changePassword.displayInWicket(form);
                        }
                    }
                } finally {
                    onAjax(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                onAjax(target);
            }
            
        };
        form.add(submit);
    }

    public abstract void onAjax(AjaxRequestTarget target);
}
