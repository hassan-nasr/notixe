package com.noktiz.domain.entity.notifications;

import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by hasan on 9/28/14.
 */

@Entity
public class NotificationEndorse extends BaseNotification {

    @OneToOne
    private Endorse endorse;

    public NotificationEndorse(Endorse endorse) {
        super(endorse.getReceiver());
        this.endorse = endorse;
        displayInHeader=true;
        displayInUpdateList=false;
        setSendMail(true);
    }

    public NotificationEndorse() {
    }

    @Override
    public boolean create() {
        return simpleCreate(owner.getNotificationSettings().getNewEndorseNotification());
    }

    @Override
    protected boolean sendEmail() {
        IEmailProvider.IEmail email = EmailCreator.createNewEndorseEmail(this);
        return sendEmail(email);
    }


    public Endorse getEndorse() {
        return endorse;
    }
}
