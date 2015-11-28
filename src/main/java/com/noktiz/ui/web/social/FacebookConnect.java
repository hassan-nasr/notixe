package com.noktiz.ui.web.social;

import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.facebook.FacebookUtils;
import com.noktiz.domain.social.facebook.RegisterFacebookConnection;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.behavior.IndicatingBehavior;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hasan on 7/19/14.
 */
public abstract class FacebookConnect extends Panel{
    WebMarkupContainer getPermissionsButton;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    public FacebookConnect(String id, final UserFacade user, final List<String> requiredPermissions, String connectButtonText, String description, boolean needAcceptTerms) {
        super(id);
        user.refresh();
        final NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        add(feedback);
        feedback.setOutputMarkupId(true);
        getPermissionsButton = new WebMarkupContainer("getPermissionButton"){
            @Override
            public void renderHead(IHeaderResponse response) {
                response.render(JavaScriptContentHeaderItem.forReference(new PackageResourceReference(FacebookConnect.class, "facebookConnect.js")));
                response.render(new OnDomReadyHeaderItem("initFB('"+ SystemConfigManager.getCurrentConfig().getProperty("FB_APP_ID")+"')"));
            }
        };
        if(description==null)
            description="";
        add(new MultiLineLabel("description",Model.of(description)){
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if(getDefaultModelObject()==null){
                    setVisible(false);
                }
            }
        });
        boolean needPermissions = checkForRequiredPermissions(user, requiredPermissions);
        getPermissionsButton.setOutputMarkupId(true);
        /*if(!needPermissions) {
            getPermissionsButton.setVisible(false);
        }*/
        add(getPermissionsButton);
        StringBuilder permissions = new StringBuilder();
        for (int i = 0; i < requiredPermissions.size(); i++) {
            String requiredPermission = requiredPermissions.get(i);
            permissions.append(requiredPermission);
            if(i!= requiredPermissions.size()-1)
                permissions.append(",");
        }
        WebMarkupContainer acceptTerm = new WebMarkupContainer("agreeCheck");
        add(acceptTerm);
        if(!needAcceptTerms)
            acceptTerm.setVisible(false);
        getPermissionsButton.add(new AttributeAppender("onclick","myFacebookLogin('"+permissions+"')"));
        Label getPermissionButtonLabel = new Label("getPermissionButtonLabel",connectButtonText);
        getPermissionsButton.add(getPermissionButtonLabel);
        getPermissionsButton.add(new Image("indImage",new PackageResourceReference(IndicatingBehavior.class, "ajax-loader2.gif")));
        Form form = new Form("registerFacebookData");
        add(form);
        final TextField facebookId = new TextField("facebookId",Model.of(""));
        form.add(facebookId);
        final TextField access_token = new TextField("access_token", Model.of(""));
        form.add(access_token);
        AjaxSubmitLink submitLink = new IndicatingAjaxSubmitLink2("submitData") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    target.add(getPermissionsButton);
                    new RegisterFacebookConnection().register(user,facebookId.getDefaultModelObjectAsString(),access_token.getDefaultModelObjectAsString());
                    boolean now = checkForRequiredPermissions(user, requiredPermissions);
                    if(!now)
                        onAccessGranted(target);
                } catch (ResultException e) {
                    e.getResult().displayInWicket(this);
                    target.add(feedback);
                }
            }
        };
        form.add(submitLink);
        if(!needPermissions)
            onAccessGranted(null);
    }

    public abstract void onAccessGranted(AjaxRequestTarget target);

    private boolean checkForRequiredPermissions(UserFacade user, List<String> requiredPermissions) {
        boolean needPermissions = false;
        Set<String> permissions = new FacebookUtils().getUserPermissions(user.getUser());
        for (String requiredPermission : requiredPermissions) {
            if(!permissions.contains(requiredPermission)){
                needPermissions=true;
                break;
            }
        }
        return needPermissions;
    }
}
