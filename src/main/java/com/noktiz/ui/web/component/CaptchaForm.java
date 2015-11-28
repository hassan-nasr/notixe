package com.noktiz.ui.web.component;

import com.noktiz.domain.Utils.UrlContent;
import com.noktiz.domain.system.SystemConfigManager;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Hossein on 12/12/2014.
 */
public class CaptchaForm extends Form {
    Boolean validateCaptcha =null;
    public CaptchaForm(String id) {
        super(id);
    }
    public CaptchaForm(String id, IModel model) {
        super(id, model);
    }

    @Override
    protected void beforeUpdateFormComponentModels() {
        super.beforeUpdateFormComponentModels();
        String captcha = getRequest().getPostParameters().getParameterValue("g-recaptcha-response").toOptionalString();
        validateCaptcha =validateCaptcha(captcha);
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
//        String captcha = getRequest().getPostParameters().getParameterValue("g-recaptcha-response").toOptionalString();
//        validateCaptcha =validateCaptcha(captcha);
    }
    private boolean validateCaptcha(String s){
        try {
//            String captchaSecret = "6LdnKf8SAAAAALuCBcOrdgdJBLluRwkF5I5LpDjM";
            String captchaSecret = SystemConfigManager.getCurrentConfig().getProperty("reCaptchaSecret");
            UrlContent urlContent = new UrlContent(new URL("https://www.google.com/recaptcha/api/siteverify?secret=" + captchaSecret + "&response=" + s),null);
            return urlContent.getContent().contains("true");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean getValidateCaptcha() {
        return validateCaptcha;
    }
}
