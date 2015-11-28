/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.user.Role;
import com.noktiz.domain.persistance.HSF;

import java.util.*;
import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Where;

/**
 *
 * @author hossein
 */
@NamedQueries({
    @NamedQuery(name = "loadUserWithEmail", query = "From User u where u.email=:email"),
    @NamedQuery(name = "loadAllActiveUsers", query = "From User u where u.active <> false "),
    @NamedQuery(name = "loadActiveUserWithEmail", query = "From User u where u.email=:email and (u.active=true or u.active is null)"),
    @NamedQuery(name = "loadUserWithEmailId", query = "Select c.user From Credential c where c.emailId=:emailId"),
    @NamedQuery(name = "loadActiveUserWithEmailId", query = "Select c.user From Credential c where c.emailId=:emailId and (c.user.active=true or c.user.active is null)"),
    @NamedQuery(name = "loadByActivationCode", query = "From User u where u.personalInfo.activate=:activate"),
    @NamedQuery(name = "loadUserWithFacebookId", query = "From User u where u.credential.facebookInfo.facebook_id=:facebook_id"),
    @NamedQuery(name = "loadUserWithGoogleId", query = "From User u where u.credential.googleInfo.google_id=:google_id"),
    @NamedQuery(name = "findIdWithGoogleId", query = "SELECT id From User u where u.credential.googleInfo.google_id=:google_id"),
    @NamedQuery(name = "findIdWithEmail", query = "SELECT id From User u where u.email=:email"),
    @NamedQuery(name = "findIdWithEmailId", query = "SELECT c.user.id From Credential c where c.emailId=:emailId"),
//    @NamedQuery(name = "loadUserWithCredential", query = "From User u where u.credential.id in ( Select id From Credential c join c.properties p where p.key='facebook_id' and p.value='123123')")
    @NamedQuery(name = "getThanksCount", query = "SELECT count(*),count(distinct target_id) FROM Thread t WHERE (t.starter = :user and t.targetThanks=true and t.targetThanksDate< :date) or (t.target = :user and t.starterThanks=true and t.starterThanksDate< :date)"), 
//    @NamedQuery(name = "loadUserWithCredential", query = "From User u where u.credential.id in ( Select id From Credential c join c.properties p where p.key='facebook_id' and p.value='123123')")
})
@Entity
@Table(name = "Account")
public class User extends BaseObject {

