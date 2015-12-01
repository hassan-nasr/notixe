package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.notifications.*;
import com.noktiz.domain.entity.rate.NotificationRateInvite;

import java.util.Date;

/**
 * Created by hassan on 30/11/2015.
 */
public class NotificationView {
    Long notificationId;
    Boolean seen;
    Long objectId;
    String desc1;
    String desc2;
    Date date;
    NotificationView.NotificationType type;
    public NotificationView(BaseNotification notification) {
        notificationId=notification.getId();
        seen = notification.getSeen();
        date = notification.getCreationDate();
    }

    public NotificationView(NotificationAddFriend notificationAddFriend) {
        this(((BaseNotification) notificationAddFriend));
        objectId=notificationAddFriend.getFriend().getId();
        desc1 = notificationAddFriend.getFriend().getName();
        type=NotificationType.AddFriend;
    }
    public NotificationView(NotificationNewMessage notificationNewMessage) {
        this(((BaseNotification) notificationNewMessage));
        objectId=notificationNewMessage.getMessage().getId();
        desc1 = notificationNewMessage.getMessage().getSenderFacade().getUser().getName();
        type=NotificationType.NewMesssage;
    }
    public NotificationView(NotificationEndorse notificationEndorse) {
        this(((BaseNotification) notificationEndorse));
        objectId=notificationEndorse.getEndorse().getId();
        desc1 = notificationEndorse.getEndorse().getContext();
        desc2 = notificationEndorse.getEndorse().getSender().getName();
        type=NotificationType.Endrosee;
    }
    public NotificationView(NotificationRate notificationRate) {
        this(((BaseNotification) notificationRate));
        objectId=notificationRate.getRate().getId();
        desc1 = notificationRate.getRate().getContext().getTitle();
        type=NotificationType.Rate;
    }
    public NotificationView(NotificationThanks notificationThanks) {
        this(((BaseNotification) notificationThanks));
        objectId=notificationThanks.getThread().getId();
        desc1 = notificationThanks.getSender().getName();
        type=NotificationType.Thanks;
    }
    public NotificationView(NotificationRateInvite notificationRateInvite) {
        this(((BaseNotification) notificationRateInvite));
        objectId=notificationRateInvite.getRateContext().getId();
        desc1 = notificationRateInvite.getSender().getName();
        type=NotificationType.RateInvite;
    }


    public enum NotificationType {
        AddFriend, NewMesssage, Endrosee, Rate, Thanks, RateInvite,
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
