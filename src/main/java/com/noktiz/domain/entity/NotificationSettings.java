/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import com.noktiz.domain.persistance.HSF;

import java.awt.font.TextHitInfo;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.sound.sampled.Control;

import org.hibernate.Session;

/**
 *
 * @author Hossein
 */
@Entity
public class NotificationSettings implements Serializable{



    public static NotificationSettings getNewNotificationSettings() {
        NotificationSettings ns = new NotificationSettings();
//        ns.setFriendAddYouNotification(EmailState.periodically);
//        ns.setNewMessageNotification(EmailState.always);
//        ns.setNewRateNotification(EmailState.always);
//        ns.setFriendJoinedNotification(EmailState.periodically);
//        ns.setNewEndorseNotification(EmailState.always);
//        ns.setRateInviteNotification(EmailState.always);
        return ns;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User user;
    private EmailState friendJoinedNotification = EmailState.periodically;
    private EmailState newMessageNotification = EmailState.always;
    private EmailState friendAddYouNotification = EmailState.periodically;
    private EmailState newRateNotification = EmailState.always;
    private EmailState newEndorseNotification = EmailState.periodically;
    private EmailState rateInviteNotification = EmailState.always;

    public EmailState getFriendAddYouNotification() {
        return friendAddYouNotification;
    }

    public void setFriendAddYouNotification(EmailState friendAddYouNotification) {
        this.friendAddYouNotification = friendAddYouNotification;
    }

    public EmailState getFriendJoinedNotification() {
        return friendJoinedNotification;
    }

    public void setFriendJoinedNotification(EmailState friendJoinedNotification) {
        this.friendJoinedNotification = friendJoinedNotification;
    }

    public EmailState getNewMessageNotification() {
        return newMessageNotification;
    }

    public void setNewMessageNotification(EmailState newMessageNotification) {
        this.newMessageNotification = newMessageNotification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailState getNewRateNotification() {
        return newRateNotification;
    }

    public EmailState getNewEndorseNotification() {
        return newEndorseNotification;
    }

    public void setNewEndorseNotification(EmailState newEndorseNotification) {
        this.newEndorseNotification = newEndorseNotification;
    }

    public EmailState getRateInviteNotification() {
        return rateInviteNotification;
    }

    public void setRateInviteNotification(EmailState rateInviteNotification) {
        this.rateInviteNotification = rateInviteNotification;
    }

    public enum EmailState {
        always,periodically,never;
    }
    public void save(){
        Session cs = HSF.get().getCurrentSession();
        cs.saveOrUpdate(this);
        cs.flush();
    }

    public void setNewRateNotification(EmailState newRateNotification) {
        this.newRateNotification = newRateNotification;
    }
}
