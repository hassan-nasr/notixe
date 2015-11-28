package com.noktiz.ui.web.friend.fb;

import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
* Created by hasan on 2014-12-19.
*/
public class SocialRegisteredPerson extends PersonListProvider.IPerson<SocialConnection> {
    private final UserFacade owner;
    private SocialConnection facebookProfile;
    String disc = "";

    public SocialRegisteredPerson(UserFacade owner1, SocialConnection facebookProfile1) {
        this.owner = owner1;
        this.facebookProfile = facebookProfile1;
        addAction(new IAction() {
            @Override
            public IModel<String> getActionTitle() {
                if(!facebookProfile.isInvited())
                    return Model.of(new StringResourceModel("connect", null).getObject());
                else
                    return Model.of(new StringResourceModel("connected", null).getObject());
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                if(facebookProfile.isInvited())
                    return true;
                Result r = add();
                r.displayInWicket(caller);
                return true;
            }

            @Override
            public boolean isActionEnabled() {
                return !facebookProfile.isInvited();
            }

            @Override
            public IModel<String> getButtonClass() {
                if(isActionEnabled())
                    return Model.of("");
                return Model.of(" blue");
            }
        });
    }

    public Result add() {
        Result r= owner.addFriend(new UserFacade(facebookProfile.getRegisteredUser()));
        if (r.result == true) {
            facebookProfile.setInvited(true);
            ResultWithObject<SocialConnection> mergeResult = new SocialConnectionManager().merge(facebookProfile);
            facebookProfile = mergeResult.object;
            r.addRequired(mergeResult);
        }
        else{
//            if(!r.messages.isEmpty()){
//                facebookProfile.setDismissed(true);
//                new SocialConnectionManager().merge(facebookProfile);
//            }
        }
        return r;

    }

    @Override
    public SocialConnection getObj() {
        return facebookProfile;
    }

    @Override
    public String getImageUrl() {
        String pictureUrl = facebookProfile.getPictureUrl();
        if(pictureUrl == null || pictureUrl.isEmpty()){
            if(facebookProfile.getRegisteredUser()!=null)
                return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),ImageManagement.getUserImageParameter(new UserFacade(facebookProfile.getRegisteredUser()), ImageManagement.ImageSize.small)).toString();
        }
        return pictureUrl;
    }


    @Override
    public String getOverallLink() {
        facebookProfile=facebookProfile.refresh();
        return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid",facebookProfile.getRegisteredUser().getId())).toString();
    }

    @Override
    public IModel<String> getTitle() {
        return Model.of(facebookProfile.getName());
    }

    @Override
    public IModel<String> getDesc() {
        return Model.of(disc);
    }
}
