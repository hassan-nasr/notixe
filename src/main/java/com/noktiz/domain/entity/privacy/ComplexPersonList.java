package com.noktiz.domain.entity.privacy;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hasan on 2014-11-03.
 */
@Entity
public class ComplexPersonList extends BaseObject{

    Long id;

    boolean includeMyTrustedFriends = true;
    boolean includePeoplesTrustedMe = false;
    Set<User> includeUsers = new HashSet<>();
    Set<User> excludeUsers = new HashSet<>();

    public boolean isIncludeMyTrustedFriends() {
        return includeMyTrustedFriends;
    }

    public void setIncludeMyTrustedFriends(boolean includeMyTrustedFriends) {
        this.includeMyTrustedFriends = includeMyTrustedFriends;
    }

    public boolean isIncludePeoplesTrustedMe() {
        return includePeoplesTrustedMe;
    }

    public void setIncludePeoplesTrustedMe(boolean includePeoplesTrustedMe) {
        this.includePeoplesTrustedMe = includePeoplesTrustedMe;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "complexPersonListInclude")
    public Set<User> getIncludeUsers() {
        return includeUsers;
    }

    public void setIncludeUsers(Set<User> includeUsers) {
        this.includeUsers = includeUsers;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "complexPersonListExclude")
    public Set<User> getExcludeUsers() {
        return excludeUsers;
    }

    public void setExcludeUsers(Set<User> excludeUsers) {
        this.excludeUsers = excludeUsers;
    }



    public void includeUser(User user){
        includeUsers.add(user);
        excludeUsers.remove(user);
    }

    public void dontIncludeUser(User user){
        includeUsers.remove(user);
    }

    public void excludeUser(User user){
        includeUsers.remove(user);
        excludeUsers.add(user);
    }

    public void dontExcludeUser(User user){
        excludeUsers.remove(user);
    }

    public Set<User> getUsers(User baseUser){
        Set<User> ret = new HashSet<>();
        if(includeMyTrustedFriends)
        {
            for (Friendship friendship : baseUser.getFriends()) {
                ret.add(friendship.getFriend());
            }
        }
        if(includePeoplesTrustedMe){
            for (Friendship friendship : baseUser.getOthersFriends()) {
                ret.add(friendship.getFriendshipOwner());
            }
        }
        ret.addAll(includeUsers);
        ret.removeAll(excludeUsers);
        return ret;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexPersonList)) return false;

        ComplexPersonList that = (ComplexPersonList) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ComplexPersonList{" +
                "includePeoplesTrustedMe=" + includePeoplesTrustedMe +
                ", includeMyTrustedFriends=" + includeMyTrustedFriends +
                ", id=" + id +
                '}';
    }
}
