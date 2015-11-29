package com.noktiz.ui.rest.services.response;

/**
 * Created by hassan on 03/11/2015.
 */
public class SimpleResponse {
    private Status status;
    private String message;

    public SimpleResponse() {
    }

    public SimpleResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public static  enum Status{
        Success,
        Failed
    }
}
