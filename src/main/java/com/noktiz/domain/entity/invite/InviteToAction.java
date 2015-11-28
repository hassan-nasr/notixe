package com.noktiz.domain.entity.invite;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hasan on 2014-11-03.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class InviteToAction extends BaseObject {
    Long id;
    User sender;
    User receiver;
    Boolean completed = false;
    Date sendDate;
    Date completeDate;

    public InviteToAction(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        sendDate = new Date();
    }

    public InviteToAction() {

    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @ManyToOne
    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InviteToAction)) return false;

        InviteToAction that = (InviteToAction) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InviteToAction{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", completed=" + completed +
                ", sendDate=" + sendDate +
                ", completeDate=" + completeDate +
                '}';
    }
}
