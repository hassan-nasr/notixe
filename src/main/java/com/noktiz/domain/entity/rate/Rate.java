/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.*;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.entity.notifications.NotificationRate;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.domain.model.UserFacade;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;

import java.util.Date;
import java.util.Objects;
import javax.persistence.*;

/**
 *
 * @author Hossein
 */
@Entity
@NamedQueries({
//    @NamedQuery(name = "loadLastWeekRating", query = "Select r From Rate as r, where r.owner= :owner and r.friend = :friend and r.sequence=w.weekNumber and w.id=1l"),
    @NamedQuery(name = "loadBetweenDate", query = "Select r From Rate as r where r.sender= :owner and r.receiver = :friend and r.date> :begin and r.date <= :end"),
    @NamedQuery(name = "loadRatesDone", query = "Select r From Rate as r where r.sender= :owner order by date desc ") ,
    @NamedQuery(name = "loadRatesDoneBefore", query = "Select r From Rate as r where r.sender= :owner and r.date<:toDate order by date desc ") ,
    @NamedQuery(name = "loadRatesDoneByContext", query = "Select r From Rate as r where r.sender= :sender and r.context= :context order by date desc ") ,
    @NamedQuery(name = "loadRatesDoneByContextBefore", query = "Select r From Rate as r where r.sender= :sender and r.context= :context and r.date<:toDate order by date desc ") ,
    @NamedQuery(name = "loadRatesOfContext", query = "Select r From Rate as r where r.context= :context order by date desc ") ,
    @NamedQuery(name = "loadRatesOfContextBetweenDate", query = "Select r From Rate as r where r.context= :context and r.date>=:startDate and r.date < :endDate order by date desc ") ,
    @NamedQuery(name = "loadRatesDoneCount", query = "Select count(r) From Rate as r where r.sender= :owner ") ,
    @NamedQuery(name = "loadRatesOfUser", query = "Select r From Rate as r where r.receiver= :owner and (r.notified= :notif1 or r.notified=:notif2)  order by date desc "),
    @NamedQuery(name = "loadRatesOfUserCount", query = "Select count(r) From Rate as r where r.receiver= :owner and (r.notified= :notif1 or r.notified=:notif2)  "),
    @NamedQuery(name = "RateWeekOverView",query = "select year(date),weekofyear(date),context, count(rate),sum(r.rate) from Rate r where r.receiver = :receiver and r.date between :minDate and :maxDate group by 1,2,3"),
})
public class Rate extends BaseObject {

    @Id
    @GeneratedValue
    private Long Id;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;

    @Basic
    private Integer rate;
    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    private RateContext context;
    private Boolean notified = true;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Index(name="rate.date")
    private Date date;
    private boolean showSender = false;

    public Rate() {
    }

    @OneToOne(mappedBy = "rate", cascade = CascadeType.ALL)
    NotificationRate notification;

    public NotificationRate getNotification() {
        return notification;
    }

    public void setNotification(NotificationRate notificationRate) {
        this.notification = notificationRate;
    }

    public RateContext getContext() {
        return context;
    }

    public void setContext(RateContext context) {
        this.context = context;
    }

    public Rate(User sender, Integer rate, String comment, RateContext context, Date date) {
        this.sender = sender;
        this.receiver = context.getUser();
        this.rate = rate;
        this.comment = comment;
        this.context = context;
        this.date = date;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.Id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Rate)) {
            return false;
        }
        final Rate other = (Rate) obj;
        if (!Objects.equals(this.Id, other.Id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rate{" + "owner=" + sender + ", friend=" + receiver + ", rate=" + rate  + ", Date=" + date + '}';
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User owner) {
        this.sender = owner;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User friend) {
        this.receiver = friend;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return StringUtils.abbreviate(comment, 255);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    final static Long weekInMilis = 24 * 7 * 60 * 60 * 1000l;

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
           this.notified = notified;
    }


    public boolean isShowSender() {
        return showSender;
    }

    public void setShowSender(boolean showSender) {
        this.showSender = showSender;
    }

    @OneToOne(mappedBy = "relatedRate")
    private Thread thread;

    public Thread getThread() {
        return thread;
    }

    public ThreadFacade getThread(UserFacade user){
        if (thread == null)
            return null;
        return user.getThread(thread.getId()).object;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
