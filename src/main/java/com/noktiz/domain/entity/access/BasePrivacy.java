package com.noktiz.domain.entity.access;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;

/**
 * Created by hasan on 2014-10-07.
 */
public abstract class BasePrivacy extends BaseObject<BasePrivacy>{
    public abstract AccessLevel getAccessLevel(User u);
    public abstract PrivacyLevel getPrivacyLevel() ;

    public abstract void setPrivacyLevel(PrivacyLevel privacyLevel) ;

    public AccessLevel getAccessLevelFromPrivacy(User owner, User visitor, PrivacyLevel privacyLevel) {
        if(owner.equals(visitor))
            return AccessLevel.Write;
        if(privacyLevel.equals(PrivacyLevel.Personal))
            return AccessLevel.None;
        if(privacyLevel.equals(PrivacyLevel.Friends)) {
            if(!visitor.getActive())
                return AccessLevel.None;
            return new UserFacade(owner).doIOwnerTheFriendshipOf(new UserFacade(visitor))
                    ? AccessLevel.Read
                    : AccessLevel.None;
        }
        if(privacyLevel.equals(PrivacyLevel.Public))
            return AccessLevel.Read;
        return AccessLevel.None;
    }
}
