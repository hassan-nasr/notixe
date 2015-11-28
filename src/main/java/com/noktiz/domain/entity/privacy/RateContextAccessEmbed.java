package com.noktiz.domain.entity.privacy;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by hasan on 2014-11-05.
 */


public class RateContextAccessEmbed implements Serializable {

    RateContext rateContext;

    User user;

    public RateContextAccessEmbed() {
    }

    public RateContextAccessEmbed(RateContext rateContext, User user) {

        this.rateContext = rateContext;
        this.user = user;
    }


    @ManyToOne
    public RateContext getRateContext() {
        return rateContext;
    }

    public void setRateContext(RateContext rateContext) {
        this.rateContext = rateContext;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
