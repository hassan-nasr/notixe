package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.access.AccessLevel;
import com.noktiz.domain.entity.access.BasePrivacy;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.access.PrivacyLevel;

import javax.persistence.*;

/**
 * Created by hasan on 2014-10-07.
 */
@Entity
public class RateOverviewPrivacy extends BasePrivacy {
    Long id;
    private User user;
    private RateContext context;
    private PrivacyLevel privacyLevel=PrivacyLevel.Friends;

    public RateOverviewPrivacy(User user, RateContext context, PrivacyLevel privacyLevel) {
        this.user = user;
        this.context = context;
        this.privacyLevel = privacyLevel;
    }

    public RateOverviewPrivacy() {
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

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    public RateContext getContext() {
        return context;
    }

    public void setContext(RateContext context) {
        this.context = context;
    }
    @Enumerated(EnumType.STRING)
    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    @Override
    public AccessLevel getAccessLevel(User u) {
        return super.getAccessLevelFromPrivacy(user, u, privacyLevel);
    }

    @Override
    public String toString() {
        return "RateOverviewPrivacy{" +
                "id=" + id +
                ", user=" + user +
                ", context=" + context +
                ", privacyLevel=" + privacyLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateOverviewPrivacy)) return false;

        RateOverviewPrivacy that = (RateOverviewPrivacy) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
