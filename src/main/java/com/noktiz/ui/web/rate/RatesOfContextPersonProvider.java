package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.model.TimeManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.wicket.model.StringResourceModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hasan on 2014-11-25.
 */
public class RatesOfContextPersonProvider extends RatesOfPersonProvider {
    public RatesOfContextPersonProvider(RateContext rateContext) {
        super(rateContext);
    }

    private Date toDate;
    private Date fromDate;
    private int from;
    @Override
    public List getElements(Integer from, Integer count) {
        List ret = new ArrayList();
        this.from = from;
//        toDate = rateContext.getReversePeriodStartTime(from);
//        fromDate = rateContext.getReversePeriodStartTime(from + count);
        ArrayList<ImmutablePair<Date,List<Rate>>> periodList = new ArrayList<>();
        for (int i = from; i <= from+count ; i++) {
            periodList.add(new ImmutablePair<Date, List<Rate>>(rateContext.getReversePeriodStartTime(i),new ArrayList<Rate>()));
        }
        List<Rate> rates = rateManager.loadRatesOfContextBetweenDate(rateContext, periodList.get(periodList.size()-1).getLeft(), periodList.get(0).getLeft());
        for (Rate rate : rates) {
            for (int i = 0; i < periodList.size(); i++) {
                if(rate.getDate().getTime()>=periodList.get(i).getLeft().getTime()) {
                    periodList.get(i).right.add(rate);
                    break;
                }
            }
        }
        for (int i = 1; i < periodList.size(); i++) {
            fromDate = periodList.get(i).getLeft();
            toDate = periodList.get(i-1).getLeft();
            this.from=i+from-1;
            ret.addAll(getNewElements(periodList.get(i).getRight()));
        }
        hasMore = periodList.get(periodList.size()-1).getLeft().getTime()>rateContext.getCreationDate().getTime();
        return ret;
    }


    @Override
    protected void add(List<Rate> rates) {
        HeaderTile lastHeader = null;
        Long rateCount=0l;
        Long rateSum =0l;
        for (Rate rate : rates) {
            String friendlyMonthOrWeekRange = getFriendLyTitle();
            if(!friendlyMonthOrWeekRange.equals(lastTimeTitle)){
                if(lastHeader!=null)
                    lastHeader.setStars(rateSum / (double) rateCount);
                rateSum = 0l;
                rateCount =0l;
                lastHeader = new HeaderTile(friendlyMonthOrWeekRange,null);
                addPerson(lastHeader);
                lastTimeTitle = friendlyMonthOrWeekRange;
            }
            if(rate.getRate() != null)
            rateSum+=rate.getRate();
            rateCount++;
            addPerson(new RatingReceivedPerson(rate));
        }
        if(rateCount == 0)
            rateCount=1l;
        if (lastHeader != null) {
            lastHeader.setStars(rateSum / (double) rateCount);
        }

    }

    public String getFriendLyTitle() {
        if (from==0) {
            return new StringResourceModel("CurrentPeriod", null).getObject();
        }
        else if(from==1)
            return new StringResourceModel("PreviousPeriod", null).getObject();
        DateFormat dFrom = new SimpleDateFormat(TimeManager.bestFormatFor(fromDate));
        DateFormat dTo = new SimpleDateFormat(TimeManager.bestFormatFor(toDate));
        return dFrom.format(fromDate)+ new StringResourceModel("to", null).getObject() + dTo.format(toDate);
    }
}
