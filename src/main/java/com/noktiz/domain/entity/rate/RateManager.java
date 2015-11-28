package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.Message;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.NotificationRate;
import com.noktiz.domain.model.*;
import com.noktiz.domain.persistance.HSF;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hasan on 9/19/14.
 */
public class RateManager extends BaseManager {

    public Rate loadLastWeekRating(User owner, User friend) {

        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadBetweenDate");
        namedQuery.setParameter("owner", owner);
        namedQuery.setParameter("friend", friend);
        GregorianCalendar now = new GregorianCalendar();
        now.setTime(new Date());
        Calendar sw = getStartOfWeekDate(now);
        namedQuery.setParameter("begin", sw.getTimeInMillis());
        sw.add(Calendar.WEEK_OF_YEAR, 1);
        namedQuery.setParameter("end", sw.getTimeInMillis());
        Rate uniqueResult = (Rate) namedQuery.uniqueResult();
        return uniqueResult;
    }

    public List<Rate> loadLastRatesDoneByUser(UserFacade owner, int from, int count) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesDone");
        namedQuery.setParameter("owner", owner.getUser());
        namedQuery.setFirstResult(from);
        namedQuery.setMaxResults(count);
        List<Rate> ret = namedQuery.list();
        return ret;
    }

    public Long countRatesDoneByUser(User owner) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesDoneCount");
        namedQuery.setParameter("owner", owner);
        Long ret = (Long) namedQuery.uniqueResult();
        return ret;
    }

    public List<Rate> loadLastRatesOfUser(User owner, int from, int count, boolean loadUnPublish) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesOfUser");
        namedQuery.setParameter("owner", owner);
        namedQuery.setParameter("notif1", true);
        namedQuery.setParameter("notif2", !loadUnPublish);
        namedQuery.setFirstResult(from);
        namedQuery.setMaxResults(count);
        List<Rate> ret = namedQuery.list();
        return ret;
    }

    public Long countRatesOfUser(User owner, boolean loadUnPublish) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesOfUserCount");
        namedQuery.setParameter("owner", owner);
        namedQuery.setParameter("notif1", true);
        namedQuery.setParameter("notif2", !loadUnPublish);
        Long ret = (Long) namedQuery.uniqueResult();
        return ret;
    }

    public List<Rate> loadBetweenWeek(User owner, User friend, Calendar beginWeekInclusive, Calendar endWeekExclusive) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadBetweenDate");
        namedQuery.setParameter("owner", owner);
        namedQuery.setParameter("friend", friend);
        namedQuery.setParameter("begin", beginWeekInclusive.getTime());
        namedQuery.setParameter("end", endWeekExclusive);
        List<Rate> rates = (List<Rate>) namedQuery.list();
        return rates;
    }

    public Result save(Rate rate) {
        if (RateContext.RateState.Required.equals(rate.getContext().getRequireRate()) && rate.getRate() == null) {
            return new Result(new Result.Message("message.rating_required", Result.Message.Level.error), false);
        }
        if (rate.getRate() != null && (rate.getRate() < 1 || rate.getRate() > 5)) {
            return new Result(new Result.Message("message.rating_required", Result.Message.Level.error), false);
        }
        if (RateContext.RateState.Unavailable.equals(rate.getContext().getRequireRate())) {
            rate.setRate(null);
        }

        Result result = new Result(true);
        try {
            super.update(rate);
            Result.Message m = new Result.Message("message.respond_saved", Result.Message.Level.success);
            result.addRequired(new Result(m, true));
            return result;
        } catch (Exception e) {
            Logger.getLogger(RateManager.class).error(e);
            return new Result(new Result.Message(e.getMessage(), Result.Message.Level.error), false);
        }
    }

    public Result rateFriend(Rate rate, NotificationRateInvite notificationRateInvite) {
//
//        if (!new UserFacade(rate.getSender()).doIOwnerTheFriendshipOf(new UserFacade(rate.getReceiver()))) {
//            return new Result(new Result.Message("You are not a friend of " + rate.getReceiver().getName() + ".", Result.Message.Level.error), false);
//        }
        Block blocked = Block.loadBlocked(rate.getSender(), rate.getReceiver());

        if (blocked != null) {
            Result result = new Result(new Result.Message("message.can_not_rate_because_block", Result.Message.Level.error,rate.getReceiver().getName() ), false);
            return result;
        }
        rate.setContext(new RateContextManager().load(rate.getContext().getId()));
        List<Rate> lastRate = loadLastRatesDoneByUserInContext(new UserFacade(rate.getSender()), rate.getContext(), 0, 1);
        if(lastRate.size()>0 && !rate.getContext().canRateHavingPreviousRating(lastRate.get(0))){
            Result result = new Result(new Result.Message("message.can_not_respond_anymore" , Result.Message.Level.error), false);
            return result;
        }
        if (!rate.getContext().getEnable()) {
            Result result = new Result(new Result.Message("message.respond_disabled" , Result.Message.Level.error), false);
            return result;
        }
        HSF.get().beginTransaction();
        try {
            Result result = save(rate);
            if(result.result == false){
                return result;
            }
            if (notificationRateInvite != null && notificationRateInvite.getSeen()==false) {
                notificationRateInvite.markAsRead();
            }
            if (rate.getNotification() == null) {
                NotificationRate notif = new NotificationRate(rate);
                notif.create();
                rate.setNotification(notif);
            } else {
                rate.getNotification().setSeen(false);
            }
            update(rate);
            HSF.get().commitTransaction();
//            if (rate.getNotification() == null) {
//                NotificationRate notif = new NotificationRate(rate);
//                notif.create();
//                rate.setNotification(notif);
//            } else {
//                rate.getNotification().setSeen(false);
//            }
            return result;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            rate.setId(null);
            return new Result("couldn't rate please try again later",false);
        }
//        HSF.get().getCurrentSession().getTransaction().commit();

    }

    public Calendar getStartOfWeekDate(Calendar cal) {
        // get today and clear time of day
        Calendar currentWeek = null;
        if (currentWeek == null) {
            cal.setFirstDayOfWeek(Calendar.SATURDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            currentWeek = cal;
        }
        Calendar sw = new GregorianCalendar();
        sw.setTimeInMillis(currentWeek.getTimeInMillis());
        return sw;
    }

    public ThreadFacade createReplyThread(Rate rate) throws ResultException {
        ResultWithObject<ThreadFacade> result = new UserFacade(rate.getReceiver()).createThread(new UserFacade(rate.getSender()), getReplyThreadTitle(rate));
        if (!result.result) {
            throw new ResultException(result, null);
        }
        result.object.getThread().setStarterCanBeThanked(false);
        result.object.getThread().setTargetCanBeThanked(true);

        ThreadFacade thread = result.object;
        rate.setThread(thread.getThread());
        MessageFacade message = thread.createMessage(new UserFacade(rate.getReceiver()), new UserFacade(rate.getSender()), "", Message.DIR.ltr).object;
//        message.setReciverRead(true);
        thread.setRelatedRate(rate);
        thread.getThread().setTargetVisible(rate.isShowSender());
        thread.getThread().setStarterVisible(true);
        thread.save();
        return thread;

    }

    private String getReplyThreadTitle(Rate rate) {

        String star;
        if(rate.getRate() != null && rate.getRate()>0)
            star = new StringBuilder("(").append(rate.getRate()).append(" stars) ").toString();
        else
            star = "";
        return new StringBuilder().append(StringUtils.abbreviate(new StringBuilder().append(star).append(rate.getComment() == null?"":rate.getComment()).toString(), 40))
                .append(" -> ").append(rate.getContext().getTitle()).toString();
    }

    public Map<RateContext, List<RateOverview>> getRateOverview(User user, Date minDate, Date maxDate) {
        if (maxDate == null) {
            maxDate = new Date();
        }
        if (minDate == null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(maxDate);
            c.add(Calendar.YEAR, -5);
            minDate = c.getTime();
        }
        Query query = HSF.get().getCurrentSession().getNamedQuery("RateWeekOverView");
        query.setParameter("receiver", user);

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        query.setString("minDate", df.format(minDate));
        query.setString("maxDate", df.format(maxDate));
        List<Object[]> result = query.list();
        Map<RateContext, List<RateOverview>> ret = new HashMap<>();
        for (Object[] objects : result) {
            Calendar cld = Calendar.getInstance();
            Integer year = (Integer) objects[0];
            Integer week = (Integer) objects[1];
            RateContext context = (RateContext) objects[2];
            cld.set(Calendar.YEAR, year);
            cld.set(Calendar.WEEK_OF_YEAR, week);
            Date from = cld.getTime();
            cld.add(Calendar.DATE, 6);
            Date to = cld.getTime();
            List<RateOverview> current = ret.get(context);
            if (current == null) {
                current = new ArrayList<>();
                ret.put(context, current);
            }
            Long rate = (Long) objects[4];
            Long count = (Long) objects[3];
            current.add(new RateOverview(count.intValue(), rate / (double) count, context, from, to));
        }
        for (List<RateOverview> overviews : ret.values()) {
            Collections.sort(overviews);
        }
        return ret;
    }

    public List<Rate> loadLastRatesDoneByUserInContext(UserFacade sender, RateContext rateContext, Integer from, Integer count) {
        User owner = sender.getUser();
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesDoneByContext");
        namedQuery.setParameter("sender", owner);
        namedQuery.setParameter("context", rateContext);
        namedQuery.setFirstResult(from);
        namedQuery.setMaxResults(count);
        return namedQuery.list();
    }

    public List<Rate> loadRatesOfContext(RateContext rateContext, Integer from, Integer count) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesOfContext");
        namedQuery.setParameter("context", rateContext);
        namedQuery.setFirstResult(from);
        namedQuery.setMaxResults(count);
        return namedQuery.list();
    }

    public List<Rate> loadRatesOfContextBetweenDate(RateContext rateContext, Date fromDate, Date toDate) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesOfContextBetweenDate");
        namedQuery.setParameter("context", rateContext);
        namedQuery.setParameter("startDate", fromDate);
        namedQuery.setParameter("endDate", toDate);
        return namedQuery.list();
    }

    public List<Rate> loadLastRatesDoneByUserInContextBefore(UserFacade sender, RateContext rateContext, Date toDate, Integer count) {
        User owner = sender.getUser();
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesDoneByContextBefore");
        namedQuery.setParameter("sender", owner);
        namedQuery.setParameter("context", rateContext);
        setDateString(namedQuery, "toDate", toDate);
        namedQuery.setMaxResults(count);
        return namedQuery.list();
    }

    public List<Rate> loadLastRatesDoneByUserBefore(UserFacade user, Date toDate, Integer count) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRatesDoneBefore");
        namedQuery.setParameter("owner", user.getUser());
        setDateString(namedQuery, "toDate", toDate);
        namedQuery.setMaxResults(count);
        List<Rate> ret = namedQuery.list();
        return ret;
    }

//    static  List<RateContext> rateContexts;
//
//    public  List<RateContext> getRateContexts() {
//        if(rateContexts == null)
//            rateContexts = loadContexts();
//        return rateContexts;
//    }
//    private List<RateContext> loadContexts() {
//        Query query = HSF.get().getCurrentSession().getNamedQuery("LoadAllRateContexts");
//        return query.list();
//    }
//    public  void setRateContexts(List<RateContext> rateContexts) {
//        RateManager.rateContexts = rateContexts;
//    }
//    private static RateContext defaultRateContext = null;
//    public RateContext getDefaultContext() {
//        return    getRateContexts().get(0);
//
//    }

}
