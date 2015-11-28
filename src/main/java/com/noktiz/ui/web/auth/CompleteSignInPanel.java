package com.noktiz.ui.web.auth;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.CaptchaForm;
import com.noktiz.ui.web.component.CaptchaPanel;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.friend.fb.RegisterWithFacebookPanel;
import com.noktiz.ui.web.google.GoogleLogin;
import com.noktiz.ui.web.staticp.legal.PrivacyPolicy;
import com.noktiz.ui.web.staticp.legal.TermsOfUse;
import com.noktiz.ui.web.utils.AjaxTimeZoneSubmit;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Date;

/**
 * Created by hasan on 9/12/14.
 */
public class CompleteSignInPanel extends BasePanel {

    PasswordTextField RRpassword;
    TextField Femail;
    TextField LIemail;
    PasswordTextField LIpassword;
    CheckBox rememberMe;

    TextField Remail;
    TextField<Date> birthday;
    TextField RfirstName;
    TextField RlastName;
    PasswordTextField Rpassword;
    CheckBox acceptTerm;
    NotificationFeedbackPanel feedback;
    private String explicitNextUrl=null;

    @Override
    public void renderHead(IHeaderResponse response) {
//        getSession().setLocale(new Locale("en","US"));
//        getSession().setLocale(new Locale("fa","IR"));
        addResourceOld(response);
        super.renderHead(response);
        if(getRequest().getRequestParameters().getParameterNames().contains("register")){
            response.render(new OnDomReadyHeaderItem("jQuery('.login-form').hide();\n" +
                    "\t            jQuery('.register-form').show();"));
        }
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CompleteSignInPanel.class,"CompleteSignInPanel.js")));
//        response.render(OnDomReadyHeaderItem.forScript("readyCaptcha()"));
    }
    private void addResourceOld(IHeaderResponse response){
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/scripts/login.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/scripts/app.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/jquery-validation/dist/jquery.validate.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/select2/select2.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/pages/login-soft.css")));

        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/metro.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/font-awesome/css/font-awesome.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style_responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style_default.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/uniform/css/uniform.default.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/noktiz/light-theme.css")));

    }
    private void addResource(IHeaderResponse response){

        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/js/jquery-1.8.3.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/bootstrap/js/bootstrap.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/uniform/jquery.uniform.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/js/jquery.blockui.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/js/app.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/jquery-validation/dist/jquery.validate.min.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap.min.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/metro.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/font-awesome/css/font-awesome.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style_responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style_default.css")));

//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/uniform/css/uniform.default.css")));*/
//		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/scripts/Login.js")));

    }


    CaptchaPanel captchaPanel;
    public CompleteSignInPanel(String id, String aContinue) {
        super(id);
        explicitNextUrl = aContinue;
//        Logger.getRootLogger().error("constructing CSIPanel ");
        feedback = new NotificationFeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
        createLoginForm();
//        WebMarkupContainer forgetForm = createForgetForm();
        createRegForm();
        add(new AjaxTimeZoneSubmit("timeZone"));
        add(new RegisterWithFacebookPanel("facebookConnect"));
        add(new GoogleLogin("googleConnect"));

    }

    private void createLoginForm() {
        Form loginForm = new Form("loginForm") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                UserSession session = (UserSession) getSession();

                session.signIn(LIemail.getDefaultModelObjectAsString(), LIpassword.getDefaultModelObjectAsString(),(Boolean) rememberMe.getDefaultModelObject(), (WebResponse) getResponse());
                Result r =session.popLastResult();
                if (r.result) {
//                    IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
//                            .getAuthenticationStrategy();
//
//                    if ((Boolean) rememberMe.getDefaultModelObject())
//                    {
//                        strategy.save(LIemail.getDefaultModelObjectAsString(), LIpassword.getDefaultModelObjectAsString());
//                    }
//                    else
//                    {
//                        strategy.remove();
//                    }
                    if(explicitNextUrl!=null)
                        throw new RedirectToUrlException(explicitNextUrl);
                    setResponsePage(getApplication().getHomePage());
                } else {
                    r.displayInWicket(this);
                }
            }

        };
        add(loginForm);