    public static User loadByActivationCode(String code) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("loadByActivationCode");
        query.setString("activate", code);
        List<User> list = (List<User>) query.list();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Logger.getLogger(User.class).error("find more than on records with email :" + code);
        return list.get(0);
    }

    public static Long findIdWithEmail(String email) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("findIdWithEmail");
        query.setString("email", email);
        List<Long> list = (List<Long>) query.list();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Logger.getLogger(User.class).error("find more than on records with email :" + email);
        return list.get(0);
    }
    public static Long findIdWithEmailId(String email) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("findIdWithEmailId");
        query.setString("emailId", email);
        List<Long> list = (List<Long>) query.list();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Logger.getLogger(User.class).error("find more than on records with email :" + email);
        return list.get(0);
    }

    public static Long findIdWithGoogleId(String google_id) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("findIdWithGoogleId");
        query.setString("google_id", google_id);
        List<Long> list = (List<Long>) query.list();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Logger.getLogger(User.class).error("find more than on records with google id :" + google_id);
        return list.get(0);
    }

    public static User loadUserWithGoogleId(String id) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("loadUserWithGoogleId");
        query.setString("google_id", id);
        List<User> list = (List<User>) query.list();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Logger.getLogger(User.class).error("find more than on records with google id :" + id);
        return list.get(0);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Index(name = "user.email")
    private String email;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender= Gender.Unknown;
    private String pictureId;
    private Boolean active;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "user",optional = false)
    private PersonalInfo personalInfo = new PersonalInfo();

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Credential credential = new Credential(this);

    @Embedded
    private Scores scores = new Scores();

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date birthdate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "friendshipOwner")
    @Where(clause = "deleted='false'")
    private List<Friendship> friends = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "friend")
    @Where(clause = "deleted='false'")
    private List<Friendship> othersFriends = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "blocker")
    @Where(clause = "deleted='false'")
    private List<Block> blocks = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "blocked")
    @Where(clause = "deleted='false'")
    private List<Block> blocked = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @Where(clause = "deleted='false'")
    private Set<SocialConnection> socialConnections = new HashSet<>();
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private NotificationSettings notificationSettings;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles= new HashSet<>();

    public Set<SocialConnection> getSocialConnections() {
        return socialConnections;
    }

    public void setSocialConnections(Set<SocialConnection> socialConnections) {
        this.socialConnections = socialConnections;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        if(active == null)
            active=false;
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Scores getScores() {
        if (scores == null) {
            scores = new Scores();
        }
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Friendship> getFriends() {
        return friends;
    }

    public void setFriends(List<Friendship> friends) {
        this.friends = friends;
    }

    public List<Friendship> getOthersFriends() {
        return othersFriends;
    }

    public void setOthersFriends(List<Friendship> othersFriends) {
        this.othersFriends = othersFriends;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public List<Block> getBlocked() {
        return blocked;
    }

    public void setBlocked(List<Block> blocked) {
        this.blocked = blocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = StringUtils.abbreviate(firstName,255);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = StringUtils.abbreviate(lastName, 255);
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public PersonalInfo getPersonalInfo() {
        if(personalInfo==null)
            personalInfo=new PersonalInfo();
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getName() {
        if (lastName.startsWith("@"))
            return firstName+lastName;
        return firstName + " " + lastName;
    }

    public void save() {
        try {
            final Session s = HSF.get().getCurrentSession();
            s.saveOrUpdate(credential);
            s.saveOrUpdate(this);
            getPersonalInfo().save();

            s.flush();
        } catch (Exception t) {
            Logger.getLogger(this.getClass()).error("error in save User email=" + getEmail(), t);
        }
    }

    public static User loadUserWithEmail(String email, boolean checkActivate) {
        Session s = HSF.get().getCurrentSession();
        Query query;
        if (checkActivate) {
            query = s.getNamedQuery("loadActiveUserWithEmail");
        }
        else{
            query = s.getNamedQuery("loadUserWithEmail");
        }
        query.setString("email", email);
        List<User> result = (List<User>) query.list();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            Logger.getLogger(User.class).error("find more than on records with email :" + email);
            return result.get(0);
        }
    }

    /**
     * load user with its emailId, emailId is unique for any verity of emails that a person can use, for example gmail emails with + or .
     * if email of a user is some.one+somthing@gmail.com emailId should be someone@gmail.com
     * @param emailId
     * @param checkActivate
     * @return
     */
    public static User loadUserWithEmailId(String emailId, boolean checkActivate) {
        emailId=EmailAddressUtils.normalizeEmail(emailId);
        Session s = HSF.get().getCurrentSession();
        Query query;
        if (checkActivate) {
            query = s.getNamedQuery("loadActiveUserWithEmailId");
        }
        else{
            query = s.getNamedQuery("loadUserWithEmailId");
        }
        query.setString("emailId", emailId);
        List<User> result = (List<User>) query.list();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            Logger.getLogger(User.class).error("find more than on records with emailId :" + emailId);
            return result.get(0);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + email + ") id= "+id;
    }

    public Credential getCredential() {
        if (credential == null) {
            credential = new Credential(this);
        }
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public static User loadUserByFacebookId(String value) {
        Session s = HSF.get().getCurrentSession();
        Query query = s.getNamedQuery("loadUserWithFacebookId");
        query.setString("facebook_id", value);
        List<User> result = (List<User>) query.list();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            Logger.getLogger(User.class).error("find more than on records with facebookId :" + value);
            return result.get(0);
        }
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public void delete() {
        Session cs = HSF.get().getCurrentSession();
        cs.delete(this);
        cs.flush();
    }

    public static User newGuestUser() {
        User ret = new User();
        ret.setFirstName("Guest");
        ret.setActive(false);
        ret.setGender(Gender.Unknown);
        return ret;
    }

    public boolean hasEmail(String s) {
        s=EmailAddressUtils.normalizeEmail(s);
        if(EmailAddressUtils.normalizeEmail(getEmail()).equals(s))
            return true;
        if(getCredential().getGoogleInfo()!=null && s.equals(EmailAddressUtils.normalizeEmail(getCredential().getGoogleInfo().getPrimary_email()))){
            return true;
        }
        return false;
    }

    public enum Gender {
        male("hi","him"), female("she","her") , Unknown("hi/she","him/her") , Group("they","them");
        private final String subject;
        private final String object;

        Gender(String subject, String object) {
            this.subject = subject;
            this.object = object;
        }

        public String getSubject() {
            return subject;
        }

        public String getObject() {
            return object;
        }
    }
    final static int hour = 1000 * 60 * 60;

    /**
     *
     * @return array which first element is number of user thanks and second
     * element is number of user thankers
     */
    public Long[] getThanksCount(boolean exact) {
        Date date;
        if (exact) {
            date = new Date();
        } else {
            date = new Date((long) (System.currentTimeMillis() - hour * 12 - 12 * Math.random() * hour));
        }
        Query nq = HSF.get().getCurrentSession().getNamedQuery("getThanksCount");
        nq.setParameter("user", this);
        nq.setParameter("date", date);
        List<Object[]> list = (List<Object[]>) nq.list();
        Long[] ans = new Long[2];
        long temp = (Long) list.get(0)[0];
        ans[0] = temp;
        temp = (Long) list.get(0)[1];
        ans[1] = temp;
        if(!exact){
            ans[0]=ans[0]/5*5;
            ans[1]=ans[1]/5*5;
        }
        return ans;
    }

//    @OneToOne(mappedBy = "user")
//    MyEndorsements endorsements;
//
//    public MyEndorsements getEndorsements() {
//        return endorsements;
//    }
//
//    public void setEndorsements(MyEndorsements endorsements) {
//        this.endorsements = endorsements;
//    }
//


    public static User load(Long id) {
        return (User) HSF.get().getCurrentSession().get(User.class, id);
    }


    /*@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @MapKey(name = "context")
    private Map<RateContext,RateOverviewPrivacy> rateOverviewPrivacyListInternal;

    public Map<RateContext,RateOverviewPrivacy> getRateOverviewPrivacyListInternal() {
        return rateOverviewPrivacyListInternal;
    }

    public void setRateOverviewPrivacyListInternal(Map<RateContext,RateOverviewPrivacy> rateOverviewPrivacyListInternal) {
        this.rateOverviewPrivacyListInternal = rateOverviewPrivacyListInternal;
    }

    @Transient
    public RateOverviewPrivacy getRateOverviewPrivacy(RateContext context){
        RateOverviewPrivacy rateOverviewPrivacy = rateOverviewPrivacyListInternal.get(context);
        if (rateOverviewPrivacy == null){
            rateOverviewPrivacy = new RateOverviewPrivacy(this,context, PrivacyLevel.Friends);
            this.rateOverviewPrivacyListInternal.put(context,rateOverviewPrivacy);
        }
        return rateOverviewPrivacy;
    }

    public void setRateOverviewPrivacy(RateContext context,RateOverviewPrivacy privacy){
        rateOverviewPrivacyListInternal.put(context,privacy);
    }
    */

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> approvedEndorseContexts = new TreeSet<>();


    public Set<String> getApprovedEndorseContexts() {
        return approvedEndorseContexts;
    }

    public void setApprovedEndorseContexts(Set<String> approvedEndorseContexts) {
        this.approvedEndorseContexts = approvedEndorseContexts;
    }
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> removedEndorseContexts = new TreeSet<>();

    public Set<String> getRemovedEndorseContexts() {
        return removedEndorseContexts;
    }

    public void setRemovedEndorseContexts(Set<String> removedEndorseContexts) {
        this.removedEndorseContexts = removedEndorseContexts;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user")
    @OrderBy(value = "creationDate desc")
    @Where(clause = "deleted='false'")
    private List<RateContext> rateContexts = new ArrayList<>();

    public List<RateContext> getRateContexts() {
        return rateContexts;
    }

    public void setRateContexts(List<RateContext> rateContexts) {
        this.rateContexts = rateContexts;
    }
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
//    private FastAccessLists fastAccessLists = new FastAccessLists();
//
//    public FastAccessLists getFastAccessLists() {
//        return fastAccessLists;
//    }
//
//    public void setFastAccessLists(FastAccessLists fastAccessLists) {
//        this.fastAccessLists = fastAccessLists;
//    }


//    @OneToMany(mappedBy = "user")
//    @Where(clause = "deleted = 'false'")
//    private Set<Subscription> subscriptions = new HashSet<>();
//
//    public Set<Subscription> getSubscriptions() {
//        return subscriptions;
//    }
//
//    public void setSubscriptions(Set<Subscription> subscriptions) {
//        this.subscriptions = subscriptions;
//    }

    public String getProperty(String property){
        org.apache.wicket.Session session = org.apache.wicket.Session.get();
        HashMap<String, String> properties = session.getMetaData(userPropertiesKey);
        if(properties==null){
            properties = new HashMap<>();
            session.setMetaData(userPropertiesKey, properties);
        }
        if(properties.containsKey(property)){
            return properties.get(property);
        }
        else{
            String value = UserProperties.getValue(this, property);
            properties.put(property,value);
            return value;
        }
    }
    public void setProperty(String property,String value){
        org.apache.wicket.Session session = org.apache.wicket.Session.get();
        HashMap<String, String> properties = session.getMetaData(userPropertiesKey);
        if(properties==null){
            properties = new HashMap<>();
            session.setMetaData(userPropertiesKey, properties);
        }
        if(value!=null && value.equals(properties.get(property))){
            return;
        }
        else{
            UserProperties.setValue(this,property,value);
            properties.put(property,value);
        }
    }
        private static MetaDataKey<HashMap<String,String>> userPropertiesKey = new MetaDataKey<HashMap<String, String>>() {
    };
}
