package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.social.GoogleSocialConnection;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.model.*;
import com.noktiz.domain.persistance.HSF;
import org.apache.log4j.Logger;
import org.hibernate.Query;

import java.io.Serializable;
import java.util.*;

/**
 * Created by hasan on 2014-11-03.
 */
public class RateContextManager extends BaseManager {


//    InviteToActionManager inviteToActionManager = new InviteToActionManager();
//    public RateContext createNewRateContext(User user, String title) {
//        RateContext ret = new RateContext(title, "", user);
//        saveAsNew(ret);
//        return ret;
//    }

    public ResultWithObject saveAsNew(RateContext rateContext, boolean sendInvite){
        Result check = check(rateContext);
        if(!check.result){
            return new ResultWithObject(check);
        }
//        rateContext.updateAccessList();
        ResultWithObject<Serializable> serializableResultWithObject = super.saveAsNew(rateContext);
        if(sendInvite)
            sendInvites(rateContext);
        return serializableResultWithObject;
    }


    public ResultWithObject update(RateContext rateContext, boolean sendInvite) throws ResultException{
        Result check = check(rateContext);
        if(!check.result)
            return new ResultWithObject(check);
        rateContext = (RateContext) attach(rateContext);
        if(sendInvite)
            sendInvites(rateContext);
//        rateContext.updateAccessList();
        return super.update(rateContext);

    }
    public ResultWithObject merge(RateContext rateContext, boolean sendInvite) throws ResultException{
        Result check = check(rateContext);
        if(!check.result)
            return new ResultWithObject(check);
        rateContext = (RateContext) attach(rateContext);
        if(sendInvite)
            sendInvites(rateContext);
//        rateContext.updateAccessList();
        return super.merge(rateContext);

    }

    private Result check(RateContext rateContext) {
        if(rateContext.getPredefinedQuestion()!= null && !rateContext.getPredefinedQuestion().getTitle().equals(rateContext.getTitle())){
            rateContext.setPredefinedQuestion(null);
        }
        if(rateContext.getTitle().trim().length()==0){
            return new Result("Title can't be empty",false);
        }
        return new Result(true);
    }

    public Result sendInvites(RateContext rateContext){

        UserFacade newUserFacade = new UserFacade(rateContext.getUser());
        newUserFacade.refresh();

        rateContext.setUser(newUserFacade.getUser());
        Set<User> toInviteUsers = rateContext.getInvitedUsers();
//        List<NotificationRateInvite> inPeriodInvites = getInPeriodInvites(rateContext);
        List<NotificationRateInvite> allInvites = getAllInvites(rateContext);

        for (NotificationRateInvite currentInvite : allInvites) {
            if(toInviteUsers.contains(currentInvite.getOwner())) {
                toInviteUsers.remove(currentInvite.getOwner());
                currentInvite.setMutualFriendsCount(null);
                currentInvite.setDeleted(false);
            }
            else{
                currentInvite.setDeleted(true);
                currentInvite.setCreationDate(new Date());
                currentInvite.setMutualFriendsCount(null);
            }
            currentInvite.update();
        }
        for (User toInvitedUser : toInviteUsers) {
            NotificationRateInvite notificationRateInvite = new NotificationRateInvite(toInvitedUser, rateContext);
            notificationRateInvite.create();
        }
        return new Result(new Result.Message("message.new_invitations_send", Result.Message.Level.success),true);
    }


