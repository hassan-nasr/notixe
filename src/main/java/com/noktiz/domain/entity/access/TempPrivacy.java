package com.noktiz.domain.entity.access;

import com.noktiz.domain.entity.User;

/**
 * Created by hasan on 2014-10-13.
 */
public class TempPrivacy extends BasePrivacy {


    private User user;
    private PrivacyLevel level;

    public TempPrivacy(User user, PrivacyLevel level) {
        this.user = user;
        this.level = level;
    }

    @Override
    public AccessLevel getAccessLevel(User u) {
            return super.getAccessLevelFromPrivacy(user, u, level);
    }

    @Override
    public PrivacyLevel getPrivacyLevel() {
        return level;
    }

    public void setPrivacyLevel(PrivacyLevel level) {
        this.level = level;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
