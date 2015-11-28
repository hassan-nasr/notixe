/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import com.noktiz.domain.i18n.Local;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.system.SystemConfigManager;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 *
 * @author Hossein
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "checkActivateExist",query="select count(*) from PersonalInfo as p where p.activate=:activate")
})
public class PersonalInfo extends BaseObject{
    public static String automaticTimezone="automatic";

    @ManyToOne
    private Local local;

    public static boolean checkActivateExist(String activate) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("checkActivateExist");
        query.setString("activate", activate);
        Long count = (Long)query.uniqueResult();
        if(count==0){
            return false;
        }
        return true;
    }
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne()
    private User user;
    private String password;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date joinDate;
    private String activate;
    private String rePassword;
    private String namak;
    private String timezone = "automatic";
    private int wrongLoginAttempt=0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lockUntil;
    
    public PersonalInfo() {
    }

    public int getWrongLoginAttempt() {
        return wrongLoginAttempt;
    }

    public void setWrongLoginAttempt(int wrongLoginAttempt) {
        this.wrongLoginAttempt = wrongLoginAttempt;
    }

    public Date getLockUntil() {
        return lockUntil;
    }

    public void setLockUntil(Date lockUntil) {
        this.lockUntil = lockUntil;
    }

    public PersonalInfo(User user) {
        this.user=user;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }
    
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getNamak() {
        return namak;
    }

    public void setNamak(String namak) {
        this.namak = namak;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    void save() {
        final Session s = HSF.get().getCurrentSession();
        s.saveOrUpdate(this);
        s.flush();
    }

    @Override
    public String toString() {
        return "PersonalInfo{" + "id=" + id + ", user=" + user + ", timezone=" + timezone + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersonalInfo)) {
            return false;
        }
        final PersonalInfo other = (PersonalInfo) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    public boolean addWrongLoginAttempt() {
        wrongLoginAttempt++;
        if(wrongLoginAttempt> SystemConfigManager.getCurrentConfig().getPropertyAsLong("LoginAttempt")){
            GregorianCalendar lockUntil = new GregorianCalendar();
            lockUntil.add(Calendar.MINUTE, SystemConfigManager.getCurrentConfig().getPropertyAsLong("LoginAttemptLockMinute").intValue());
            setLockUntil(lockUntil.getTime());
            wrongLoginAttempt=0;
            return true;
        }
        return false;
    }
    public void resetLoginAttempt() {
        wrongLoginAttempt=0;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Local getLocal() {
        if(local ==null)
            return new Local();
        return local;
    }
}
