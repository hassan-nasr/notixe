/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import javax.mail.internet.MailDateFormat;

/**
 *
 * @author Hossein
 */
public class TimeManager {

    public static Calendar changeTimezone(Date date, String timezone) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timezone));
        calendar.setTime(date);
        return calendar;
    }
    public static Calendar changeTimezone(Date date, int timezoneOffset) {
        Calendar calendar = new GregorianCalendar(new SimpleTimeZone(timezoneOffset, "automatic"));
        calendar.setTime(date);
        return calendar;
    }
    final static String nears="h:mm aa",fars="MMM d";
    final static SimpleDateFormat near = new SimpleDateFormat(nears);
    final static SimpleDateFormat far = new SimpleDateFormat(fars);

    public static String getDate(Calendar calendar) {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format;
        if (currentTimeMillis - calendar.getTimeInMillis() < 60*24*60000) {
            format = near;
        } else {
            format = far;
        }
        format.setTimeZone(calendar.getTimeZone());
        return format.format(calendar.getTime());
    }
    public static String getDifferenceFrom(Calendar base,Calendar date){
        final int byear = base.get(Calendar.YEAR);
        final int dyear = date.get(Calendar.YEAR);
        final int bmonth = base.get(Calendar.MONTH);
        final int dmonth = date.get(Calendar.MONTH);
        final int bday = base.get(Calendar.DAY_OF_MONTH);
        final int dday = date.get(Calendar.DAY_OF_MONTH);
        final int bhour = base.get(Calendar.HOUR_OF_DAY);
        final int dhour = date.get(Calendar.HOUR_OF_DAY);
        final int bminute = base.get(Calendar.MINUTE);
        final int dminute = date.get(Calendar.MINUTE);
        if(byear!=dyear){
            return byear-dyear +" year ago";
        }
        else if(bmonth!=dmonth){
            return bmonth-dminute +" month ago";
        }
        else if(bday!=dday){
            return bday-dday+" day ago";
        }
        else if(bhour!=dhour){
            return bhour-dhour+" hour ago";
        }
        else if(bminute!=dminute){
            return bminute-dminute+" minute ago";
        }
        return "just now";
    }
    public static String getDateWithDifference(Calendar calendar) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.getTimeZone());
        return getDate(calendar)+" ("+getDifferenceFrom( gregorianCalendar,calendar)+")";
    }

    public static String getFriendlyMonthOrWeekRange(Date date, Date dase) {
        Calendar datec = new GregorianCalendar();
        datec.setTime(date);
        int datewn = datec.get(Calendar.WEEK_OF_YEAR);
        int datemn = datec.get(Calendar.MONTH)+12*datec.get(Calendar.YEAR);
        Calendar basec = new GregorianCalendar();
        basec.setTime(dase);
        int basewn = basec.get(Calendar.WEEK_OF_YEAR);
        int basemn = basec.get(Calendar.MONTH)+12*datec.get(Calendar.YEAR);
        if(basewn == datewn)
            return "This week";
        if(basewn+1 == datewn)
            return "Next week";
        if(basewn+2 == datewn)
            return "Two week later";
        if(basewn-1 == datewn)
            return "Last week";
        if(basewn-2 == datewn)
            return "Two week age";

        switch (datemn-basemn){
            case 0:
                if(datewn>basemn)
                    return "Later this month";
                return "Earlier this month";
            case 1:
                return "Next month";
            case -1:
                return "Last month";
            case -2:
                return "Two month ago";
        }
        return new SimpleDateFormat("MMMM YYYY").format(date);
    }

    public static String bestFormatFor(Date d) {
     long currentTimeMillis = System.currentTimeMillis();
        String format;
        if (currentTimeMillis - d.getTime()< 60*24*60000) {
            format = nears;
        } else {
            format = fars;
        }
        return format;
    }
}
