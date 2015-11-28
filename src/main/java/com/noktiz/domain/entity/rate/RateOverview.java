package com.noktiz.domain.entity.rate;

import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by hasan on 2014-10-04.
 */

public class RateOverview implements Comparable<RateOverview>{
    private Integer endorseCount = 0;
    private double endorseAvg = 0;
    private RateContext endorseContext;
    private Date from;
    private Date to;

    public RateOverview() {
    }

    public RateOverview(Integer endorseCount, double endorseAvg, RateContext endorseContext, Date from, Date to) {
        this.endorseCount = endorseCount;
        this.endorseAvg = endorseAvg;
        this.endorseContext = endorseContext;
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }


    public RateOverview(RateContext endorseContext) {
        this.endorseContext = endorseContext;
    }

    public Integer getEndorseCount() {
        return endorseCount;
    }

    public void setEndorseCount(Integer endorseCount) {
        this.endorseCount = endorseCount;
    }

    public double getEndorseAvg() {
        return endorseAvg;
    }

    public void setEndorseAvg(double endorseAvg) {
        this.endorseAvg = endorseAvg;
    }

    @ManyToOne
    public RateContext getEndorseContext() {
        return endorseContext;
    }

    public void setEndorseContext(RateContext endorseContext) {
        this.endorseContext = endorseContext;
    }

    @Override
    public int compareTo(RateOverview o) {
        return from.compareTo(o.from);
    }
}
