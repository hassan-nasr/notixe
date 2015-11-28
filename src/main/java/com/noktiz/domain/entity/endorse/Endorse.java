package com.noktiz.domain.entity.endorse;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hasan on 2014-10-13.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "loadAllMyEndorses", query = "From Endorse where receiver = :receiver and deleted=false order by date desc "),
        @NamedQuery(name = "findDuplicateEndorse", query = "From Endorse where receiver = :receiver and sender =:sender and context=:context ")
})
//@Table(
//        uniqueConstraints = { @UniqueConstraint(columnNames =
//                { "sender_id", "receiver_id", "context" }) })
public class Endorse extends BaseObject{

    private Long id;
    private User sender;
    private User receiver;
    private String context;
    private Date date;


    public Endorse() {
    }

    public Endorse(String context, User sender, User receiver) {
        this.context = context;
        this.sender = sender;
        this.receiver = receiver;
        date=new Date();
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
    @NaturalId
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @ManyToOne
    @NaturalId
    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    @NaturalId
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = StringUtils.abbreviate(context, 255);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Endorse{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", context='" + context + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endorse)) return false;

        Endorse endorse = (Endorse) o;

        if (id != null ? !id.equals(endorse.id) : endorse.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
