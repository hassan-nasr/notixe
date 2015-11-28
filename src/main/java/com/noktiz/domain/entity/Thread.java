/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.persistance.HSF;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

/**
 *
 * @author hossein
 */
@NamedQueries(value = {
    @NamedQuery(name = "numOfThreads", query = "select m.thread.id,max(m.sendDate) as d from Message m where (m.sender = :suser or (m.reciver = :suser and m.block=null)) and m.draft=false group by m.thread.id "),
    @NamedQuery(name = "loadThreadWithUser", query = "select m.thread,max(m.sendDate) as d from Message m where (m.sender = :suser or (m.reciver = :suser and m.block=null)) and m.draft=false group by m.thread order by d desc"),
    @NamedQuery(name = "loadDraftWithUser", query = " select m.thread,max(m.sendDate) as d from Message m where m.sender = :suser and m.draft=true group by m.thread order by d desc"),
    @NamedQuery(name = "numOfDrafts", query = " select m.thread.id,max(m.sendDate) as d from Message m where m.sender = :suser and m.draft=true group by m.thread.id"),
    @NamedQuery(name = "canStarterSendMoreMessage", query = " from Message m where m.thread= :thread and m.block=null and m.draft=false order by m.sendDate desc"),
    @NamedQuery(name = "unblockAllMessages", query = " Update Message set block=null where thread= :thread and sender=:sender"),
    @NamedQuery(name = "loadThreadWithUsers", query = "from Thread t where t.starter = :suser and t.target = :tuser")
})
@Entity
public class Thread extends BaseObject {

    public static Thread loadThreadWithId(Long id) {
        return (Thread) HSF.get().getCurrentSession().load(Thread.class, id);
    }

