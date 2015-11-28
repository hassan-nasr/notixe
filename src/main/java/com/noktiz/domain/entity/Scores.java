/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Hossein
 */
@Embeddable
public class Scores implements Serializable{

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    private Long thanksCount;
    private Long thanksUser;
    private Long friendCount;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date friendUpdate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date friendAddOrRemove;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getThanksCount() {
        return thanksCount;
    }

    public void setThanksCount(Long thanksCount) {
        this.thanksCount = thanksCount;
    }

    public Long getThanksCountNotExact() {
        return (thanksCount!=0&&thanksCount/5*5==0)?1:(thanksCount/5*5);
    }
    
    public Long getThanksUser() {
        return thanksUser;
    }

    public Long getThanksUserNotExact() {
        return (thanksUser!=0 && thanksUser/5*5==0)?1:(thanksUser/5*5);
    }

    public void setThanksUser(Long thanksUser) {
        this.thanksUser = thanksUser;
    }

    public Long getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(Long friendCount) {
        this.friendCount = friendCount;
    }

    public Date getFriendUpdate() {
        return friendUpdate;
    }

    public void setFriendUpdate(Date friendUpdate) {
        this.friendUpdate = friendUpdate;
    }

    public Date getFriendAddOrRemove() {
        return friendAddOrRemove;
    }

    public void setFriendAddOrRemove(Date friendAdd) {
        this.friendAddOrRemove = friendAdd;
    }
    
}
