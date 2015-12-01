package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.model.MessageFacade;
import com.noktiz.domain.model.ThreadFacade;

import java.util.Date;

/**
 * Created by hassan on 30/11/2015.
 */
public class MessageView {
    Long conversationId;
    Long messageId;
    Long sender;
    String message;
    Date date;
    Boolean read;
    Boolean draft;

    public MessageView(MessageFacade message, ThreadFacade threadFacade) {
        conversationId = threadFacade.getId();
        messageId = message.getId();
        sender = message.getSender().isAnonymous()? null: message.getSender().getUser().getId();
        this.message = message.getContent();
        date = message.getSendDate();
        read = message.getReciver().equals(threadFacade.getMe())?message.isReciverRead():true;
        draft = message.isDraft();
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
