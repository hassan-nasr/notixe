/*
 * FooterPanel.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
 
package com.noktiz.ui.web.header;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.auth.SignInPage;
import com.noktiz.ui.web.auth.UserSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/** 
 *
 * @author hossein
 * @version 
 */

public final class GuestHeaderPanel extends BasePanel {


    TextField LIemail;
    PasswordTextField LIpassword;
    HiddenField redirect;


    public GuestHeaderPanel(String id, String selectedPage) {
        super(id);
        WebMarkupContainer header = new TransparentWebMarkupContainer("header");
        add(header);

        header.add(new AttributeAppender("class", " " + (isMobile ? "container-fluid" : "container")));
        Image image= new Image("logo",new PackageResourceReference(Application.class,"Notixe.png"));
        add(image);
        createLoginForm();
    }


    private void createLoginForm() {
        Form loginForm = new Form("loginForm") {
            @Override
            protected void onSubmit() {
                UserSession session = (UserSession) getSession();
                boolean r = session.signIn(LIemail.getDefaultModelObjectAsString(), LIpassword.getDefaultModelObjectAsString(),false,getWebResponse());
                if (r) {
                    throw new RedirectToUrlException(redirect.getDefaultModelObjectAsString());
//                    setResponsePage(getApplication().getHomePage());
                } else {
                    throw new RedirectToUrlException(urlFor(SignInPage.class, new PageParameters().add("error","true")).toString());
//                    String errmsg = getString("loginError", null, "Username and Password aren't match please try again or click on 'forget password' to reset your password");
//                    error(errmsg);
                }
            }
        };
        add(loginForm);
        BookmarkablePageLink register= new BookmarkablePageLink("register",SignInPage.class,new PageParameters().add("register",""));
        loginForm.add(register);
//        FeedbackPanel feedback = new FeedbackPanel("feedback");
//        loginForm.add(feedback);
        LIemail = new TextField("LIemail", new Model());
        loginForm.add(LIemail);
        LIemail.setRequired(true);

        LIpassword = new PasswordTextField("LIpassword", new Model());
        loginForm.add(LIpassword);
        LIpassword.setRequired(true);
        redirect = new HiddenField("redirect",Model.of()){
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                response.render(new OnDomReadyHeaderItem("$('#"+getMarkupId()+"').val(window.location.toString())"));
            }
        };
        loginForm.add(redirect);
        redirect.setOutputMarkupId(true);
    }

}
