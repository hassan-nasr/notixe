/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity.notifications;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.NotificationSettings;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.domain.persistance.HSF;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Hossein
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "loadByUserOnHeader", query = "From BaseNotification as bn where bn.owner= :user and bn.displayInHeader=true and bn.deleted=false order by bn.creationDate desc"),
        @NamedQuery(name = "loadUnreadByUserOnHeader", query = "From BaseNotification as bn where bn.owner= :user and bn.seen=false and bn.deleted=false and (bn.displayInHeader is null or bn.displayInHeader=true) order by bn.creationDate desc"),
        @NamedQuery(name = "loadUpdateList", query = "From BaseNotification as bn where bn.owner= :user  and bn.deleted=false and bn.displayInUpdateList=true order by bn.creationDate desc"),
        @NamedQuery(name = "loadUpdateListBeforeDate", query = "From BaseNotification as bn where bn.owner= :user  and bn.deleted=false and bn.displayInUpdateList=true and (bn.creationDate<:toDate or bn.creationDate=:toDate and bn.id<:beforeId ) order by bn.creationDate desc,id desc"),
        @NamedQuery(name = "loadSubscribedUpdateList", query = "From BaseNotification as bn where bn.owner= :user  and bn.deleted=false and bn.displayInUpdateList=true and bn.subscribed=true order by bn.creationDate desc"),
        @NamedQuery(name = "loadSubscribedUpdateListBeforeDate", query = "From BaseNotification as bn where bn.owner= :user and bn.deleted=false and bn.displayInUpdateList=true and bn.subscribed=true and (bn.creationDate<:toDate or bn.creationDate=:toDate and bn.id<:beforeId ) order by bn.creationDate desc,id desc"),
        @NamedQuery(name = "loadNewUpdateList", query = "From BaseNotification as bn where bn.owner= :user  and bn.deleted=false and bn.displayInUpdateList=true and bn.seen=false order by bn.creationDate desc"),
        @NamedQuery(name = "loadNewUpdateListBeforeDate", query = "From BaseNotification as bn where bn.owner= :user  and bn.deleted=false and bn.displayInUpdateList=true and bn.seen=false and (bn.creationDate<:toDate or bn.creationDate=:toDate and bn.id<:beforeId ) order by bn.creationDate desc,id desc"),
        @NamedQuery(name = "getNumberOfUnreadNotificationsOnHeader",
                query = "Select count(*) From BaseNotification as bn where bn.owner= :user and bn.deleted=false  and bn.seen=false and bn.displayInHeader=true"),
        @NamedQuery(name = "findNextUserToNotify", query = "Select bn.owner.id From BaseNotification as bn where bn.owner.id > :user_id and bn.deleted=false  and bn.seen=false and bn.notifiedWithEmail=false and bn.sendMail=true order by bn.owner.id"),
        @NamedQuery(name = "getEmailNotification", query = "From BaseNotification as bn where bn.owner.id= :user_id and bn.seen=false and bn.deleted=false and bn.notifiedWithEmail=false and bn.sendMail=true")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class BaseNotification extends BaseObject<BaseNotification> {

    private Boolean subscribed = false;

    public static List<BaseNotification> getEmailNotification(User base) {
        Long lowerbound = 0l;
        if (base != null) {
            lowerbound = base.getId();
        }
        final Session cs = HSF.get().getCurrentSession();
        Query findNextUser = cs.getNamedQuery("findNextUserToNotify");
        findNextUser.setParameter("user_id", lowerbound);
        findNextUser.setMaxResults(1);
        Object nextUser = findNextUser.uniqueResult();
        if (nextUser == null) {
            return null;
        }
        Query emailNotif = cs.getNamedQuery("getEmailNotification");
        emailNotif.setParameter("user_id", nextUser);
        List<BaseNotification> list = (List<BaseNotification>) emailNotif.list();
        return list;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date sendDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Index(name = "notification.createDate")
    protected Date creationDate = new Date();
    @org.hibernate.annotations.Index(name = "notification.seen")
    protected Boolean seen = false;
    protected Boolean displayInHeader = true;
    protected Boolean displayInUpdateList = false;
    /**
     * do i should send Mail
     */
    protected Boolean sendMail;
    /**
     * is email sent?
     */
    protected Boolean notifiedWithEmail = false;
    @ManyToOne
    protected User owner;

    public BaseNotification(User owner) {
        seen = false;
        sendDate = new Date();
        this.owner = owner;
    }

    public BaseNotification() {
    }

    public Boolean getSendMail() {
        return sendMail;
    }

    public void setSendMail(Boolean sendMail) {
        this.sendMail = sendMail;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public User getOwner() {
        return owner;
    }

    public Boolean getDisplayInHeader() {
        return displayInHeader;
    }

    public void setDisplayInHeader(Boolean displayInHeader) {
        this.displayInHeader = displayInHeader;
    }

    public Boolean getNotifiedWithEmail() {
        return notifiedWithEmail;
    }

    public void setNotifiedWithEmail(Boolean notifiedWithEmail) {
        this.notifiedWithEmail = notifiedWithEmail;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Result update() {
        HSF.get().beginTransaction();
        try {
            HSF.get().getCurrentSession().saveOrUpdate(this);
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public boolean create() {
        throw new IllegalAccessError("can not create base notification");
    }

    protected boolean simpleCreate(NotificationSettings.EmailState emailState) {
        if (emailState.equals(NotificationSettings.EmailState.always)) {
            sendEmail();
            this.sendMail = false;
        } else if (emailState.equals(NotificationSettings.EmailState.periodically)) {
            this.sendMail = true;
        } else if (emailState.equals(NotificationSettings.EmailState.never)) {
            this.sendMail = false;
        }
        update();
        return true;
    }

    protected boolean createWithFollowCheck(NotificationSettings.EmailState emailState, boolean dontFalow) {
        if (!dontFalow) {
            return simpleCreate(emailState);
        }
        this.sendMail = false;
        this.seen=true;
        update();
        return true;
    }

    protected boolean sendEmail() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected final boolean sendEmail(IEmailProvider.IEmail mail) {
        return IEmailProvider.get().send(owner.getEmail(), mail);
    }

    public Result markAsRead() {
        try {
            seen = true;
//            HSF.get().beginTransaction();
            update();
//            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public Result markAsUnRead() {
        try {
            HSF.get().beginTransaction();
            seen = false;
            update();
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public static List<BaseNotification> loadByUser(User user, int start, int count) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadByUserOnHeader");
        namedQuery.setParameter("user", user);
        namedQuery.setFirstResult(start);
        namedQuery.setMaxResults(count);
        List<BaseNotification> list = (List<BaseNotification>) namedQuery.list();
        return list;
    }

    public static BaseNotification load(Long id){
        return (BaseNotification) HSF.get().getCurrentSession().load(BaseNotification.class,id);
    }

    public static List<BaseNotification> loadUnreadByUser(User user) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadUnreadByUserOnHeader");
        namedQuery.setParameter("user", user);
        List<BaseNotification> list = (List<BaseNotification>) namedQuery.list();
        return list;
    }

    public static Long getNumberOfUnreadNotifications(User user) {
        Query namedQuery = HSF.get().getCurrentSession().getNamedQuery("getNumberOfUnreadNotifications");
        namedQuery.setParameter("user", user);
        return (Long) namedQuery.uniqueResult();
    }

    public Result markAsSent() {
        this.notifiedWithEmail = true;
        final Session cs = HSF.get().getCurrentSession();
        HSF.get().beginTransaction();
        try {
            cs.saveOrUpdate(this);
            cs.flush();
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BaseNotification)) {
            return false;
        }
        final BaseNotification other = (BaseNotification) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Boolean getDisplayInUpdateList() {
        return displayInUpdateList;
    }

    public void setDisplayInUpdateList(Boolean displayInUpdateList) {
        this.displayInUpdateList = displayInUpdateList;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "BaseNotification{"
                + "creationDate=" + creationDate
                + ", seen=" + seen
                + ", owner=" + owner
                + ", id=" + id
                + '}';
    }

    public static List<BaseNotification> loadByUserInUpdateList(User user, int start, boolean loadSeen, boolean loadSubscribedOnly, int count) {
        Query namedQuery;
        if (loadSubscribedOnly) {
            namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadSubscribedUpdateList");
        } else if (loadSeen) {
            namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadUpdateList");
        } else {
            namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadNewUpdateList");
        }
        namedQuery.setParameter("user", user);
        namedQuery.setFirstResult(start);
        namedQuery.setMaxResults(count);
        List<BaseNotification> list = (List<BaseNotification>) namedQuery.list();
        return list;
    }

    public static List<BaseNotification> loadByUserInUpdateListBefore(User user, BaseNotification before, boolean loadSeen, boolean loadSubscribedOnly, int count) {
        Query namedQuery;
        if (loadSubscribedOnly) {
            namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadSubscribedUpdateListBeforeDate");
        } else if (loadSeen) {
            namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadUpdateListBeforeDate");
        } else {
            namedQuery = HSF.get().getCurrentSession().getNamedQuery("loadNewUpdateListBeforeDate");
        }
        namedQuery.setParameter("user", user);
//        q.setDate("cd",new Date(before.getCreationDate().getTime()+23*3600*1000+1155*1000));
        Date beforeDate = (before == null) ? new Date() : new Date(before.getCreationDate().getTime());
        Long beforeId = (before == null) ? Long.MAX_VALUE : before.getId();
        BaseManager.setDateString(namedQuery, "toDate", beforeDate);
        namedQuery.setLong("beforeId", beforeId);
        namedQuery.setMaxResults(count);
        List<BaseNotification> list = (List<BaseNotification>) namedQuery.list();
        return list;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Boolean isSubscribed() {
        return subscribed;
    }
}
