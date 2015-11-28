package com.noktiz.ui.web.component;

import com.noktiz.domain.system.SystemConfigManager;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by Hossein on 12/12/2014.
 */
public class CaptchaPanel extends WebMarkupContainer{
    public CaptchaPanel(String id, Boolean compact) {
        super(id);
        add(new AttributeAppender("class","g-recaptcha"));
//                .add(new AttributeAppender("data-sitekey", "6LdnKf8SAAAAAEbykOewcr6Z9OwYANBQGTFyR9O9"));
        setOutputMarkupId(true);
        if(compact)
            add(new AttributeModifier("data-size","compact"));
    }
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CaptchaPanel.class,"CaptchaPanel.js")));
        response.render(JavaScriptHeaderItem.forUrl("https://www.google.com/recaptcha/api.js?render=explicit&onload=onLoadCaptchaAPI").setAsync(true).setDefer(true));
        String reCaptchaPublic = SystemConfigManager.getCurrentConfig().getProperty("reCaptchaPublic");
//        String reCaptchaPublic = "6LdnKf8SAAAAAEbykOewcr6Z9OwYANBQGTFyR9O9";
        response.render(OnDomReadyHeaderItem.forScript("captchaEnable('"+getMarkupId()+ "','" + reCaptchaPublic + "')"));
    }
}
