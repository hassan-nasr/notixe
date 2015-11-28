package com.noktiz.domain.entity;

import com.noktiz.domain.persistance.HSF;
import org.apache.log4j.Logger;
import org.hibernate.*;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Hossein on 12/30/2014.
 */
@Entity
@NamedQueries({
        @NamedQuery(name="UserProperties_getValue",query="select value From UserProperties where user=:user and property=:property order by id desc"),
        @NamedQuery(name="UserProperties_load",query="From UserProperties where user=:user and property=:property order by id desc")
})
public class UserProperties extends BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    User user;
    String property;
    String value;
    public static void setValue(User user,String property,String value){

        UserProperties oldVal = loadUserProperties(user, property);
        if(value==null && oldVal==null || value!=null && oldVal!=null && value.equals(oldVal.getValue())){
            return;
        }
        if(oldVal==null){
            oldVal= new UserProperties();
        }
        oldVal.setValue(value);
        oldVal.setProperty(property);
        oldVal.setUser(user);
        HSF.get().beginTransaction();
        try{
            HSF.get().getCurrentSession().saveOrUpdate(oldVal);
            HSF.get().commitTransaction();
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(UserProperties.class).info("HibernateException", ex);
        }
    }
    public static String getValue(User user,String property){
        org.hibernate.Query query = HSF.get().getCurrentSession().getNamedQuery("UserProperties_getValue");
        query.setParameter("user",user);
        query.setParameter("property",property);
        query.setMaxResults(1);
        String ans = (String) query.uniqueResult();
        return ans;

    }
    private static UserProperties loadUserProperties(User user,String property){
        org.hibernate.Query query = HSF.get().getCurrentSession().getNamedQuery("UserProperties_load");
        query.setParameter("user",user);
        query.setParameter("property",property);
        query.setMaxResults(1);
        UserProperties userProperties = (UserProperties) query.uniqueResult();
        return userProperties;
    }

    private UserProperties(User user, String property, String value) {
        this.user = user;
        this.property = property;
        this.value = value;

    }

    public UserProperties() {
    }
    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserProperties)) {
            return false;
        }
        final UserProperties other = (UserProperties) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
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
        return "UserProperties{" +
                "id=" + id +
                ", user=" + user +
                ", property='" + property + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static String INTRODUCTION_SEEN="INTRODUCTION_SEEN";
    public static String AnonymousConversation_INTRODUCTION_SEEN="AN_CON_INTRODUCTION_SEEN";
    public static String RATE_INTRODUCTION_SEEN="RATE_INTRODUCTION_SEEN";
    public static String FRIEND_INTRODUCTION_SEEN="FRIEND_INTRODUCTION_SEEN";
    public static String PROFILE_INTRODUCTION_SEEN="PROFILE_INTRODUCTION_SEEN";
    public static String QUESTION_INTRODUCTION_SEEN="QUEST_INTRODUCTION_SEEN";
    public static String NotificationRateInvite_INTRODUCTION_SEEN = "NRI_INTRODUCTION_SEEN";
    public static String TRUE ="TRUE";
    public static String FALSE ="FALSE";
    public static final String LastGoogleFriendCheck= "LastGoogleFriendCheck";
    public static final String LastFacebookFriendCheck= "LastFacebookFriendCheck";
}
