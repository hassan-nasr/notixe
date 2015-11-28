/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import org.apache.wicket.Component;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hossein
 */
public class Result implements Serializable{

    public List<Message> messages = new ArrayList<>();
    public Boolean result = true;

    public Result() {
        messages= new ArrayList<>();
    }
    
    public Result(List<Message> messages, Boolean result) {
        this.messages = messages;
        this.result = result;
    }

    public Result(Boolean result) {
        messages= new ArrayList<>();
        this.result = result;
    }

    public Result(Message m, boolean result) {
        messages = new ArrayList<>();
        messages.add(m);
        this.result=result;
    }

    public Result(String s, boolean b) {
        messages = new ArrayList<>();
        messages.add(new Message(s,b? Message.Level.success: Message.Level.error));
        result=b;
    }

    public Result(String s, Message.Level level) {
        messages = new ArrayList<>();
        messages.add(new Message(s, level));
        result = (level.getResult());
    }

    public void displayInWicket(Component c) {
        for (Message message : messages) {
            message.displayInWicket(c);
        }
    }

    public Result addRequired(Result result) {
        this.messages.addAll(result.messages);
        this.result = this.result && result.result;
        return this;
    }

    public Result addOptional(Result result) {
        this.messages.addAll(result.messages);
        return this;
    }
    public Result addOverride(Result result) {
        this.messages.addAll(result.messages);
        this.result=result.result;
        return this;
    }


    public static class Message {

        private Object[] obj=null;
        String text;
        public Level level;

        public void displayInWicket(Component c) {
            switch (level) {
                case error:
                    c.error(MessageFormat.format(c.getString(text,null,text),obj));
                    break;
                case info:
                    c.info(MessageFormat.format(c.getString(text, null, text), obj));
                    break;
                case success:
                    c.success(MessageFormat.format(c.getString(text, null, text), obj));
                    break;
                case warning:
                    c.warn(MessageFormat.format(c.getString(text, null, text), obj));
                    break;
            }
        }

        public Message(String text, Level level,Object ... obj) {
            this.text = text;
            this.level = level;
            this.obj=obj;
        }

        public String getText() {
            return text;
        }

        public enum Level {

            error(false), warning(false), info(true), success(true);
            private Boolean result;

            Level(Boolean result) {
                this.result = result;
            }

            public Boolean getResult() {
                return result;
            }

        };
    }

}
