package com.noktiz.domain.entity.rate;

/**
 * Created by hasan on 2014-11-03.
 */
public enum SimplePeriod {
    SingleTime("Only One Time",10000000),
    Week("Once a Week",7),
    TwoWeek("Once Every 2 Week", 14),
    Month("Once a Month", 30),
    ThreeMonth("Once Every 3 Month", 90),
    Year("Once a Year",365);

    String name;
    Integer days;
    SimplePeriod(String name, Integer Days) {
        this.name = name;
        days = Days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getFriendlyName() {
        if(equals(SingleTime))
            return "";
        return name.toLowerCase();
    }
}
