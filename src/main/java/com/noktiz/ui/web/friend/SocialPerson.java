package com.noktiz.ui.web.friend;

import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
* Created by hasan on 2014-12-20.
*/
public class SocialPerson extends PersonListProvider.IPerson<SocialConnection> {
    private UserFacade owner;
    private SocialConnection facebookFriend;
    String disc = "";
    public SocialPerson(final UserFacade owner, SocialConnection facebookFriend1) {
        this.owner = owner;
        this.facebookFriend = facebookFriend1;
        addAction(new IAction() {
            @Override
            public IModel<String> getActionTitle() {
                if(!facebookFriend.isInvited())
                    return Model.of("invite");
                else
                    return Model.of("invited");
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                Result r = invite();
                r.displayInWicket(caller);
                return true;
            }

            @Override
            public boolean isActionEnabled() {
                return !facebookFriend.isInvited();
            }

            @Override
            public IModel<String> getButtonClass() {
                if(isActionEnabled())
                    return Model.of(" ");
                return Model.of(" blue");
            }

        });

    }

    public Result invite() {
        Result r;
        if(!facebookFriend.isInvited())
            r = owner.inviteSocialConnection(facebookFriend);
        else
            r= new Result(new Result.Message("message.friend_was_invited", Result.Message.Level.warning,facebookFriend.getName()),false);
        return r;
    }

    @Override
    public SocialConnection getObj() {
        return facebookFriend;
    }

    @Override
    public String getImageUrl() {
        return facebookFriend.getPictureUrl();
    }

    @Override
    public IModel<String> getTitle() {
        return Model.of(facebookFriend.getName());
    }

    @Override
    public IModel<String> getDesc() {
        if(facebookFriend.getContext().equals(SocialConnection.Context.Email)){
            return Model.of(facebookFriend.getSid());
        }
        return Model.of(disc);
    }


}
