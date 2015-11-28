package com.noktiz.domain.entity.privacy;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by hasan on 2014-11-05.
 */


public class RateContextAccess{
    @Id
    private RateContextAccessEmbed obj;

    private Boolean deleted;

    public RateContextAccess( RateContext r, User u) {
        obj = new RateContextAccessEmbed(r,u);
    }


    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public RateContext getRateContext(){
        return obj.getRateContext();
    }
    public User getUser(){
        return obj.getUser();
    }

    public RateContextAccess() {
    }

    public RateContextAccessEmbed getObj() {
        return obj;
    }

    public void setObj(RateContextAccessEmbed obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