//        FeedbackPanel feedback = new FeedbackPanel("feedback");
//        loginForm.add(feedback);
        LIemail = new EmailTextField("LIemail", new Model());
        loginForm.add(LIemail);
        LIemail.setRequired(true);
        rememberMe = new CheckBox("LIrememberMe",Model.of(true));
        loginForm.add(rememberMe);

        LIpassword = new PasswordTextField("LIpassword", new Model());
        loginForm.add(LIpassword);
        LIpassword.setRequired(true);
        BookmarkablePageLink forgetPassword= new BookmarkablePageLink
                ("forgetPassword",ForgetPasswordPage.class);
        loginForm.add(forgetPassword);
    }

    private WebMarkupContainer createForgetForm() {
        if(true){
            return null;
        }
        Form resetForm = new Form("forgetForm"){
            @Override
            public boolean wantSubmitOnNestedFormSubmit() {
                return true;
            }
        };
        AjaxSubmitLink forgetFormSubmit = new IndicatingAjaxSubmitLink2("forgetFormSubmit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    CaptchaForm cform = (CaptchaForm) form;
                    if (!cform.getValidateCaptcha()) {
                        error(getString("captcha.valid.fail"));
                        return;
                    }
                    // tod o check whether we need to change loadUserWithEmail to loadUserWithEmailId? or not
                    // not needed because we don't use this part
                    ResultWithObject<UserFacade> requestedUserResult = UserFactory.loadUserWithEmail(Femail.getDefaultModelObjectAsString(), false);
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
                        String errmsg = getString("resetError", null, "Could not find any user with this Email Address");
                        error(errmsg);
                    }
                }
                finally {
                    target.add(feedback);
                    target.add(captchaPanel);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedback);
                target.add(captchaPanel);
            }
        };
        resetForm.add(forgetFormSubmit);
//        FeedbackPanel feedback = new FeedbackPanel("feedback");
//        loginForm.add(feedback);
        Femail = new EmailTextField("Femail", new Model());
        resetForm.add(Femail);
        Femail.setRequired(true);

        return resetForm;
    }

    private WebMarkupContainer createRegForm() {
        Form registerForm= new CaptchaForm("registerForm"){
            @Override
            public boolean wantSubmitOnNestedFormSubmit() {
                return true;
            }
        };
        add(registerForm);
        captchaPanel = new CaptchaPanel("captcha", false);
        registerForm.add(captchaPanel);
        registerForm.setOutputMarkupId(true);
        Remail = new EmailTextField("Remail", new Model());
        registerForm.add(Remail);
//        Remail.setRequired(true);
        RfirstName = new TextField("RfirstName", new Model());
        registerForm.add(RfirstName);
//        RfirstName.setRequired(true);
        RlastName = new TextField("RlastName", new Model());
        registerForm.add(RlastName);
//        RlastName.setRequired(true);
        Rpassword = new PasswordTextField("Rpassword", new Model());
        registerForm.add(Rpassword);
//        Rpassword.setRequired(true);

        RRpassword = new PasswordTextField("RRpassword", new Model());
        registerForm.add(RRpassword);
//        RRpassword.setRequired(true);

        acceptTerm = new CheckBox("acceptTerm",new Model    ());
        registerForm.add(acceptTerm);
//        acceptTerm.setRequired(true);
        registerForm.add(new IndicatingAjaxSubmitLink2("ajaxRegister") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                {
                    try {
                        CaptchaForm cform = (CaptchaForm) form;
                        if (!cform.getValidateCaptcha()) {
                            error("Please validate that you are not a robot");
                            return;
                        }
                        if (!RRpassword.getDefaultModelObjectAsString().equals(Rpassword.getDefaultModelObjectAsString())) {
                            new Result(new Result.Message("message.password_not_match", Result.Message.Level.error), false).displayInWicket(this);
                        } else if (!(boolean) acceptTerm.getDefaultModelObject()) {
                            new Result(new Result.Message("message.You_should_accept_the_terms_of_use", Result.Message.Level.error), false).displayInWicket(this);
                        } else {
                            User user = new User();
                            user.setEmail(Remail.getDefaultModelObjectAsString());
                            user.setFirstName(RfirstName.getDefaultModelObjectAsString());
                            user.setLastName(RlastName.getDefaultModelObjectAsString());

                            //                user.setBirthdate((Date) birthday.getDefaultModelObject());
                            PersonalInfo personalInfo = new PersonalInfo(user);
                            user.setPersonalInfo(personalInfo);
                            personalInfo.setJoinDate(new Date());
                            personalInfo.setTimezone(PersonalInfo.automaticTimezone);
                            personalInfo.setPassword(Rpassword.getDefaultModelObjectAsString());
                            user.setGender(User.Gender.Unknown);
                            Result addUser = UserFactory.addUser(user, false, true, false);
                            addUser.displayInWicket(this);
                        }
                    }finally {
                        target.add(captchaPanel);
                        target.add(feedback);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(captchaPanel,feedback);
                super.onError(target, form);
            }
        });

        registerForm.add(new BookmarkablePageLink<>("privacyPolicy", PrivacyPolicy.class));
        registerForm.add(new BookmarkablePageLink<>("termsOfUse", TermsOfUse.class));
        add(new BookmarkablePageLink<>("privacyPolicy2", PrivacyPolicy.class));
        add(new BookmarkablePageLink<>("termsOfUse2", TermsOfUse.class));
        return registerForm;
    }

}
