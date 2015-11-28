package com.noktiz.ui.web.friend.search;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.Result;
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
public class RegisteredPerson extends PersonListProvider.IPerson<UserFacade> {
    private final UserFacade user;
    private final UserFacade viewer;
    boolean inMyFriends;
    boolean imHisFriend;
    String desc = "";

    public RegisteredPerson(UserFacade user, UserFacade viewer) {
        this.user = user;
        this.viewer = viewer;
        inMyFriends =viewer.doIOwnerTheFriendshipOf(user);
        if(!user.equals(viewer))
            addAction(new IAction() {
                @Override
                public IModel<String> getActionTitle() {
                    if(!inMyFriends)
                        return Model.of(new StringResourceModel("connect", null).getObject());
                    else
                        return Model.of(new StringResourceModel("connected", null).getObject());
                }

                @Override
                public boolean onAction(AjaxRequestTarget art, Component caller) {
                    if(inMyFriends)
                        return true;
                    Result r = add();
                    r.displayInWicket(caller);
                    return true;
                }

                @Override
                public boolean isActionEnabled() {
                    return !inMyFriends;
                }

                @Override
                public IModel<String> getButtonClass() {
                    if(isActionEnabled())
                        return Model.of(" ");
                    return Model.of(" blue ");
                }
            });
        if(user.equals(viewer))
            desc = new StringResourceModel("itsYou",null).getString();
        if(user.doIOwnerTheFriendshipOf(viewer))
            if(user.getGender().equals(User.Gender.male))
                desc = new StringResourceModel("youAreHisFriend", null).getObject();
            else if(user.getGender().equals(User.Gender.female))
                desc = new StringResourceModel("youAreHerFriend", null).getObject();
            else
                desc = new StringResourceModel("youAreTheirFriend", null).getObject();
    }

    public Result add() {
        Result r= viewer.addFriend(user);
        if (r.result == true) {
            inMyFriends=true;
        }
        return r;
    }

    @Override
    public UserFacade getObj() {
        return user;
    }

    @Override
    public String getImageUrl() {

        return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),ImageManagement.getUserImageParameter(user, ImageManagement.ImageSize.small)).toString();

    }


    @Override
    public String getOverallLink() {
        return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid",user.getUser().getId())).toString();
    }

    @Override
    public IModel<String> getTitle() {
        return Model.of(user.getName());
    }

    @Override
    public IModel<String> getDesc() {
        return Model.of(desc);
    }
}
