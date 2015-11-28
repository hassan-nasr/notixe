/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.entity.social;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
import javax.persistence.Entity;

//import com.noktiz.domain.model.SocialConnectionManager;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.domain.system.SystemConfigManager;

/**
 *
 * @author sina
 */
@Entity
public class GoogleSocialConnection extends SocialConnection {

    public GoogleSocialConnection() {
    }

    public GoogleSocialConnection(User user) {
        super(user);
    }

    public GoogleSocialConnection(User user, Context context, String sid, Boolean invited, String name) {
        super(user, context, sid, invited, name);
    }

    public GoogleSocialConnection(User user, Context context, String sid, Boolean invited) {
        super(user, context, sid, invited);
    }

    public GoogleSocialConnection(User user, String sid, Boolean invited) {
        super(user, Context.Email, sid, invited);
    }
    
    @Override
    public Result sendInvite() {

        IEmailProvider.IEmail mail = EmailCreator.createInviteAsTrustedFriend(owner);
        boolean ret = IEmailProvider.get().send(sid, mail);
        if(ret == true){
            return new Result(new Result.Message("We send an Email to "+ sid+ ".he'll be your friend as soon as he Register in "+ SystemConfigManager.getCurrentConfig().getAppName(), Result.Message.Level.info),true);
        }
        return new Result(false);
    }
    
}
