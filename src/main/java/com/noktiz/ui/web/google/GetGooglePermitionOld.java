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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.google.GoogleUtils;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Level;

///**
// *
// * @author Sina
// */
public abstract class GetGooglePermitionOld extends Panel {
    private static final JavaScriptResourceReference MYPAGE_JS =
            new JavaScriptResourceReference(GetGooglePermitionOld.class, "javas.js");

    protected static final String CLIENT_ID = "368179572834-2cb2ss663jep3n0g3faf40ohq2ci82jp.apps.googleusercontent.com";
    protected static final String CLIENT_SECRET = "Mq9m-rdd8UjBp7t0NGFwh4c4";
    protected static String CLIENT_CODE;
    protected static final HttpTransport TRANSPORT = new NetHttpTransport();
    protected static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    protected static final Gson GSON = new Gson();
    protected GoogleCredential credential;

    private static final long MAX_EXPIRE_TIME = 500;

    private String permisionsToString(List<Scope> permisions){
        String str = "";
        for(Scope s:permisions){
            if(s.equals(Scope.basicProfile)){
                str+="https://www.googleapis.com/auth/userinfo.profile ";
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

    public GetGooglePermitionOld(String id, final List<Scope> permitions, final UserFacade user) {
        super(id);
        setOutputMarkupId(true);
        
        final WebMarkupContainer gConnect= new WebMarkupContainer("gConnect");
        gConnect.setOutputMarkupId(true);
        gConnect.add(new AttributeModifier("data-scope", Model.of(permisionsToString(permitions))));
        final WebMarkupContainer gConnectButton= new WebMarkupContainer("gConnectButton");
        gConnectButton.add(new AttributeModifier("data-scope", Model.of(permisionsToString(permitions))));
        gConnect.add(gConnectButton);
        add(gConnect);
        
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        
        Form bridgeForm = new Form("bridgeForm");
        Label stateLabel = new Label("stateLabel",Model.of(""));
        stateLabel.setDefaultModelObject(state);
        bridgeForm.add(stateLabel);
        final TextField bridgeText0 = new TextField("bridgeText0",Model.of(""));
        bridgeForm.add(bridgeText0);
        final TextField bridgeText1 = new TextField("bridgeText1",Model.of(""));
        bridgeForm.add(bridgeText1);
        
        try {
            String access_Token = (user.getCredential().getGoogleInfo().getGoogle_access_token());
            if(access_Token!=null){
                GoogleTokenResponse lastToken;
                lastToken = JSON_FACTORY.fromString(access_Token, GoogleTokenResponse.class);
                //System.err.println("time: "+lastToken.getAccessToken());
                URL googleInfoUrl = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token="+lastToken.getAccessToken());
                HttpURLConnection googleInfoResponse = (HttpURLConnection) googleInfoUrl.openConnection();
                googleInfoResponse.connect();
                
                int status = googleInfoResponse.getResponseCode();
                
                if(status == 201 || status == 200){
                    JsonElement je = (new JsonParser()).parse(new InputStreamReader((InputStream) googleInfoResponse.getContent()));
                    JsonObject jobject = je.getAsJsonObject();
                    if(jobject.get("error")!=null){
                        throw new GoogleInfoParseError();
                    }
                    
                    boolean haveScopes = true;
                    
                    String permisionString = permisionsToString(permitions);
                    for(String permistion:permisionString.split(" ")){
                        if(!permistion.equals("")){
                            if(!jobject.get("scope").getAsString().contains(permistion)){
                                haveScopes = false;
                            }
                        }
                    }
                    if(jobject.get("expires_in").getAsLong()<MAX_EXPIRE_TIME){
                        haveScopes = false;
                    }
                    
                    if(haveScopes == true){
                        
                        credential = new GoogleCredential.Builder()
                                .setJsonFactory(JSON_FACTORY)
                                .setTransport(TRANSPORT)
                                .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
                                .setFromTokenResponse(JSON_FACTORY.fromString(
                                        lastToken.toString(), GoogleTokenResponse.class));
                        
                        credential.refreshToken();
                        
                        GetGooglePermitionOld.this.setVisible(false);
                        onAccessGranted(null);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (GoogleInfoParseError ex) {
            java.util.logging.Logger.getLogger(GetGooglePermitionOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        AjaxSubmitLink bridgeSubmit= new IndicatingAjaxSubmitLink2("bridgeSubmit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                
                CLIENT_CODE = bridgeText0.getDefaultModelObjectAsString();
//                String storedState = ((UserSession)getSession()).getState();
                if(true){
                    try {
                        GoogleTokenResponse tokenResponse =
                                new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY,
                                        CLIENT_ID, CLIENT_SECRET, CLIENT_CODE, "postmessage").execute();
//                        GoogleIdToken idToken = tokenResponse.parseIdToken();
//                        ((UserSession)getSession()).setToken(tokenResponse.toString());

                        
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
//                        System.err.println(me.getEmails().get(0).getValue());
//                        System.err.println(me.getId());
//                        System.err.println(tokenResponse.toString());
//                        System.err.println(((UserSession)getSession()).getUser().getName());
                        
                        new GoogleUtils().connectUser(user, me.getEmails().get(0).getValue() , me.getId() , tokenResponse.toString() ,me);
                        
                        GetGooglePermitionOld.this.setVisible(false);
                        onAccessGranted(target);
                        
                    } catch (IOException ex) {
                        onAccessRejected(target);
                    } catch (ResultException e) {
                        onAccessRejected(target);
                    }

                }
                else{
                    onAccessRejected(target);
                }
            };
        };
        
        bridgeForm.add(bridgeSubmit);
        add(bridgeForm);
    }
    
    public static enum Scope{
        basicProfile,email,friendList,contacts
    }
    
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
//        new Metronic().renderHeadResponsive(response);
//        response.render(JavaScriptHeaderItem.forReference(new
//                                         JavaScriptResourceReference(Application.class, "src/bootstrap-datepicker/js/bootstrap-datepicker.js")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "src/bootstrap-datepicker/css/datepicker.css")));
    }
    
    protected abstract void onAccessGranted(AjaxRequestTarget art);
    protected abstract void onAccessRejected(AjaxRequestTarget target) ;
}
