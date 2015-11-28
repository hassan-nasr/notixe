/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity.notifications;

import com.noktiz.domain.entity.Message;
import com.noktiz.domain.entity.NotificationSettings;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author Hossein
 */
@Entity
public class NotificationNewMessage extends BaseNotification{
    @OneToOne
    private Message message;
    private boolean annonymous;
    @Deprecated
    public NotificationNewMessage() {
    }

    public NotificationNewMessage(Message message, User owner,boolean annonymous) {
        super(owner);
        this.message = message;
        this.annonymous=annonymous;
        displayInHeader=true;
        displayInUpdateList=false;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isAnnonymous() {
        return annonymous;
    }

    public void setAnnonymous(boolean annonymous) {
        this.annonymous = annonymous;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public boolean create() {
        return simpleCreate(owner.getNotificationSettings().getNewMessageNotification());
    }

    @Override
    protected boolean sendEmail() {
        IEmailProvider.IEmail email = EmailCreator.getEmailCreator().createNewMessageEmail(this);
        return sendEmail(email);
    }

    @Override
    public String toString() {
        return "NotificationNewMessage{" + "message=" + message + '}';
    }
    
}
