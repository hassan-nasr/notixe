/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.entity;

import com.noktiz.domain.persistance.HSF;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 *
 * @author Hossein
 */
public class UserInThread extends BaseObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String aliasName;
    private Boolean visible;
    @ManyToOne
    private Block blockeded;
    @ManyToOne
    private Block blockedIndirectly;
    @ManyToOne
    private Block blockedIndirectlyCompletely;
    @OneToOne
    private UserEngagementMetrics gotMetrics;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserInThread)) {
            return false;
        }
        final UserInThread other = (UserInThread) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserInThread{" + "user=" + user + ", aliasName=" + aliasName + ", visible=" + visible + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Block getBlockeded() {
        return blockeded;
    }

    public void setBlockeded(Block blockeded) {
        this.blockeded = blockeded;
    }

    public Block getBlockedIndirectly() {
        return blockedIndirectly;
    }

    public void setBlockedIndirectly(Block blockedIndirectly) {
        this.blockedIndirectly = blockedIndirectly;
    }

    public Block getBlockedIndirectlyCompletely() {
        return blockedIndirectlyCompletely;
    }

    public void setBlockedIndirectlyCompletely(Block blockedIndirectlyCompletely) {
        this.blockedIndirectlyCompletely = blockedIndirectlyCompletely;
    }

    public UserEngagementMetrics getGotMetrics() {
        return gotMetrics;
    }

    public void setGotMetrics(UserEngagementMetrics gotMetrics) {
        this.gotMetrics = gotMetrics;
    }
    public void save(){
        try{
            HSF.get().getCurrentSession().saveOrUpdate(this);
        }catch(HibernateException ex){
            Logger.getLogger(UserInThread.class).error("error in saving UserInThread user="+user,ex);
        }
        
    }
}
