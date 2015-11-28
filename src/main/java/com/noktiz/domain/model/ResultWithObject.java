/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.model;

import com.noktiz.domain.entity.User;

import java.util.List;

/**
 *
 * @author hossein
 */
public class ResultWithObject<T> extends Result{
    public T object;

    public ResultWithObject(T object, List<Message> messages, Boolean result) {
        super(messages, result);
        this.object = object;
    }

    public ResultWithObject(Boolean result) {
        super(result);
    }
    
    public ResultWithObject() {
    }

    public ResultWithObject(Result check) {
        this(null,check.messages,check.result);
    }

    public ResultWithObject(Result result, T object) {
        super(result.messages,result.result);
        this.object = object;
    }
}
