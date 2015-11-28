/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.settings;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.i18n.Local;
import com.noktiz.domain.i18n.LocalManager;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;

/**
 *
 * @author Hossein
 */
public abstract class PersonalInfoSettings extends Panel {

    public PersonalInfoSettings(String id, final UserFacade user) {
        super(id);
        Form form = new Form("form");
        add(form);
        TextField firstName = new TextField("firstName", new IModel<String>() {
            @Override
            public String getObject() {
                return user.getFirstName();
            }

            @Override
            public void setObject(String object) {
                user.setFirstName(object);
            }

            @Override
            public void detach() {
            }
        });
        form.add(firstName);
        firstName.setRequired(true);
        TextField lastName = new TextField("lastName", new IModel<String>() {
            @Override
            public String getObject() {
                return user.getLastName();
            }

            @Override
            public void setObject(String object) {
                user.setLastName(object);
            }

            @Override
            public void detach() {
            }
        });
        form.add(lastName);
        lastName.setRequired(true);
        DropDownChoice gender = new DropDownChoice("gender", new IModel<User.Gender>() {
            @Override
            public User.Gender getObject() {
                return user.getGender();
            }

            @Override
            public void setObject(User.Gender object) {
                user.setGender(object);
            }

            @Override
            public void detach() {
            }
        }, Arrays.asList(User.Gender.values()), new IChoiceRenderer<User.Gender>() {

            @Override
            public Object getDisplayValue(User.Gender gender) {
                return "Gender."+gender.toString();
            }

            @Override
            public String getIdValue(User.Gender gender, int i) {
                return String.valueOf(i);
            }
        }){
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        form.add(gender);
        gender.setRequired(true);
        DateTextField birthDay = new DateTextField("birthDay", new IModel<Date>() {
            @Override
            public Date getObject() {
                return user.getBirthdate();
            }

            @Override
            public void setObject(Date object) {
                user.setBirthdate(object);
            }

            @Override
            public void detach() {
            }
        });
        form.add(birthDay);
        ArrayList<String> availableIDs = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
        availableIDs.add(PersonalInfo.automaticTimezone);
        DropDownChoice timezone = new DropDownChoice("timezone", new IModel<String>() {
            @Override
            public String getObject() {
                return user.getPersonalInfo().getTimezone();
            }

            @Override
            public void setObject(String object) {
                user.getPersonalInfo().setTimezone(object);
            }

            @Override
            public void detach() {
            }
        }, availableIDs);
        form.add(timezone);
        LocalManager localManager= new LocalManager();
        List<Local> locals = localManager.loadActiveLocals();
        DropDownChoice localChoice = new DropDownChoice("local", new IModel<Local>() {
            @Override
            public void setObject(Local local) {
                user.getPersonalInfo().setLocal(local);
            }

            @Override
            public Local getObject() {
                Local local = user.getPersonalInfo().getLocal();
                if (local == null)
                    return locals.get(0);
                return local;
            }

            @Override
            public void detach() {
            }
        }, locals, new IChoiceRenderer<Local>() {
            @Override
            public Object getDisplayValue(Local local) {
                return  "Language."+local.getLanguage()+"."+local.getCountry();
            }

            @Override
            public String getIdValue(Local local, int i) {
                return local.getId().toString();
            }
        }){
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        form.add(localChoice);
        AjaxSubmitLink submit = new IndicatingAjaxSubmitLink2("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Result save = user.save();
                ((UserSession) getSession()).setLocal();
                if (save.result) {
                    success(getString("personalInfoChanged"));
                    onAjax(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                onAjax(target);
            }
            
        };
        form.add(submit);
        LoadInfoFromFacebook getFromFacebook = new LoadInfoFromFacebook("getFromFacebook", new AbstractReadOnlyModel<MarkupContainer>() {
            @Override
            public MarkupContainer getObject() {
                return PersonalInfoSettings.this.getParent();
            }
        });
        form.add(getFromFacebook);
    }

    public abstract void onAjax(AjaxRequestTarget target);
}
