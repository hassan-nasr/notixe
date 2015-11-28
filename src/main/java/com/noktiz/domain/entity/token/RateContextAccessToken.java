package com.noktiz.domain.entity.token;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by hasan on 2014-12-13.
 */
@Entity
public class RateContextAccessToken extends SingleAccessToken {

    public static final int VALID_TIMES=100;

    @ManyToOne
    private RateContext rateContext;

    public RateContextAccessToken() {
    }

    public RateContextAccessToken(User user, String token, RateContext rateContext) {
        super(user, token, Type.RateContext, rateContext.getTimeBetweenRatings().getDays()*60*24,VALID_TIMES);
        this.rateContext = rateContext;
    }


    public RateContext getRateContext() {
        return rateContext;
    }

    public void setRateContext(RateContext rateContext) {
        this.rateContext = rateContext;
    }
}
