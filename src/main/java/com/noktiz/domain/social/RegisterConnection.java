package com.noktiz.domain.social;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.*;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.social.facebook.RegisterFacebookConnection;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.google.OAuth2GoogleCredentialBuilder;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestParameters;

import java.util.Date;

/**
 * Created by hasan on 8/7/14.
 */
@Deprecated
public class RegisterConnection extends WebPage {
    
    public RegisterConnection() {
        IRequestParameters requestParameters = getRequest().getRequestParameters();
        String service = requestParameters.getParameterValue("service").toOptionalString();
        boolean requestRegister = requestParameters.getParameterValue("service").toBoolean(false);
        if(service.equals("facebook")){
            if(requestRegister){
                User user = new User();
                user.setEmail(requestParameters.getParameterValue("email").toString());
                String name = requestParameters.getParameterValue("name").toString();
                String[] namepart = name.split(" ", 1);
                user.setFirstName(namepart[0]);
                user.setLastName(namepart[1]);
                PersonalInfo personalInfo = new PersonalInfo(user);
                user.setPersonalInfo(personalInfo);
                personalInfo.setJoinDate(new Date());
                user.setGender(User.Gender.male);
                Result addUser = UserFactory.addUser(user,true, true,false);
                if(addUser.result==false)
                    return;
                //@todo check whether we need to change loadUserWithEmail to loadUserWithEmailId? or not
                User.loadUserWithEmailId(user.getEmail(),true);
                //@todo check whether we need to change loadUserWithEmail to loadUserWithEmailId? or not
                ((UserSession) getSession()).setUser(new UserFacade(User.loadUserWithEmailId(user.getEmail(),true)));
            }
            String access_token = requestParameters.getParameterValue("access_token").toOptionalString();
            String facebook_id = requestParameters.getParameterValue("facebook_id").toOptionalString();
            UserSession session = (UserSession) getSession();
            try {
                new RegisterFacebookConnection().register(session.getUser(), facebook_id, access_token);
            } catch (ResultException e) {
                Logger.getLogger(this.getClass()).error(e);
            }
        }
        
        if(service.equals("google")){
            String ClientCode = requestParameters.getParameterValue("client_code").toOptionalString();
            Result r = new Result(OAuth2GoogleCredentialBuilder.Build(ClientCode, ((UserSession)getSession()).getUser()));
            //TODO : Sina, Age nashod...!!!!
        }
    }
    
}
