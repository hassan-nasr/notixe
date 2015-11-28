package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.TimeManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.friend.PersonListProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hasan on 9/26/14.
 */
public class RatesDonePersonProvider extends PersonListProvider {

    private final RateContext rateContext;
    RateManager rateManager = new RateManager();
    UserFacade user;

    public RatesDonePersonProvider(UserFacade user) {
        this.user = user;
        rateContext = null;
//        List<Rate> rates= rateManager.loadLastRatesDoneByUser(user.getUser(), 0, 1000000);
//        add(rates);
    }

    public RatesDonePersonProvider(RateContext rateContext, UserFacade user) {
        this.rateContext = rateContext;
        this.user = user;
//        List<Rate> rates= rateManager.loadLastRatesDoneByUserInContext(user, rateContext, 0, 1000000);
//        add(rates);
    }

    String lastTimeTitle = "";

    private void add(List<Rate> rates) {
        for (Rate rate : rates) {
            String friendlyMonthOrWeekRange = TimeManager.getFriendlyMonthOrWeekRange(rate.getDate(), new Date());
            if (!friendlyMonthOrWeekRange.equals(lastTimeTitle)) {
                addPerson(new HeaderTile(friendlyMonthOrWeekRange, null) {

                    @Override
                    public IModel<String> getBGClass() {
                        return Model.of(" bg-darkgreen ");
                    }

                });
                lastTimeTitle = friendlyMonthOrWeekRange;
            }
            addPerson(new AnsweredPerson(rate, true));
        }
    }

    @Override
    public List getElements(Integer from, Integer count) {
        List<Rate> rates;
        if (rateContext != null) {
            rates = rateManager.loadLastRatesDoneByUserInContext(user, rateContext, from, count);
        } else {
            rates = rateManager.loadLastRatesDoneByUser(user, from, count);
        }
        return getList(rates, count);
    }

    private List getList(List<Rate> rates, Integer count) {
        Integer from;
        hasMore = rates.size() >= count;
        from = getPersons().size();
        add(rates);
        int to = getPersons().size();
        List ret = new ArrayList();
        List<? extends IPerson> persons1 = getPersons();
        for (int i = from; i < to; i++) {
            ret.add(persons1.get(i));
        }
        return ret;
    }

    @Override
    public List<IPerson> getElementsBefore(IPerson From, Integer count) {
        Date startDate = From != null ? ((AnsweredPerson) From).getRate().getDate() : new Date();
        List<Rate> rates;
        if (rateContext != null) {
            rates = rateManager.loadLastRatesDoneByUserInContextBefore(user, rateContext, startDate, count);
        } else {
            rates = rateManager.loadLastRatesDoneByUserBefore(user, startDate, count);
        }
        return getList(rates, count);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

}
