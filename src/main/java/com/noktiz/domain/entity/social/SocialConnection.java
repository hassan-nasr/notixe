package com.noktiz.domain.entity.social;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Created by hasan on 7/18/14.
 */


@Entity
@NamedQueries({
        @NamedQuery(name = "requestedSocialConnections", query = " From SocialConnection s where s.context=:context and s.invited = true and s.sid in (:sid) "),
//        @NamedQuery(name = "SocialConnectionsToSuggest", query = " From SocialConnection s where s.user = :user "),
        @NamedQuery(name = "SocialConnectionsToSuggest", query = " From SocialConnection s where s.owner = :user and s.invited = false and s.registered=true and s.dismissed !=true "),
        @NamedQuery(name = "MySocialConnections", query = " From SocialConnection s where s.owner = :user and s.context=:context"),
        @NamedQuery(name = "SocialConnectionsBySid", query = " From SocialConnection s where s.sid = :sid and s.context=:context"),
    }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SocialConnection extends BaseObject<SocialConnection> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    protected User owner;

    protected Context context;
    @Index(name="socialConnection.sid")
    protected String sid;
    protected boolean invited=false;
    protected String name;
    @Transient
    protected String pictureUrl;
    @Transient
    protected boolean persistable;
//    @Transient
    protected boolean registered;
//    @Transient
    @ManyToOne(fetch = FetchType.LAZY)
    protected User registeredUser;

    boolean dismissed = false;

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    public User getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(User registeredUser) {
        if(registeredUser!=null)
            registered=true;
        this.registeredUser = registeredUser;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean isPersistable() {
        return persistable;
    }

    public void setPersistable(boolean persistable) {
        this.persistable = persistable;
    }



    public SocialConnection() {
    }

    public SocialConnection(User owner) {
        this.owner = owner;
    }

    public SocialConnection(User owner, Context context, String sid, Boolean invited, String name) {
        this.owner = owner;
        this.context = context;
        this.sid = sid;
        this.invited = invited;
        this.name = name;
    }

    public SocialConnection(User owner, Context context, String sid, Boolean invited) {
        this.owner = owner;
        this.context = context;
        this.sid = sid;
        this.invited = invited;
        this.name=this.sid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getSid() {
        return sid;
    }


    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o == null) {
            return false;
        }
        if (!(o instanceof SocialConnection)) {
            return false;
        }

        SocialConnection that = (SocialConnection) o;

        if (context != null ? !context.equals(that.context) : that.context != null) return false;
        if (sid != null ? !sid.equals(that.sid) : that.sid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = context != null ? context.hashCode() : 0;
        result = 31 * result + (sid != null ? sid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SocialConnection{" +
                "sid='" + sid + '\'' +
                ", invited=" + invited +
                ", context=" + context +
                ", user=" + owner +
                ", id=" + id +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public enum Context{
        Facebook,
        Email,
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }


    public Result sendInvite(){
        throw new RuntimeException("invite no implemented");
    };
}
