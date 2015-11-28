package com.noktiz.domain.entity.subscribe;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.persistance.HSF;
import org.hibernate.Query;

/**
 * Created by hasan on 2014-11-06.
 */
public class RateSubscriptionManager extends BaseManager{
    public RateSubscription load(User user,RateContext rateContext){
        Query query = HSF.get().getCurrentSession().getNamedQuery("loadByUserRateContext");
        query.setParameter("user", user);
        query.setParameter("rateContext", rateContext);
        RateSubscription result = (RateSubscription) query.uniqueResult();
        return result;
    }
}
