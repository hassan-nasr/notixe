package com.noktiz.domain.entity;

import com.noktiz.domain.entity.notifications.NotificationNewMessage;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.annotations.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hossein
 */
@Entity
public class Message extends BaseObject{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "text")
    private String content;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Index(name="message.sendDate")
    private Date sendDate;
//    private String title;
    @ManyToOne
    private Thread thread;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User reciver;
    private Boolean reciverRead;
    @OneToOne(fetch = FetchType.LAZY)
    private NotificationNewMessage notification;
    private Boolean draft;
    private DIR dir;
    @ManyToOne
    private Block block;
    public boolean isReciverRead() {
        return reciverRead;
    }

    public NotificationNewMessage getNotification() {
        return notification;
    }

    public void setNotification(NotificationNewMessage notification) {
        this.notification = notification;
    }

    public DIR getDir() {
        return dir;
    }

    public void setDir(DIR dir) {
        this.dir = dir;
    }
    

    public Boolean isDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
    

    public void setReciverRead(Boolean reciverRead) {
        this.reciverRead = reciverRead;
    }
    

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReciver() {
        return reciver;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setReciver(User reciver) {
        this.reciver = reciver;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    @Lob
    public void setContent(String content) {
        this.content = content;
    }

    public void save() {
        final Session cs = HSF.get().getCurrentSession();
        cs.saveOrUpdate(this);
        cs.flush();
    }
    
    public void delete(){
        if(id!=null){
            HSF.get().getCurrentSession().delete(this);
            return;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o == null) {
            return false;
        }
        if (!(o instanceof Message)) {
            return false;
        }

        Message message = (Message) o;

        if (id != message.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", content=" + content + ", sender=" + sender + '}';
    }

    public UserFacade getSenderFacade() {
        return new UserFacade(sender,!thread.isUserVisible(sender));
    }
    public UserFacade getReceiverFacade() {
        return new UserFacade(reciver,!thread.isUserVisible(reciver));
    }


    public enum DIR{
        ltr,rtl
    }
}
