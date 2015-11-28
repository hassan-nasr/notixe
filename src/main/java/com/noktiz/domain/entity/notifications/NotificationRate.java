package com.noktiz.domain.entity.notifications;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by hasan on 9/28/14.
 */

@Entity
public class NotificationRate extends BaseNotification {

    @OneToOne
    Rate rate;

    public NotificationRate(Rate rate) {
        super(rate.getReceiver());
        this.rate = rate;
        displayInHeader=true;
        displayInUpdateList=false;
        setSendMail(true);

    }

    public NotificationRate() {
    }

    @Override
    public boolean create() {
        return simpleCreate(owner.getNotificationSettings().getNewRateNotification());
    }

    @Override
    protected boolean sendEmail() {
        IEmailProvider.IEmail email = EmailCreator.createNewRateEmail(this);
        return sendEmail(email);
    }

    public boolean isAnonymous() {
        return !rate.isShowSender();
    }

    public Rate getRate() {
        return rate;
    }
}
