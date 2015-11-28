package com.noktiz.domain.model;

import java.io.IOException;

/**
 * Created by hasan on 9/10/14.
 */
public class ResultException extends Exception {
    Result result;
    Throwable cause ;
    public ResultException(Result result, Throwable cause ) {
        this.result = result;
        this.cause = cause;
    }

    @Override
    public void printStackTrace() {
        System.err.println(result);
        if(cause!=null)
            cause.printStackTrace();
        else
            super.printStackTrace();
    }

    @Override
    public String toString() {
        return "ResultException{" +
                "result=" + result +
                '}';
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
