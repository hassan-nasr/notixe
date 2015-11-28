/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.user;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.noktiz.ui.web.friend.fb.RegisterWithFacebookPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public final class RegisterPanel extends Panel {

    TextField email;
    TextField<Date> birthday;
    TextField firstName;
    TextField lastName;
    PasswordTextField password;
    DropDownChoice timezone;
    public RegisterPanel(String id) {
        super(id);
        Form form;
        form = new Form("form") {
            @Override
            protected void onSubmit() {
                User user = new User();
                user.setEmail(email.getDefaultModelObjectAsString());
                user.setFirstName(firstName.getDefaultModelObjectAsString());
                user.setLastName(lastName.getDefaultModelObjectAsString());

                //                user.setBirthdate((Date) birthday.getDefaultModelObject());
                PersonalInfo personalInfo = new PersonalInfo(user);
                user.setPersonalInfo(personalInfo);
                personalInfo.setJoinDate(new Date());
                personalInfo.setPassword(password.getDefaultModelObjectAsString());
                personalInfo.setTimezone(timezone.getDefaultModelObjectAsString());
                user.setGender(User.Gender.male);
                Result addUser = UserFactory.addUser(user, false, true,false);
                addUser.displayInWicket(this);
            }
        };
        add(form);
        NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        add(feedback);
        email = new TextField("email", new Model());
        form.add(email);
        email.setRequired(true);
        firstName = new TextField("firstName", new Model());
        form.add(firstName);
        firstName.setRequired(true);
        lastName = new TextField("lastName", new Model());
        form.add(lastName);
        lastName.setRequired(true);
        List<String> availableIDs = Arrays.asList(TimeZone.getAvailableIDs());

//        birthday = new TextField("birthday", new Model<String>());
//        form.add(birthday);
        timezone = new DropDownChoice("timezone", Model.of("UTC"), availableIDs);
        form.add(timezone);

//        birthday.setRequired(true);
        password = new PasswordTextField("password", new Model());
        form.add(password);
        password.setRequired(true);

        add(new RegisterWithFacebookPanel("registerWithFacebook"));
    }
}
