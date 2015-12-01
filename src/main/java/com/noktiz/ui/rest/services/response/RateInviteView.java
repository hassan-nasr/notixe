package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.rate.NotificationRateInvite;

import java.util.Date;

/**
 * Created by hassan on 30/11/2015.
 */
public class RateInviteView {
    Long rateInviteId;
    Long senderId;
    Long rateContextId;
    String Title;
    String description;
    Integer mutualFriend;
    Date date;
    Boolean seen;

    public RateInviteView(NotificationRateInvite rateInvite) {
        rateInviteId = rateInvite.getId();
        senderId = rateInvite.getSender().getId();
        rateContextId = rateInvite.getRateContext().getId();
        Title = rateInvite.getRateContext().getTitle();
        description = rateInvite.getRateContext().getDescription();
        mutualFriend = rateInvite.getMutualFriendsCount();
        date = rateInvite.getCreationDate();
        seen = rateInvite.getSeen();
    }

    public Long getRateInviteId() {
        return rateInviteId;
    }

    public void setRateInviteId(Long rateInviteId) {
        this.rateInviteId = rateInviteId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRateContextId() {
        return rateContextId;
    }

    public void setRateContextId(Long rateContextId) {
        this.rateContextId = rateContextId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMutualFriend() {
        return mutualFriend;
    }

    public void setMutualFriend(Integer mutualFriend) {
        this.mutualFriend = mutualFriend;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
