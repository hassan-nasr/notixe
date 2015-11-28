/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity.notifications;

import com.noktiz.domain.entity.NotificationSettings;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.domain.persistance.HSF;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.hibernate.Query;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

/**
 *
 * @author Hossein
 */
@Entity
@NamedQueries(
        @NamedQuery(name = "loadByUserAndFriend", query = "From NotificationAddFriend as naf where naf.owner=:owner and friend= :friend and seen=false"))
public class NotificationAddFriend extends BaseNotification {

    public static NotificationAddFriend loadByUserAndFriend(User me, User friend) {
        Query nq = HSF.get().getCurrentSession().getNamedQuery("loadByUserAndFriend");
        nq.setMaxResults(1);
        nq.setParameter("owner", me);
        nq.setParameter("friend", friend);
        NotificationAddFriend uniqueResult = (NotificationAddFriend) nq.uniqueResult();
        return uniqueResult;

    }
    @ManyToOne
    private User friend;

    public NotificationAddFriend() {
    }

    public NotificationAddFriend(User friend, User notificationOwner) {
        super(notificationOwner);
        this.friend = friend;
        displayInHeader=true;
        displayInUpdateList=false;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    @Override
    public boolean create() {
        return simpleCreate(owner.getNotificationSettings().getFriendAddYouNotification());
    }



    protected boolean sendEmail() {
        IEmailProvider.IEmail email = EmailCreator.getEmailCreator().createAddFriendEmail(this);
        return sendEmail(email);
    }
}
