package com.noktiz.ui.web.start;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePage;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.auth.CompleteSignInPanel;
import com.noktiz.ui.web.auth.SignInPage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.header.SmartHeaderPanel;
import com.noktiz.ui.web.home.HomePage;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

import java.util.Enumeration;

/**
 * Created by hasan on 2014-12-14.
 */
public class Welcome extends WebPage {


    TextField LIemail;
    PasswordTextField LIpassword;
//    HiddenField redirect;
    static int counter=0;
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/css/ie/html5shiv.js" ),null, null, true, null, "lte IE 8"));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(JQueryResourceReference.class, "jquery/jquery-1.11.1.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.dropotron.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.scrolly.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/jquery.onvisible.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/skel.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/skel-layers.min.js")));
//        if(true){
//            return;
//        }
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/helios/js/init.js")));
//        if(true){
//            return;
//        }
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/skel.css" )));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/style.css" )));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/style-desktop.css" )));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/style-noscript.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/helios/css/ie/v8.css" ),null, null,"lte IE 8"));
    }

    public Welcome() {
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

//        add(new WebMarkupContainer("image1"));
//        add(new WebMarkupContainer("image2"));
//        add(new WebMarkupContainer("image3"));

        add( new Image("logo",new PackageResourceReference(Application.class,"Notixe-glow.png")));

//        add (new CompleteSignInPanel("login"));
//        createLoginForm();
//        add(new SmartHeaderPanel("header","Welcome","none"));
    }


    private void createLoginForm() {
        Form loginForm = new Form("loginForm") {
            @Override
            protected void onSubmit() {
                Logger.getLogger(Welcome.class).debug("test user for login with mail ="+LIemail.getDefaultModelObject()+". redirecting to home");
                UserSession session = (UserSession) getSession();
                boolean r = session.signIn(LIemail.getDefaultModelObjectAsString(), LIpassword.getDefaultModelObjectAsString(), false, (WebResponse) getResponse());
                if (r) {
                    Logger.getLogger(Welcome.class).debug("Login user with mail ="+LIemail.getDefaultModelObject()+". redirecting to home");
                    throw new RedirectToUrlException(urlFor(HomePage.class,null).toString());
//                    setResponsePage(getApplication().getHomePage());
                } else {
                    Logger.getLogger(Welcome.class).debug("fail to Login user with mail ="+LIemail.getDefaultModelObject()+". redirecting to loginPage");
                    throw new RedirectToUrlException(urlFor(SignInPage.class, new PageParameters().add("error","true")).toString());
//                    String errmsg = getString("loginError", null, "Username and Password aren't match please try again or click on 'forget password' to reset your password");
//                    error(errmsg);
                }
            }
        };

        Enumeration allAppenders = Logger.getRootLogger().getAllAppenders();
        add(loginForm);
//        FeedbackPanel feedback = new FeedbackPanel("feedback");
//        loginForm.add(feedback);
        LIemail = new EmailTextField("LIemail", new Model());
        loginForm.add(LIemail);
        LIemail.setRequired(true);

        LIpassword = new PasswordTextField("LIpassword", new Model());
        loginForm.add(LIpassword);
        LIpassword.setRequired(true);

        loginForm.add( new BookmarkablePageLink<>("registerLink", SignInPage.class));
//        redirect = new HiddenField("redirect",Model.of(RequestCycle.get().getUrlRenderer().renderFullUrl(getRequest().getClientUrl())));
//        loginForm.add(redirect);
    }
}
