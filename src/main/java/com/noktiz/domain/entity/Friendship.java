/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.persistance.HSF;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Index;

/**
 *
 * @author hossein
 */
@NamedQueries({
    @NamedQuery(name = "Friendship_loadWithUsers", query = "From Friendship as f where f.friendshipOwner = :me and f.friend = :friend  and f.deleted=false"),
    @NamedQuery(name = "Friendship_loadFriends", query = "From Friendship as f where f.friendshipOwner = :me and f.deleted=false"),
    @NamedQuery(name = "Friendship_loadFriendsOlder", query = "From Friendship as f where f.friendshipOwner = :me and f.deleted=false and f.startDate<:toDate order by f.startDate desc "),
    @NamedQuery(name = "Friendship_loadImFriendOf", query = "From Friendship as f where f.friend = :friend and f.deleted=false"),
    @NamedQuery(name = "Friendship_loadImFriendOfOlder", query = "From Friendship as f where f.friend = :friend and f.deleted=false and f.startDate<:toDate order by f.startDate desc"),
    @NamedQuery(name = "Friendship_canStart", query = "from Friendship f where f.friendshipOwner = :receiver and f.friend= :sender and f.deleted=false"),
    @NamedQuery(name = "Friendship_friendsCount", query = "Select Count(*) from Friendship f where f.friendshipOwner = :owner and f.deleted=false"),
    @NamedQuery(name = "Friendship_exists", query = "from Friendship f where f.friendshipOwner = :receiver and f.friend= :sender  and f.deleted=false"),
    @NamedQuery(name = "Friendship_mutualTrust", query = "select count(f.friend.id) from Friendship f where f.friendshipOwner = :user1 and f.deleted=false and f.friend.id in (select f.friend.id from Friendship f where f.friendshipOwner = :user2 and f.deleted=false )")
})
@Entity
public class Friendship extends BaseObject {

    public static Friendship load(User owner, User friend) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_loadWithUsers");
        query.setParameter("me", owner);
        query.setParameter("friend", friend);
        Friendship result = (Friendship) query.uniqueResult();
        return result;
    }

    public static List<Friendship> loadFriends(User owner, int from , int count) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_loadFriends");
        query.setParameter("me", owner);
        query.setMaxResults(count);
        query.setFirstResult(from);
        return query.list();
    }
    public static List<Friendship> loadImFriendsOf(User user, int from , int count) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_loadImFriendOf");
        query.setParameter("friend", user);
        query.setMaxResults(count);
        query.setFirstResult(from);
        return query.list();
    }

    public static Long friendsCount(User owner) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_friendsCount");
        query.setParameter("owner", owner);
        Long result = (Long) query.uniqueResult();
        return result;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Index(name="friendship.startdate")
    private Date startDate;
    @ManyToOne
    private User friendshipOwner;
    @ManyToOne
    private User friend;
    private Boolean dontFollow=false;
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return not null
     */
    public Boolean getDontFollow() {
        if(dontFollow==null){
            return false;
        }
        return dontFollow;
    }

    public void setDontFollow(Boolean dontFollow) {
        if(dontFollow!=null) {
            this.dontFollow = dontFollow;
        }else{
            dontFollow=false;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public User getFriendshipOwner() {
        return friendshipOwner;
    }

    public void setFriendshipOwner(User friendshipOwner) {
        this.friendshipOwner = friendshipOwner;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public boolean save() {

        final Session s = HSF.get().getCurrentSession();
        HSF.get().beginTransaction();
        try {
            s.saveOrUpdate(this);
            s.flush();
            return  true;
        }catch (HibernateException ex){
            Logger.getLogger(Friendship.class).error("can not save",ex);
            HSF.get().roleback();
            throw ex;
        }
        finally {
            HSF.get().commitTransaction();
        }
    }

    public void delete() {
        final Session s = HSF.get().getCurrentSession();
        s.delete(this);
        s.flush();
    }

    public static boolean canStart(User sender, User receiver) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_canStart");
        query.setParameter("receiver", receiver);
        query.setParameter("sender", sender);
        List list = query.list();
        if (list.isEmpty()) {
            return false;
        }
        return true;
    }

    public static Friendship exists(User friend, User owner) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_exists");
        query.setParameter("receiver", owner);
        query.setParameter("sender", friend);
        List list = query.list();
        if (list.isEmpty()) {
            return null;
        }
        return (Friendship) list.get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Friendship)) {
            return false;
        }

        Friendship that = (Friendship) o;

        if (id != that.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Friendship{"
                + "id=" + id
                + ", startDate=" + startDate
                + ", friendshipOwner=" + friendshipOwner
                + ", friend=" + friend
                + '}';
    }

    public static List<Friendship> loadFriendOlder(User owner, Date toDate, Integer count) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_loadFriendsOlder");
        query.setParameter("me", owner);
        BaseManager.setDateString(query,"toDate", toDate);
        query.setMaxResults(count);
        return query.list();
    }

    public static List<Friendship> loadImFriendsOfBefore(User friend, Date toDate, Integer count) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_loadImFriendOfOlder");
        query.setParameter("friend", friend);
        BaseManager.setDateString(query,"toDate", toDate);
        query.setMaxResults(count);
        return query.list();
    }

    public static Long getMutualTrustedFriedsCount(User u1, User u2){
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Friendship_mutualTrust");
        query.setParameter("user1", u1);
        query.setParameter("user2", u2);
        return (Long) query.uniqueResult();
    }
}
