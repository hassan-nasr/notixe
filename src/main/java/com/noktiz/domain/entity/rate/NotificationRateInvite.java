package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.domain.persistance.HSF;
import org.hibernate.Query;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hasan on 2014-11-03.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "InPeriodRateInvites",query = "from NotificationRateInvite r where r.rateContext =:rateContext and r.sendDate>:minSendDate"),
        @NamedQuery(name = "RateInviteByUserAndRateContext",query = "from NotificationRateInvite r where r.rateContext =:rateContext and r.owner=:invitee"),
        @NamedQuery(name = "DeleteRateInviteByUserAndUser",query = "update NotificationRateInvite r set deleted=true where r.sender =:sender and r.owner=:invitee"),
        @NamedQuery(name = "(In)activeInviteByContext",query = "update NotificationRateInvite r set deleted=:deleted where r.rateContext =:rateContext"),
        @NamedQuery(name = "upToDateSubscribeNotifications",query = "update NotificationRateInvite r set creationDate = :newDate where r.rateContext =:rateContext and r.subscribed = true"),
        @NamedQuery(name = "loadRateInviteBeforeDate",query = "From NotificationRateInvite as bn where bn.owner= :user and bn.sender = :sender and bn.deleted=false and bn.displayInUpdateList=true and (bn.originalCreationTime<:toDate or (bn.originalCreationTime is null or bn.originalCreationTime=:toDate) and bn.id<:beforeId ) order by bn.originalCreationTime desc,id desc"),
        @NamedQuery(name = "mutualInviteCount",query = "select count(f.friend.id) From Friendship f where f.friendshipOwner=:owner and f.friend.id in (select owner.id From NotificationRateInvite as bn where bn.rateContext = :rateContext)"),
})
public class NotificationRateInvite extends BaseNotification{


    @ManyToOne
    private RateContext rateContext;

    @ManyToOne
    private User sender;

    public NotificationRateInvite() {
        super();
    }

    Integer mutualFriendsCount = null;

    public Integer getMutualFriendsCount() {
        if(mutualFriendsCount == null){
            loadMutualFriendCount();
        }
        return mutualFriendsCount;
    }

    private void loadMutualFriendCount() {
        Query query = HSF.get().getCurrentSession().getNamedQuery("mutualInviteCount");
        query.setParameter("owner",owner);
        query.setParameter("rateContext",rateContext);
        mutualFriendsCount = (int)(long) query.uniqueResult();
    }

    public void setMutualFriendsCount(Integer mutualFriendsCount) {
        this.mutualFriendsCount = mutualFriendsCount;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Index(name = "notificationRI.oct")
    Date originalCreationTime = null;

    public Date getOriginalCreationTime() {
        if (originalCreationTime == null) {
            originalCreationTime=rateContext.getCreationDate();
        }
        return originalCreationTime;
    }

    public void setOriginalCreationTime(Date originalCreationTime) {
        this.originalCreationTime = originalCreationTime;
    }

    @Override
    public boolean create() {
        Friendship friendship = Friendship.load(sender, owner);
        boolean dontFalow=false;
        if(friendship!=null){
            dontFalow=friendship.getDontFollow();
        }
        return createWithFollowCheck(owner.getNotificationSettings().getRateInviteNotification(),
                dontFalow);
    }

    public NotificationRateInvite(User receiver, RateContext rateContext) {
        super(receiver);
        this.rateContext = rateContext;
        this.sender=rateContext.getUser();
        displayInUpdateList = true;
        displayInHeader = false;
    }

    @Override
    protected boolean sendEmail() {
        IEmailProvider.IEmail email = EmailCreator.createRateInviteEmail(this);
        return sendEmail(email);
    }

    public static NotificationRateInvite load(Long id){
        return (NotificationRateInvite) HSF.get().getCurrentSession().load(NotificationRateInvite.class,id);
    }

    public RateContext getRateContext() {
        return rateContext;
    }

    public void setRateContext(RateContext rateContext) {
        this.rateContext = rateContext;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public static List<NotificationRateInvite> loadByUserAndSender(User receiver, User sender, NotificationRateInvite before, Integer count) {
        if(receiver.getId() == null || sender.getId() == null || count==0){
            return new ArrayList<>();
        }
        Query namedQuery;
        namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadRateInviteBeforeDate");
        namedQuery.setParameter("user", receiver);
        namedQuery.setParameter("sender", sender);
//        q.setDate("cd",new Date(before.getCreationDate().getTime()+23*3600*1000+1155*1000));
        Date beforeDate = (before==null)?new Date():new Date(before.getOriginalCreationTime().getTime());
        Long beforeId = (before==null)?Long.MAX_VALUE:before.getId();
        BaseManager.setDateString(namedQuery, "toDate", beforeDate);
        namedQuery.setLong("beforeId", beforeId);
        namedQuery.setMaxResults(count);
        List<NotificationRateInvite> list = namedQuery.list();
        return list;
    }

}
