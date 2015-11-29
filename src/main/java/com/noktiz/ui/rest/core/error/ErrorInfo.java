package com.noktiz.ui.rest.core.error;

import javax.ws.rs.core.Response;

/**
 * Created by abolfazl on 3/31/14.
 */
public class ErrorInfo {

    private final Response.Status status;
    private final String message;
    private final Long transactionId;

    ErrorInfo(Long transactionId, Response.Status status, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Long getTransactionId() {
        return transactionId;
    }
}
