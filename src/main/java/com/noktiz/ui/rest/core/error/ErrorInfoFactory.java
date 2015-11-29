package com.noktiz.ui.rest.core.error;

import javax.ws.rs.core.Response;

/**
 * Created by abolfazl on 3/31/14.
 */
public class ErrorInfoFactory {
    public static ErrorInfo createAndLogError(Response.Status status, String message) {
        Long transId = null;//TODO:ABOLFAZL: transactionId should be read from somewhere global like a thread local, error should be logged here!
        return new ErrorInfo(transId, status, message != null ? message.replace("\"", "'") : message);
    }
}