    public static long numOfDrafts(User user) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("numOfDrafts");
        query.setParameter("suser", user);
        List result = query.list();
        return (long) result.size();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "text")
    private String title;
    @ManyToOne
    private Block blockedcompletely;
    private boolean draft;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "thread")
    @Where(clause = "deleted='false'")
    List<Message> messages = new ArrayList<>();
    @ManyToOne
    private Block targetBlock;
    @ManyToOne
    private Block targetBlockedBy;
    @ManyToOne
    private Block starterBlock;
    @ManyToOne
    private Block starterBlockedBy;
    @ManyToOne
    User starter;
    @ManyToOne
    User target;
    private Boolean starterVisible = false;
    private Boolean targetVisible = true;

    private boolean targetThanks=false;
    private boolean starterThanks=false;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date targetThanksDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date starterThanksDate;
    private Boolean starterCanBeThanked=Boolean.TRUE;
    private Boolean targetCanBeThanked=Boolean.FALSE;
    public boolean isTargetThanks() {
        return targetThanks;
    }

    public void setTargetThanks(boolean targetThanks) {
        this.targetThanks = targetThanks;
    }

    public Date getTargetThanksDate() {
        return targetThanksDate;
    }

    public void setTargetThanksDate(Date targetThanksDate) {
        this.targetThanksDate = targetThanksDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Block getBlockedcompletely() {
        return blockedcompletely;
    }

    public void setBlockedcompletely(Block Blockedcompletely) {
        this.blockedcompletely = Blockedcompletely;
    }
    /**
     * 
     * @return block object if target is block the starter
     */
    public Block getTargetBlockedBy() {
        return targetBlockedBy;
    }

    public void setTargetBlockedBy(Block targetBlockedBy) {
        this.targetBlockedBy = targetBlockedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = StringUtils.abbreviate(title,255);
    }
    /**
     * @return block object if target is block the starter in this thread
     */
    public Block getTargetBlock() {
        return targetBlock;
    }

    public void setTargetBlock(Block targetBlock) {
        this.targetBlock = targetBlock;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public User getStarter() {
        return starter;
    }

    public void setStarter(User starter) {
        this.starter = starter;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public Boolean isStarterVisible() {
        return starterVisible;
    }

    public void setStarterVisible(Boolean isStarterVisible) {
        this.starterVisible = isStarterVisible;
    }

    public Boolean isTargetVisible() {
        return targetVisible;
    }

    public void setTargetVisible(Boolean isTargetVisible) {
        this.targetVisible = isTargetVisible;
    }

    public void save() {
        final Session cs = HSF.get().getCurrentSession();
        cs.saveOrUpdate(this);
    }

    public static List<Thread> loadThreadWithUser(User user, long begin, long size) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("loadThreadWithUser");
        query.setParameter("suser", user);
        query.setFirstResult((int) begin);
        query.setMaxResults((int) size);
        List<Object[]> list = (List<Object[]>) query.list();
        ArrayList<Thread> ans = new ArrayList<Thread>();
        for (Object[] object : list) {
            ans.add((Thread) object[0]);
        }
        return ans;
    }

    public static List<Thread> loadDraftWithUser(User user, long begin, long size) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("loadDraftWithUser");
        query.setParameter("suser", user);
        query.setFirstResult((int) begin);
        query.setMaxResults((int) size);
        List<Object[]> list = (List<Object[]>) query.list();
        ArrayList<Thread> ans = new ArrayList<Thread>();
        for (Object[] object : list) {
            ans.add((Thread) object[0]);
        }
        return ans;
    }

    public static List<Thread> loadThreadWithUsers(User starter, User target) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("loadThreadWithUsers");
        query.setParameter("suser", starter);
        query.setParameter("tuser", target);
        List<Thread> list = (List<Thread>) query.list();
        return list;
    }

    public static long numOfThreads(User user) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("numOfThreads");
        query.setParameter("suser", user);
        List result = query.list();
        return (long) result.size();

    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }
        if (!(o instanceof Thread)) {
            return false;
        }

        Thread thread = (Thread) o;

        if (id != null ? !id.equals(thread.id) : thread.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Thread{" + "id=" + id + ", title=" + title + ", draft=" + draft + ", starter=" + starter + ", target=" + target + '}';
    }

    

    public void delete() {
        HSF.get().getCurrentSession().delete(this);
    }

    public boolean canStarterSendMoreMessage() {
        Query nq = HSF.get().getCurrentSession().getNamedQuery("canStarterSendMoreMessage");
        nq.setParameter("thread", this);
        nq.setMaxResults(2);
        List<Message> list = nq.list();
        for (Message message : list) {
            if(message.getSender().equals(target)){
                return true;
            }
        }
        return false;
    }

    @OneToOne()
    private Rate relatedRate;

    public Rate getRelatedRate() {
        return relatedRate;
    }

    public void setRelatedRate(Rate relatedRate) {
        this.relatedRate = relatedRate;
    }
    /**
     * 
     * @return block object if starter block target in this thread
     */
    public Block getStarterBlock() {
        return starterBlock;
    }

    public void setStarterBlock(Block starterBlock) {
        this.starterBlock = starterBlock;
    }
    /**
     * 
     * @return block object if starter block target
     */
    public Block getStarterBlockedBy() {
        return starterBlockedBy;
    }

    public void setStarterBlockedBy(Block starterBlockedBy) {
        this.starterBlockedBy = starterBlockedBy;
    }

    public boolean isStarterThanks() {
        return starterThanks;
    }

    public void setStarterThanks(boolean starterThanks) {
        this.starterThanks = starterThanks;
    }

    public boolean canTargetSendMoreMessage() {
        Query nq = HSF.get().getCurrentSession().getNamedQuery("canStarterSendMoreMessage");
        nq.setParameter("thread", this);
        nq.setMaxResults(2);
        List<Message> list = nq.list();
        for (Message message : list) {
            if(message.getSender().equals(starter)){
                return true;
            }
        }
        return false;
    }

    public void unblockAllMessagesFrom(User blocked) {
        Query nq = HSF.get().getCurrentSession().getNamedQuery("unblockAllMessages");
        nq.setParameter("sender",blocked);
        nq.setParameter("thread", this);
        nq.executeUpdate();
    }

    public Date getStarterThanksDate() {
        return starterThanksDate;
    }

    public void setStarterThanksDate(Date starterThanksDate) {
        this.starterThanksDate = starterThanksDate;
    }

    public Boolean isStarterCanBeThanked() {
        return starterCanBeThanked;
    }

    public void setStarterCanBeThanked(Boolean starterCanBeThanked) {
        this.starterCanBeThanked = starterCanBeThanked;
    }

    public Boolean isTargetCanBeThanked() {
        return targetCanBeThanked;
    }

    public void setTargetCanBeThanked(Boolean targetCanBeThanked) {
        this.targetCanBeThanked = targetCanBeThanked;
    }


    public Boolean isUserVisible(User user) {
        if(user.equals(starter))
            return isStarterVisible();
        if(user.equals(target))
            return isTargetVisible();
        return null;
    }
}
