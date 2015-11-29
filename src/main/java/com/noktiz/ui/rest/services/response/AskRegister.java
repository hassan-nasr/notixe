package com.noktiz.ui.rest.services.response;

/**
 * Created by hassan on 03/11/2015.
 */
public class AskRegister {
    private String register_access_token;
    private String captcha_image_url;


    public AskRegister() {
    }

    public AskRegister(String register_access_token, String captcha_image_url) {
        this.register_access_token = register_access_token;
        this.captcha_image_url = captcha_image_url;
    }

    public String getCaptcha_image_url() {
        return captcha_image_url;
    }

    public void setCaptcha_image_url(String captcha_image_url) {
        this.captcha_image_url = captcha_image_url;
    }

    public String getRegister_access_token() {
        return register_access_token;
    }

    public void setRegister_access_token(String register_access_token) {
        this.register_access_token = register_access_token;
    }
}
