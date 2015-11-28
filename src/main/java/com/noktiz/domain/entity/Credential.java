package com.noktiz.domain.entity;

import com.noktiz.domain.entity.cred.FacebookInfo;
import com.noktiz.domain.entity.cred.GoogleInfo;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasan on 7/18/14.
 */
@Entity
public class Credential extends BaseObject{

    @OneToOne()
    private User user;

//    @ElementCollection
//    @MapKeyColumn(name="prop_name")
//    @Column(name="prop_value")
//    private Map<String,String> properties = new HashMap<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    FacebookInfo facebookInfo;
    
    @Embedded
    GoogleInfo googleInfo;

    @org.hibernate.annotations.Index(name = "user.emailId")
    private String emailId;

    public Credential() {
    }

    public Credential(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    public Map<String, String> getProperties() {
//        return properties;
//    }

//    public void setProperties(Map<String, String> properties) {
//        this.properties = properties;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "user=" + user +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o == null) {
            return false;
        }
        if (!(o instanceof Credential)) {
            return false;
        }

        Credential that = (Credential) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public FacebookInfo getFacebookInfo() {
        if(facebookInfo==null)
            facebookInfo = new FacebookInfo();
        return facebookInfo;
    }

    public void setFacebookInfo(FacebookInfo facebookInfo) {
        this.facebookInfo = facebookInfo;
    }
    
    public GoogleInfo getGoogleInfo() {
        if(googleInfo==null){
            googleInfo = new GoogleInfo();
        }
        return googleInfo;
    }

    public void setGoogleInfo(GoogleInfo googleInfo) {
        this.googleInfo = googleInfo;
    }
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
