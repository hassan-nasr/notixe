/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.TimeManager;
import com.noktiz.domain.model.UserFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author Hossein
 */
public class RatesOfDataProvider implements IDataProvider<Serializable>{

    UserFacade owner;
    private RateManager rateManager;

    public RatesOfDataProvider(UserFacade owner, RateManager rateManager) {
        this.owner = owner;
        this.rateManager = rateManager;
    }
    
    @Override
    public Iterator<? extends Serializable> iterator(long first, long count) {
        List<Rate> lastRatesOfUser = rateManager.loadLastRatesOfUser(owner.getUser(), (int) first, (int) count, false);
//        new RateManager().getStartOfWeekDate(null)
        List<Serializable> ret = new ArrayList<>();
        if(lastRatesOfUser.size()== 0){
            ret.add(new StringResourceModel("NoRatingsFound", null).getObject());
            return ret.iterator();
        }
        String lastMonthRange ="";
        for (Rate rate : lastRatesOfUser) {
            String friendlyMonthOrWeekRange = TimeManager.getFriendlyMonthOrWeekRange(rate.getDate(), new Date());
            String [] dateSep = null;
            if(!friendlyMonthOrWeekRange.equals(lastMonthRange)){
                dateSep= new String[]{friendlyMonthOrWeekRange,""};
                lastMonthRange = friendlyMonthOrWeekRange;
            }
            ret.add(new ImmutablePair<>(dateSep,rate));
        }
        return ret.iterator();
    }

    @Override
    public long size() {
        return rateManager.countRatesOfUser(owner.getUser(),false);
    }

    @Override
    public IModel<Serializable> model(Serializable object) {
        return new Model(object);
    }

    @Override
    public void detach() {
    }
    
}
