package com.noktiz.domain.entity.social;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.social.facebook.FacebookTaggableFriendProvider;

import javax.persistence.Entity;

/**
 * Created by hasan on 9/3/14.
 */
@Entity
public class FacebookSocialConnection extends SocialConnection {
    public FacebookSocialConnection() {
    }

    public FacebookSocialConnection(User user) {
        super(user);
    }

    public FacebookSocialConnection(User user, Context context, String sid, Boolean invited, String name) {
        super(user, context, sid, invited, name);
    }

    public FacebookSocialConnection(User user, Context context, String sid, Boolean invited) {
        super(user, context, sid, invited);
    }

    @Override
    public Result sendInvite() {
        try {
            new FacebookTaggableFriendProvider().inviteUser(getOwner(), this);
            return new Result(true);
        } catch (ResultException e) {
            return e.getResult();
        }
    }
}
