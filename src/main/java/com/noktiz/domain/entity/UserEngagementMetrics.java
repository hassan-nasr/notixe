/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.entity;

import com.noktiz.domain.entity.rate.Rate;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author Hossein
 */
public class UserEngagementMetrics extends BaseObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private UserInThread owner;
    @OneToOne
    private UserInThread assigner;
    private Thread thread;
    private Rate thanks;
    private Rate politness;
    private Rate effectiveness;
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserEngagementMetrics)) {
            return false;
        }
        final UserEngagementMetrics other = (UserEngagementMetrics) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInThread getOwner() {
        return owner;
    }

    public void setOwner(UserInThread owner) {
        this.owner = owner;
    }

    public UserInThread getAssigner() {
        return assigner;
    }

    public void setAssigner(UserInThread assigner) {
        this.assigner = assigner;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Rate getThanks() {
        return thanks;
    }

    public void setThanks(Rate thanks) {
        this.thanks = thanks;
    }

    public Rate getPolitness() {
        return politness;
    }

    public void setPolitness(Rate politness) {
        this.politness = politness;
    }

    public Rate getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(Rate effectiveness) {
        this.effectiveness = effectiveness;
    }

    @Override
    public String toString() {
        return "UserEngagementMetrics{" + "owner=" + owner + ", assigner=" + assigner + ", thread=" + thread + '}';
    }
    
}
