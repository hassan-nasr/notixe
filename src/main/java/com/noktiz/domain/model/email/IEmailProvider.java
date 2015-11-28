/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model.email;

/**
 *
 * @author Hossein
 */
public abstract class IEmailProvider {
    public static IEmailProvider get(){
        return new IEmailProvider() {

            @Override
            public boolean send(String toEmail, IEmail email) {
                GmailEmailer emailer = GmailEmailer.getEmailer();
                emailer.SetEmailSetting(toEmail, email.title, email.content, email.html);
                emailer.start();
                return true;
            }
        };
    }
    public abstract  boolean send(String toEmail,IEmail email);
    
    public static class IEmail {
        String content;
        String title;
        String html;
        public IEmail() {
            
        }
    }
}
