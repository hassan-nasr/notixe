package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.TimeManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hasan on 9/26/14.
 */
public class RatesOfPersonProvider extends PersonListProvider {
    RateManager rateManager = new RateManager();
    private UserFacade user;
    protected RateContext rateContext;
    public RatesOfPersonProvider(UserFacade user) {
        this.user = user;
        rateContext = null;
//        List<Rate> rates= rateManager.loadLastRatesOfUser(user.getUser(), 0, 1000000,false);
//        add(rates);

    }
    String lastTimeTitle = "";
    protected void add(List<Rate> rates) {
        HeaderTile lastHeader = null;
        Long rateCount=0l;
        Long rateSum =0l;
        for (Rate rate : rates) {
            String friendlyMonthOrWeekRange = TimeManager.getFriendlyMonthOrWeekRange(rate.getDate(), new Date());
            if(!friendlyMonthOrWeekRange.equals(lastTimeTitle)){
                if(lastHeader!=null)
                    lastHeader.setStars(rateSum / (double) rateCount);
                rateSum = 0l;
                rateCount =0l;
                lastHeader = new HeaderTile(friendlyMonthOrWeekRange,null);
                addPerson(lastHeader);
                lastTimeTitle = friendlyMonthOrWeekRange;
            }
            if(rate.getRate()!=null) {
                rateSum += rate.getRate();
                rateCount++;
            }
            addPerson(new RatingReceivedPerson(rate));
        }
        if(rateCount == 0)
            rateCount=1l;
        if (lastHeader != null) {
            lastHeader.setStars(rateSum / (double) rateCount);
        }

    }

    public RatesOfPersonProvider(RateContext rateContext) {
        this.rateContext = rateContext;
        rateContext = null;
//        List<Rate> rates= rateManager.loadRatesOfContext(rateContext, 0, 1000000);
//        add(rates);
    }

    @Override
    public List getElements(Integer from, Integer count) {
        List<Rate> rates;
        if(rateContext!=null)
            rates = rateManager.loadRatesOfContext(rateContext, from, count);
        else
            rates = rateManager.loadLastRatesOfUser(user.getUser(), from, count, false);
        hasMore=rates.size()>=count;
        List ret = getNewElements(rates);
        return ret;
    }

    protected List getNewElements(List<Rate> rates) {
        Integer from;
        from=getPersons().size();
        add(rates);
        int to=getPersons().size();
        List ret = new ArrayList();
        List<? extends IPerson> persons1 = getPersons();
        for (int i = from; i < to; i++) {
            ret.add(persons1.get(i));
        }
        return ret;
    }


    @Override
    public boolean isSelectable() {
        return false;
    }

    protected class RatingReceivedPerson extends IPerson<Rate> {
        private Rate rate;


        public RatingReceivedPerson(final Rate rate1) {
            this.rate = rate1;
            addAction(new ReplyAction(rate));
        }

        @Override
        public Rate getObj() {
            return rate;
        }

        @Override
        public String getImageUrl() {
            if(rate.isShowSender())
                return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                        ImageManagement.getUserImageParameter(new UserFacade(rate.getSender()), ImageManagement.ImageSize.medium)).toString();
            return null;
        }

        @Override
        public IModel<String> getBGClass() {
            return Model.of("bg-blue");
        }

        
        @Override
        public String getImageLink() {
            if(rate.isShowSender())
                return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid",rate.getSender().getId())).toString();
            return null;
        }

        @Override
        public String getSubtitleLink() {
            if(rate.isShowSender())
                return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid",rate.getSender().getId())).toString();
            return null;
        }

        @Override
        public IModel<String> getSubTitle() {
            if(rate.isShowSender())
                return Model.of(rate.getSender().getName());
            return new StringResourceModel("YourFriend",null);
        }

        @Override
        public IModel<String> getTitle() {
//            if(rate.getComment()!=null)
                return Model.of(rate.getContext().getTitle());
//            return Model.of("");
        }


        @Override
        public IModel<Boolean> getSearchInSubtitle() {
            return Model.of(true);
        }

        @Override
        public IModel<String> getDesc() {
            return Model.of(rate.getComment());
        }

        @Override
        public boolean getStarsEnable() {
            return rate.getRate() != null;
        }

        @Override
        public IModel<Double> getStars() {
            return Model.of(rate.getRate()!=null?rate.getRate().doubleValue():0);
        }
    }
}
