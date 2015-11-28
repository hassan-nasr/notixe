/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity.notifications;

import com.noktiz.domain.entity.NotificationSettings;
import com.noktiz.domain.entity.User;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import com.noktiz.domain.entity.Thread;
import javax.persistence.Entity;
import java.security.acl.Owner;

/**
 *
 * @author Hossein
 */
@Entity
public class NotificationThanks extends BaseNotification {

    @ManyToOne(fetch = FetchType.LAZY)
    private Thread thread;
    @ManyToOne
    private User sender;

    public NotificationThanks() {
    }

    public NotificationThanks(Thread thread, User owner,  User sender) {
        super(owner);
        this.sender = sender;
        displayInHeader=true;
        displayInUpdateList=false;
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public boolean create() {
        return simpleCreate(NotificationSettings.EmailState.never);
    }

    @Override
    public String toString() {
        return "NotificationThanks{" + "thread=" + thread + '}';
    }

    public User getSender() {
        return sender;
    }
}
