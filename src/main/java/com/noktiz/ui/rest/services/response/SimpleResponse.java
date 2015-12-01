package com.noktiz.ui.rest.services.response;

/**
 * Created by hassan on 03/11/2015.
 */
public class SimpleResponse {
    Status status;
    String message;
    Object object;


    public SimpleResponse() {
    }

    public SimpleResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public SimpleResponse(Status status, String message, Object object) {
        this.status = status;
        this.message = message;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
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
    public static enum Status{
        Success,
        Failed
    }
}
