package com.noktiz.ui.rest.services.response;

/**
 * Created by hassan on 01/12/2015.
 */
public class SendMessageResponse {
    SimpleResponse.Status status;
    String message;
    Long messageId;

    public SendMessageResponse(SimpleResponse.Status status, String message, Long messageId) {
        this.status = status;
        this.message = message;
        this.messageId = messageId;
    }

    public SimpleResponse.Status getStatus() {
        return status;
    }

    public void setStatus(SimpleResponse.Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
