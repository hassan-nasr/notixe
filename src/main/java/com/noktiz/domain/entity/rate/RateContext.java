package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.privacy.ComplexPersonList;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hasan on 2014-10-04.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "LoadRateContextsByUserBefore", query = "From RateContext rc where rc.user=:user and rc.creationDate < :creationDate order by creationDate desc "),
        @NamedQuery(name = "LoadRateContextsByUser", query = "From RateContext rc where rc.user=:user  order by creationDate desc "),
        @NamedQuery(name="rateContextWithMaxNextInvitationTime", query = "from RateContext where nextInviteDate < :date and enable = true")
})
public class RateContext extends BaseObject{

    private static final long DAY_IN_MILLISECONDS = 60 * 60 * 24 * 1000;
    private Boolean enable=true;

    @OneToOne(cascade = CascadeType.ALL)
    private ComplexPersonList invitedPersons = new ComplexPersonList();

    @Temporal(TemporalType.TIMESTAMP)

    @Index(name="rateContext.createDate")
    private Date creationDate;
    private Date nextInviteDate;

    @ManyToOne
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private RateState requireRate  = RateState.Optional;
    private Boolean allowRespondMore = false;
    @OneToOne(fetch = FetchType.LAZY)
    private PredefinedQuestion predefinedQuestion;
    public Boolean getAllowRespondMore() {
        if(allowRespondMore == null)
            allowRespondMore = false;
        return allowRespondMore;
    }


    public PredefinedQuestion getPredefinedQuestion() {
        return predefinedQuestion;
    }

    public void setPredefinedQuestion(PredefinedQuestion predefinedQuestion) {
        this.predefinedQuestion = predefinedQuestion;
    }

    public void setAllowRespondMore(Boolean allowRespondMore) {
        this.allowRespondMore = allowRespondMore;
    }

    public RateState getRequireRate() {
        if(requireRate == null){
            requireRate = RateState.Required;
        }
        return requireRate;
    }

    public void setRequireRate(RateState requireRate) {
        this.requireRate = requireRate;
    }

    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    private SimplePeriod timeBetweenRatings = SimplePeriod.SingleTime;
//    private Set<User> hasAccessList = new HashSet<>();
//    private boolean loaded = false;

    /*@PostLoad
    public void postLoad(){
        loaded = true;
        setNextInviteTime();
    }*/

    public RateContext() {
    }

    public RateContext(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
        creationDate = new Date();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = StringUtils.abbreviate(title, 255);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.abbreviate(description, 2000);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateContext)) return false;

        RateContext that = (RateContext) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EndorseContext{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public ComplexPersonList getInvitedPersons() {
        return invitedPersons;
    }

    @Transient
    public Set<User> getInvitedUsers(){
        if(enable == false){
            return new HashSet<>();
        }
        return invitedPersons.getUsers(user);
    }

    public void setInvitedPersons(ComplexPersonList invitedPersons) {
        this.invitedPersons = invitedPersons;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SimplePeriod getTimeBetweenRatings() {
        return timeBetweenRatings;
    }

    public void updateTimeBetweenRatings(SimplePeriod timeBetweenRatings) {
        this.timeBetweenRatings = timeBetweenRatings;
        setNextInviteTime();
    }

    public void setNextInviteTime() {
//        if (!loaded)
//            return;
        if(enable) {
            setNextInviteDate(getReversePeriodStartTime(0));
        }
        else{
            setNextInviteDate(null);
        }
    }

    public Date getNextInviteDate() {
        if(nextInviteDate == null){
            setNextInviteTime();
        }
        return nextInviteDate;
    }

    public void setNextInviteDate(Date nextInviteDate) {
        this.nextInviteDate = nextInviteDate;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
//        setNextInviteTime();
    }

//    @ManyToMany
//    @JoinTable(
//            name="FastAccessList_rateContext",
//            inverseJoinColumns = @JoinColumn( name="user_id"),
//            joinColumns = @JoinColumn( name="rateContextId")
//    )
//    public Set<User> getHasAccessList() {
//        return hasAccessList;
//    }
//
//    public void setHasAccessList(Set<User> hasAccessList) {
//        this.hasAccessList = hasAccessList;
//    }
//
//    void updateAccessList() {
//        Set<User> invitedUsers = getInvitedUsers();
//        List<User> toRemove = new ArrayList<>();
//        for (User hasAccess : getHasAccessList()) {
//            if(!invitedUsers.contains(hasAccess))
//                toRemove.add(hasAccess);
//        }
//        getHasAccessList().removeAll(toRemove);
//        getHasAccessList().addAll(invitedUsers);
//    }

//    public void grantAccessNoCheck(User user){
//        getHasAccessList().add(user);
//    }

    public boolean isInCurrentRange(Rate rate) {
        Long diff =(new Date().getTime()-getCreationDate().getTime())/(1000*60*60*24);
        long firstDayOfPeriod = (diff/getTimeBetweenRatings().getDays())*getTimeBetweenRatings().getDays();
        long now = (rate.getDate().getTime()-getCreationDate().getTime())/(1000*60*60*24);
        if(now  >= firstDayOfPeriod)
            return true;
        return false;

    }

    /**
     *
     * @param num if num =0 returns next invite time otherwise return previous invite times by number for instance 1 means last invite time
     * @return
     */
    public Date getReversePeriodStartTime(Integer num) {
        if(timeBetweenRatings.equals(SimplePeriod.SingleTime)) {
            if(num == 0){
                return new GregorianCalendar(2099,1,1).getTime();
            }
            return getCreationDate();
        }
        else {
            long diff = (new Date().getTime() - creationDate.getTime()) / DAY_IN_MILLISECONDS;
            long next = (diff / timeBetweenRatings.getDays() + 1-num) * timeBetweenRatings.getDays();
            if(next<0)
                next=0;
            Date ret = new Date(creationDate.getTime() + next * DAY_IN_MILLISECONDS);
            return ret;
        }
    }

    public boolean canRateHavingPreviousRating(Rate rate) {
        Date periodStart = getReversePeriodStartTime(1);
        if(rate.getDate().getTime() < periodStart.getTime())
            return true;
        return getAllowRespondMore() && getTimeBetweenRatings().equals(SimplePeriod.SingleTime);
//        return getAllowRespondMore() && (rate.getDate().getTime()-periodStart.getTime())/DAY_IN_MILLISECONDS < ( new Date().getTime()-periodStart.getTime())/DAY_IN_MILLISECONDS;
    }



    public enum  RateState {
        Unavailable,
        Optional,
        Required
    }
}
