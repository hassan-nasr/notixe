///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
package com.noktiz.ui.web.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.google.GoogleUtils;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.behavior.IndicatingBehavior;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;

import java.io.IOException;
import java.util.List;

import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

///**
// *
// * @author Sina
// */
public abstract class GetGooglePermission extends Panel {
    private static final JavaScriptResourceReference MYPAGE_JS =
            new JavaScriptResourceReference(GetGooglePermission.class, "GetGooglePermission.js");

    protected static final String CLIENT_ID = SystemConfigManager.getCurrentConfig().getProperty("GoogleClientID");
    protected static final String CLIENT_SECRET = SystemConfigManager.getCurrentConfig().getProperty("GoogleClientSecret");
    protected static String CLIENT_CODE;
    protected static final HttpTransport TRANSPORT = new NetHttpTransport();
    protected static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    protected static final Gson GSON = new Gson();
    protected GoogleCredential credential;
    protected NotificationFeedbackPanel feedbackPanel;


    public static String permissionsToString(List<Scope> permissions){
        String str = "";
        for(Scope s:permissions){
            if(s.equals(Scope.basicProfile)){
                str+="profile ";
            }
            else if(s.equals(Scope.friendList)){
                str+="https://www.googleapis.com/auth/plus.login ";
            }
            else if(s.equals(Scope.email)){
                str+="https://www.googleapis.com/auth/plus.profile.emails.read ";
            }
            else if(s.equals(Scope.contacts)){
                str+="https://www.google.com/m8/feeds ";
            }
        }
        return str;
    }

    @Deprecated
    public GetGooglePermission(String id, final List<Scope> permissions, final UserFacade user){
        this(id,permissions,user,true,"Connect with Google");
    }

    public GetGooglePermission(String id, final List<Scope> permissions, final UserFacade user, boolean autoLogin, String buttonText) {
        super(id);
        feedbackPanel = new NotificationFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        setOutputMarkupId(true);

        final WebMarkupContainer scopesList= new WebMarkupContainer("scopesList");
        scopesList.setOutputMarkupId(true);
        scopesList.add(new AttributeModifier("content", Model.of(permissionsToString(permissions))));
        add(scopesList);

        final WebMarkupContainer clientid= new WebMarkupContainer("clientid");
        clientid.setOutputMarkupId(true);
        clientid.add(new AttributeModifier("content", Model.of(CLIENT_ID)));
        add(clientid);

        Form bridgeForm = new Form("bridgeForm");
        final TextField bridgeText0 = new TextField("bridgeText0",Model.of(""));
        bridgeForm.add(bridgeText0);

        if(autoLogin == true) {
            if(new GoogleUtils().refreshToken(user,permissions)){
                this.setVisible(false);
                onAccessGranted(null);
            }
        }

        AjaxSubmitLink bridgeSubmit= new IndicatingAjaxSubmitLink2("bridgeSubmit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                CLIENT_CODE = bridgeText0.getDefaultModelObjectAsString();
//                String storedState = ((UserSession)getSession()).getState();
                try {

                    GoogleTokenResponse tokenResponse =
                            new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY,
                                    CLIENT_ID, CLIENT_SECRET, CLIENT_CODE, "postmessage").execute();
                    GoogleCredential credential;

                    credential = new GoogleCredential.Builder()
                            .setJsonFactory(JSON_FACTORY)
                            .setTransport(TRANSPORT)
                            .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
                            .setFromTokenResponse(JSON_FACTORY.fromString(
                                    tokenResponse.toString(), GoogleTokenResponse.class));

                    Plus pService = new Plus.Builder(TRANSPORT, JSON_FACTORY, credential)
                            .setApplicationName(SystemConfigManager.getCurrentConfig().getAppName())
                            .build();
                    Person me = pService.people().get("me").execute();

                    new GoogleUtils().connectUser(user, me.getEmails().get(0).getValue() , me.getId() , tokenResponse.toString() ,me);

                    GetGooglePermission.this.setVisible(false);
                    onAccessGranted(target);

                } catch (IOException ex ) {
                    onAccessRejected(target,(new Result("Couldn't connect to Google",false)));
                } catch (ResultException e) {
                    onAccessRejected(target, e.getResult());
                }
            }
        };
        add(new Label("getPermissionButtonLabel",buttonText));
        add(new Image("indImage", new PackageResourceReference(IndicatingBehavior.class, "ajax-loader2.gif")));

        bridgeForm.add(bridgeSubmit);
        add(bridgeForm);
    }

    public static enum Scope{
        basicProfile,email,friendList,contacts
    }

    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forUrl("https://apis.google.com/js/client:platform.js"));
        response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
//        new Metronic().renderHeadResponsive(response);
//        response.render(JavaScriptHeaderItem.forReference(new
//                                         JavaScriptResourceReference(Application.class, "src/bootstrap-datepicker/js/bootstrap-datepicker.js")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "src/bootstrap-datepicker/css/datepicker.css")));
    }

    protected abstract void onAccessGranted(AjaxRequestTarget art);
    protected abstract void onAccessRejected(AjaxRequestTarget target, Result e) ;
}
