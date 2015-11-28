package com.noktiz.domain.entity.subscribe;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Created by hasan on 2014-11-06.
 */
@Entity
@NamedQueries({
        @NamedQuery(name="loadByUserRateContext", query = "from RateSubscription where user=:user and rateContext = :rateContext")
})
public class RateSubscription extends Subscription {
    RateContext rateContext;

    public RateSubscription(RateContext rateContext, User user) {
        this.rateContext = rateContext;
        this.user=user;
    }

    public RateSubscription(RateContext rateContext) {
        this.rateContext = rateContext;
    }

    public RateSubscription() {
    }

    @ManyToOne
    public RateContext getRateContext() {
        return rateContext;
    }

    public void setRateContext(RateContext rateContext) {
        this.rateContext = rateContext;
    }
}
