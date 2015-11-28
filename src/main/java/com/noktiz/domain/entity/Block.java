/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author hossein
 */
@NamedQueries({
    @NamedQuery(name = "Block_loadBlocked", query = "From Block where blocker = :blocker and blocked = :blocked"),
    @NamedQuery(name = "Block_BlockByAll1", query = "UPDATE Thread t SET t.targetBlockedBy=:block"
            + " where t.starter = :blocked and t.target= :blocker and t.starterVisible = false"),
    @NamedQuery(name = "Block_BlockByAll2", query = "UPDATE Thread t SET t.starterBlockedBy=:block"
            + " where t.target = :blocked and t.starter= :blocker and t.targetVisible = false"),
    @NamedQuery(name = "Block_updateState_unblockBy1", query = "UPDATE Thread t SET t.starterBlockedBy=null"
            + " where t.starterBlockedBy= :block"),
    @NamedQuery(name = "Block_updateState_unblockBy2", query = "UPDATE Thread t SET t.targetBlockedBy=null"
            + " where t.targetBlockedBy= :block"),
    @NamedQuery(name = "Block_updateState_unblockByMessages", query = "UPDATE Message m SET m.block = null where m.block = :block"),
    @NamedQuery(name = "Block_updateState_unblockByCompletely", query = "UPDATE Thread t SET t.blockedcompletely = NULL where t.blockedcompletely = :block"),
    @NamedQuery(name = "Block_updateState_search", query="Select Count(*) From Thread as t where t.starterBlock = :block or t.targetBlock = :block")
//   , @NamedQuery(name = "Block_isBlockedWithThread",query = "Select Block From Block as b , Thread as t where b.blocker = :blocker and b.blocked = :blocked and t.id = :threadId and b.")
})
@Entity
public class Block extends BaseObject {

    public static Block loadBlockedById(long id) {
        Session cs = HSF.get().getCurrentSession();
        return (Block) cs.load(Block.class, id);
    }

    public static void BlockByAll(UserFacade blocker, User blocked, Block block) {
        Query nq = HSF.get().getCurrentSession().getNamedQuery("Block_BlockByAll1");
        nq.setParameter("block", block);
        nq.setParameter("blocker", blocker.getUser());
        nq.setParameter("blocked", blocked);
        nq.executeUpdate();
        nq = HSF.get().getCurrentSession().getNamedQuery("Block_BlockByAll2");
        nq.setParameter("block", block);
        nq.setParameter("blocker", blocker.getUser());
        nq.setParameter("blocked", blocked);
        nq.executeUpdate();
        
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDate;
    @ManyToOne
    private User blocker;
    @ManyToOne
    private User blocked;
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public User getBlocker() {
        return blocker;
    }

    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }

    public User getBlocked() {
        return blocked;
    }

    public void setBlocked(User blocked) {
        this.blocked = blocked;
    }

    public static Block loadBlocked(User sender, User receiver) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Block_loadBlocked");
        query.setParameter("blocker", receiver);
        query.setParameter("blocked", sender);
        Block load = (Block) query.uniqueResult();
        return load;
    }

    public static boolean isBlockedWithThread(User sender, User receiver, Thread thread) {
        Session cs = HSF.get().getCurrentSession();
        Query query = cs.getNamedQuery("Block_isBlockedWithThread");
        query.setParameter("blocker", receiver);
        query.setParameter("blocked", sender);
        query.setParameter("threadId", thread.getId());
        List list = query.list();
        return !list.isEmpty();
    }

    public static boolean Block(User blocker, User blocked, Thread thread) {
        if (isBlockedWithThread(blocked, blocker, thread)) {
            return true;
        }
        Block block = new Block();
        block.setBlocked(blocked);
        block.setBlocker(blocker);
        block.setStartDate(new Date());
        Session cs = HSF.get().getCurrentSession();
        cs.save(block);
        cs.flush();
        return true;
    }

    public void save() {
        Session cs = HSF.get().getCurrentSession();
        cs.saveOrUpdate(this);
        cs.flush();
    }

    public void delete() {
        Session cs = HSF.get().getCurrentSession();
        cs.delete(this);
    }


    @Override
    public String toString() {
        return blocker.toString()+" BLOCKED "+blocked.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!(obj instanceof User)) {
            return false;
        }
        final Block other = (Block) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public void updateState() {
        Query nq = HSF.get().getCurrentSession().getNamedQuery("Block_updateState_search");
        nq.setParameter("block", this);
        Long count =(Long) nq.uniqueResult();
        if(count==0){
            Query nqu = HSF.get().getCurrentSession().getNamedQuery("Block_updateState_unblockBy1");
            nqu.setParameter("block", this);
            nqu.executeUpdate();
            nqu = HSF.get().getCurrentSession().getNamedQuery("Block_updateState_unblockBy2");
            nqu.setParameter("block", this);
            nqu.executeUpdate();
            nqu = HSF.get().getCurrentSession().getNamedQuery("Block_updateState_unblockByMessages");
            nqu.setParameter("block", this);
            nqu.executeUpdate();
            nqu = HSF.get().getCurrentSession().getNamedQuery("Block_updateState_unblockByCompletely");
            nqu.setParameter("block", this);
            nqu.executeUpdate();
            delete();
            
        }
        
    }



}
