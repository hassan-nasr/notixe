package com.noktiz.ui.web.auth;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.component.CaptchaForm;
import com.noktiz.ui.web.component.CaptchaPanel;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.home.HomePage;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Created by Hossein on 1/3/2015.
 */
public class ForgetPasswordPage extends BaseUserPage {
    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(SignInPage.class, "SignInPage.css")));
        addResourceOld(response);
    }
    public ForgetPasswordPage(){
        super("none");
        if(isUserLoggedIn()){
            setResponsePage(HomePage.class);
        }
        add( new Image("logo",new PackageResourceReference(Application.class,"Notixe.png")));
        CaptchaForm form = new CaptchaForm("form");
        add(form);
        CaptchaPanel captcha= new CaptchaPanel("captcha", false);
        form.add(captcha);
        NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        form.add(feedback);
        final EmailTextField email = new EmailTextField("email", Model.of(""));
        form.add(email);
        email.setRequired(true);
        SubmitLink submit = new SubmitLink("submit"){
            @Override
            public void onSubmit() {
                Boolean validateCaptcha = form.getValidateCaptcha();
                if(validateCaptcha==false){
                    error("please validate that you are not robot");
                    return;
                }
                ResultWithObject<UserFacade> requestedUserResult = UserFactory.loadUserWithEmailId(EmailAddressUtils.normalizeEmail(email.getDefaultModelObjectAsString()), false);
                if (requestedUserResult.result) {
                    UserFacade requestedUserFacade = requestedUserResult.object;
                    Result r;
                    if (requestedUserFacade.getUser().getActive()) {
                        r = requestedUserFacade.sendForgetPasswordEmail();
                    } else {
                        r = requestedUserFacade.sendActivationEmail();
                    }
                    r.displayInWicket(this);
                } else {
                    String errMsg = getString("resetError", null, "Could not find any user with this Email Address");
                    error(errMsg);
                }
            }
        };
        form.add(submit);
    }
    private void addResourceOld(IHeaderResponse response){

    }
}