    private List<NotificationRateInvite> getInPeriodInvites(RateContext rateContext) {
        Query q= HSF.get().getCurrentSession().getNamedQuery("InPeriodRateInvites");
        q.setParameter("rateContext",rateContext);
        Calendar startDate= new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_MONTH,-rateContext.getTimeBetweenRatings().days);
        if(startDate.getTime().getTime()<0)
            startDate.setTime(new Date(1));
        setDateString(q,"minSendDate",startDate.getTime());
        List<NotificationRateInvite> currentInvites = q.list();
        return currentInvites;
    }

    private List<NotificationRateInvite> getAllInvites(RateContext rateContext) {
        Query q= HSF.get().getCurrentSession().getNamedQuery("InPeriodRateInvites");
        q.setParameter("rateContext",rateContext);
        Calendar startDate= new GregorianCalendar();
        startDate.setTime(new Date(1));
        setDateString(q,"minSendDate",startDate.getTime());
        List<NotificationRateInvite> currentInvites = q.list();
        return currentInvites;
    }

    public RateContext load(Long contextId) {
        return (RateContext) HSF.get().getCurrentSession().load(RateContext.class,contextId);
    }

    public boolean sendAutomaticInvites(int limit){
        List<RateContext> toInvite = getRateContextsByNextInvitationTime(new Date(),limit);
        for (RateContext rateContext : toInvite) {
            int count = updateSubscribedInvites(rateContext);
            rateContext.setNextInviteTime();
            try {
                update(rateContext, true);
            } catch (ResultException e) {
                Logger.getLogger(this.getClass()).error(e);
            }
            Logger.getLogger(this.getClass()).info(count + " inviting updated for: " + rateContext);
        }
        return limit == toInvite.size();
    }

    private int updateSubscribedInvites(RateContext rateContext) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("upToDateSubscribeNotifications")
                .setParameter("rateContext", rateContext)
                .setParameter("newDate", rateContext.getNextInviteDate());
        return query.executeUpdate();
    }


    private List<RateContext> getRateContextsByNextInvitationTime(Date date, int limit) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("rateContextWithMaxNextInvitationTime");
        setDateString(query,"date",date);
        query.setFetchSize(limit);
        return query.list();
    }

    public void inviteNewFriendIfInvited(RateContext rateContext, User friend) {
        if(!rateContext.getEnable())
            return;
        if(rateContext.getInvitedPersons().isIncludeMyTrustedFriends()){
//            rateContext.grantAccessNoCheck(friend);
            sendNotificationNoCheck(rateContext,friend);
        }
    }

    private void sendNotificationNoCheck(RateContext rateContext, User friend) {
        NotificationRateInvite rateInvite = getInviteNotification(rateContext,friend);
        if(rateInvite == null) {
            NotificationRateInvite notificationRateInvite = new NotificationRateInvite(friend, rateContext);
            notificationRateInvite.create();
        }
        else{
            rateInvite.setDeleted(false);
            rateInvite.update();
        }
    }

    public NotificationRateInvite getInviteNotification(RateContext rateContext, User friend) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("RateInviteByUserAndRateContext");
        query.setParameter("rateContext",rateContext);
        query.setParameter("invitee",friend);
        return (NotificationRateInvite) query.uniqueResult();
    }

    public List<RateContext> loadRateContextsByUserBefore(UserFacade user, Date before, int count){
        if(user.getUser().getId()== null)
            return new ArrayList<>();
        Query query = HSF.get().getCurrentSession().getNamedQuery("LoadRateContextsByUserBefore");
        query.setParameter("user",user.getUser());
        query.setParameter("creationDate",before);
        query.setMaxResults(count);
        return query.list();
    }

    public List<RateContext> loadRateContextsByUser(UserFacade user, int from, int count){
        if(user.getUser().getId()== null)
            return new ArrayList<>();
        Query query = HSF.get().getCurrentSession().getNamedQuery("LoadRateContextsByUser");
        query.setParameter("user",user.getUser());
        query.setFirstResult(from);
        query.setMaxResults(count);
        return query.list();
    }

    public Result inviteWithEmail(RateContext rateContext, String[] emails) {
        Result ret = new Result(true);
        for (String email : emails) {
            if(email.isEmpty())
                continue;
            ResultWithObject<User> userResultWithObject = UserFactory.addFutureUserOrGetWithEmail(email);
            if(!userResultWithObject.result) {
                ret.messages.add(new Result.Message("message.can_not_invite_email", Result.Message.Level.warning,email));
                ret.result=false;
               continue;
            }
            User toInviteUser = userResultWithObject.object;
            if(toInviteUser.equals(rateContext.getUser())) {
                ret.messages.add(new Result.Message("message.can_not_add_yourself", Result.Message.Level.warning));
                ret.result=false;
                continue;
            }
            rateContext.getInvitedPersons().includeUser(toInviteUser);
            if(!toInviteUser.getActive()) {
                SocialConnection s = new GoogleSocialConnection(rateContext.getUser(), toInviteUser.getEmail(), false);
                new SocialConnectionManager().saveAsNew(s);
            }
        }
        update(rateContext);
        return ret;
    }

//    public void setActiveOfRateContextInvites(RateContext rateContext, Boolean active){
//        rateContext.setEnable(active);
//        Query query = HSF.get().getCurrentSession().getNamedQuery("(In)activeInviteByContext");
//        query.setParameter("rateContext", rateContext);
//        query.setParameter("deleted",!active);
//        query.executeUpdate();
//    }
}
