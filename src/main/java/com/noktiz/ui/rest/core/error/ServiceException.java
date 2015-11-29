package com.noktiz.ui.rest.core.error;

import javax.ws.rs.core.Response;

/**
 * If status is not given, Internal Server Error is assumed by the corresponding mapper.
 * Created by abolfazl on 3/31/14.
 */
public class ServiceException extends RuntimeException {
    private final Response.Status status;

    public ServiceException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
