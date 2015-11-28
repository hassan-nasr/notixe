package com.noktiz.ui.web.start;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.auth.SignInPage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.CaptchaForm;
import com.noktiz.ui.web.component.CaptchaPanel;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.friend.fb.RegisterWithFacebookPanel;
import com.noktiz.ui.web.google.GoogleLogin;
import com.noktiz.ui.web.home.HomePage;
import com.noktiz.ui.web.staticp.legal.PrivacyPolicy;
import com.noktiz.ui.web.staticp.legal.TermsOfUse;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Date;
import java.util.Enumeration;

/**
 * Created by hasan on 2014-12-14.
 */
public class Welcome2 extends WebPage {

//    HiddenField redirect;
    static int counter=0;
    private EmailTextField Remail;
    private TextField RfirstName;
    private TextField RlastName;
    private PasswordTextField Rpassword;
    private PasswordTextField RRpassword;
    private CaptchaPanel captchaPanel;
    private NotificationFeedbackPanel feedback;
    private Label feedbackHeader;
    private Label feedbackBody;
//    private Label feedbackShowLink;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/css/ie/html5shiv.js" ),null, null, true, null, "lte IE 8"));
////        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.min.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.dropotron.min.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.scrolly.min.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.onvisible.min.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/skel.min.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/skel-layers.min.js")));
//        if(true){
//            return;
//        }
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/init.js")));
//        if(true){
//            return;
//        }
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/skel.css" )));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/style.css" )));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/style-desktop.css" )));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/style-noscript.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/ie/v8.css" ),null, null,"lte IE 8"));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Welcome2.class,"bootstrap-3.3.5/css/bootstrap.min.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Welcome2.class,"form.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Welcome2.class,"style.css")));


        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Welcome2.class, "jquery.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Welcome2.class, "bootstrap-3.3.5/js/bootstrap.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Welcome2.class, "app.js")));
    }

    public Welcome2() {
        super();
        if(((UserSession)getSession()).getUser()!=null){
            setResponsePage(HomePage.class);
        }
        if(((UserSession) getSession()).signInWithStaySignedInCookie(((WebRequest) getRequest()), (WebResponse) getResponse())) {
            setResponsePage(HomePage.class, null);
        }
//        if(true){
//            return;
//        }
        createLoginForm();
        createRegForm();
//        add(new WebMarkupContainer("image1"));
//        add(new WebMarkupContainer("image2"));
//        add(new WebMarkupContainer("image3"));

        add( new Image("logo",new PackageResourceReference(Welcome2.class,"images/notixe.gif")));

//        add (new CompleteSignInPanel("login"));
//        createLoginForm();
//        add(new SmartHeaderPanel("header","Welcome","none"));
        feedbackBody = new Label("feedbackBody");
        add(feedbackBody);
        feedbackBody.setOutputMarkupId(true);
        feedbackHeader = new Label("feedbackHeader");
        add(feedbackHeader);
        feedbackHeader.setOutputMarkupId(true);
//        feedbackShowLink = new Label("feedbackShowLink");
//        add(feedbackShowLink);
//        feedbackShowLink.setOutputMarkupId(true);
    }


    private void createLoginForm() {
        Form loginForm = new LoginForm("loginForm", "LIemail", "LIpassword");
        Form loginForm2 = new LoginForm("loginForm2", "LIemail", "LIpassword");

        Enumeration allAppenders = Logger.getRootLogger().getAllAppenders();
        add(loginForm);
        add(loginForm2);
//        FeedbackPanel feedback = new FeedbackPanel("feedback");
//        loginForm.add(feedback);
//        loginForm.add( new BookmarkablePageLink<>("registerLink", SignInPage.class));
//        redirect = new HiddenField("redirect",Model.of(RequestCycle.get().getUrlRenderer().renderFullUrl(getRequest().getClientUrl())));
//        loginForm.add(redirect);
    }

    private WebMarkupContainer createRegForm() {
        feedback = new NotificationFeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
        Form registerForm= new CaptchaForm("registerForm"){
            @Override
            public boolean wantSubmitOnNestedFormSubmit() {
                return true;
            }
        };


        registerForm.add(new RegisterWithFacebookPanel("facebookConnect"));
        registerForm.add(new GoogleLogin("googleConnect"));

        add(registerForm);
        captchaPanel = new CaptchaPanel("captcha", true);
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

//        acceptTerm = new CheckBox("acceptTerm",new Model    ());
//        registerForm.add(acceptTerm);
//        acceptTerm.setRequired(true);
        registerForm.add(new IndicatingAjaxSubmitLink2("ajaxRegister") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                {
                    try {
                        CaptchaForm cform = (CaptchaForm) form;
                        if (!cform.getValidateCaptcha()) {
//                            error("Please validate that you are not a robot");
                            feedbackBody.setDefaultModel(new StringResourceModel("validateNoRobot",null,new Object[0]));
                            feedbackHeader.setDefaultModel(Model.of("Sign Up"));
                            return;
                        }
                        if (!RRpassword.getDefaultModelObjectAsString().equals(Rpassword.getDefaultModelObjectAsString())) {
//                            new Result(new Result.Message("Passwords aren't match", Result.Message.Level.error), false).displayInWicket(this);
                            feedbackBody.setDefaultModel(new StringResourceModel("passwordNotMatch",null,new Object[0]));
                            feedbackHeader.setDefaultModel(Model.of("Sign Up"));
//                        } else if (!(boolean) acceptTerm.getDefaultModelObject()) {
//                            new Result(new Result.Message("You should accept the terms of use", Result.Message.Level.error), false).displayInWicket(this);
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
//                            addUser.displayInWicket(this);
                            feedbackBody.setDefaultModel(new StringResourceModel(addUser.messages.get(0).getText(),null,new String[]{user.getEmail()}));
                            feedbackHeader.setDefaultModel(Model.of("Sign Up"));
                        }
                    }finally {
                        target.appendJavaScript("$('#regModel').modal('show')");
                        target.add(feedbackBody);
                        target.add(feedbackHeader);
                        target.add(captchaPanel);
//                        target.add(feedback);
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
        return registerForm;
    }


    private class LoginForm extends Form {

        TextField LIemail;
        PasswordTextField LIpassword;

        public LoginForm(String name, String emailId, String passwordId) {
            super(name);
            LIemail = new EmailTextField(emailId, new Model());
            this.add(LIemail);
            LIemail.setRequired(true);

            LIpassword = new PasswordTextField(passwordId, new Model());
            this.add(LIpassword);
            LIpassword.setRequired(true);

        }

        @Override
        protected void onSubmit() {
            Logger.getLogger(Welcome2.class).debug("test user for login with mail ="+LIemail.getDefaultModelObject()+". redirecting to home");
            UserSession session = (UserSession) getSession();
            boolean r = session.signIn(LIemail.getDefaultModelObjectAsString(), LIpassword.getDefaultModelObjectAsString());
            if (r) {
                Logger.getLogger(Welcome2.class).debug("Login user with mail ="+LIemail.getDefaultModelObject()+". redirecting to home");
                throw new RedirectToUrlException(urlFor(HomePage.class,null).toString());
//                    setResponsePage(getApplication().getHomePage());
            } else {
                Logger.getLogger(Welcome2.class).debug("fail to Login user with mail ="+LIemail.getDefaultModelObject()+". redirecting to loginPage");
                throw new RedirectToUrlException(urlFor(SignInPage.class, new PageParameters().add("error","true")).toString());
//                    String errmsg = getString("loginError", null, "Username and Password aren't match please try again or click on 'forget password' to reset your password");
//                    error(errmsg);
            }
        }
    }
}
