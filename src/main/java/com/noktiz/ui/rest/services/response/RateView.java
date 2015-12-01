package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.model.UserFacade;

import java.util.Date;

/**
 * Created by hassan on 30/11/2015.
 */
public class RateView {
    Long rateId;
    Long rateContextId;
    String message;
    Integer score;
    Long ConversationId;
    Date date;
    Long sender;
    Long receiver;



    public RateView(Rate rate, UserFacade requester) {
        rateContextId = rate.getContext().getId();
        rateId = rate.getId();
        message = rate.getComment();
        score = rate.getRate();
        date = rate.getDate();
        if(requester.getUser().equals(rate.getSender()))
            sender = rate.getSender().getId();
        receiver = rate.getReceiver().getId();
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public Long getRateContextId() {
        return rateContextId;
    }

    public void setRateContextId(Long rateContextId) {
        this.rateContextId = rateContextId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getConversationId() {
        return ConversationId;
    }

    public void setConversationId(Long conversationId) {
        ConversationId = conversationId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }
}
