package com.noktiz.ui.rest.core.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by abolfazl on 3/31/14.
 */
@Provider
public class ErrorInfoWriter implements MessageBodyWriter<ErrorInfo> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ErrorInfo.class == type;
    }

    @Override
    public long getSize(ErrorInfo errorInfo, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ErrorInfo errorInfo, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"transactionId\": ");
        sb.append(errorInfo.getTransactionId());
        sb.append(", \"status\": ");
        sb.append(errorInfo.getStatus().getStatusCode());
        sb.append(", \"message\": \"");
        sb.append(errorInfo.getMessage());
        sb.append("\"}");
        entityStream.write(sb.toString().getBytes("utf8"));
    }
}
