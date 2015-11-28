package com.noktiz.domain.entity.token;


import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hasan on 9/12/14.
 */
@Entity
@NamedQueries({
        @NamedQuery(name="loadValidAccessToken", query = "From SingleAccessToken s where s.token = :token and s.expirationDate >= :expDate")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SingleAccessToken extends BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @org.hibernate.annotations.Index(name="accesstoken.token")
    String token;
    Integer used;
    @Enumerated(EnumType.STRING)
    Type type;
    @ManyToOne
    User user;

    @Temporal(TemporalType.TIMESTAMP)
    Date expirationDate=new Date();

    Integer allowedUseTimes=1;

    public SingleAccessToken() {
    }

    public SingleAccessToken(User user, String token, Type type, Integer validMinutes,Integer allowedUseTimes) {
        this.user = user;
        this.token = token;
        this.type = type;
        this.allowedUseTimes = allowedUseTimes;
        expirationDate = new Date(new Date().getTime()+validMinutes*60*1000);
        used=0;
    }
    public SingleAccessToken(User user, String token, Type type, Integer validMinutes){
        this(user, token, type, validMinutes, 5);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleAccessToken)) return false;

        SingleAccessToken that = (SingleAccessToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SingleAccessToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", used=" + used +
                ", action='" + type + '\'' +
                ", user=" + user +
                ", expirationDate=" + expirationDate +
                '}';
    }

    public boolean use() {
        if(used==allowedUseTimes)
            return false;
        used++;
        return true;
    }

    public enum Type {
        Validate, ResetPassword, RateContext
    }
}
