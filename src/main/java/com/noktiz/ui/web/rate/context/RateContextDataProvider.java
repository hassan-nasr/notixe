package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
import org.apache.commons.lang.NotImplementedException;

import java.util.Date;
import java.util.List;

/**
 * Created by hasan on 2014-12-10.
 */
public class RateContextDataProvider extends ListDataProvider<RateContext> {
    UserFacade user;
    private boolean small;
    boolean hasMore;
    RateContextManager rateContextManager = new RateContextManager();
    public RateContextDataProvider(UserFacade user, boolean small) {
        super(false);
        this.user = user;
        this.small = small;
    }

    @Override
    public List<RateContext> getElements(Integer From, Integer count) {
        throw new NotImplementedException();
    }

    @Override
    public BasePanel getPanel(String name, RateContext obj) {
        return new RateContextViewEditPanel(name,obj,small);
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

    @Override
    public List<RateContext> getElementsBefore(RateContext From, Integer count) {
        Date before = new Date();
        if(From != null){
            before = From.getCreationDate();
        }
        List<RateContext> rateContexts = rateContextManager.loadRateContextsByUserBefore(user, before, count);
        hasMore = rateContexts.size()>=count;
        return rateContexts;
    }

}
