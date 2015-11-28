/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend.iaction;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.friend.PersonListProvider;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public class AddFriendAction extends  PersonListProvider.IPerson.IAction {

    private UserFacade friendshipOwner;
    private UserFacade friend;
    boolean enable=true;
    public AddFriendAction(UserFacade friendshipOwner, UserFacade friend) {
        this.friendshipOwner = friendshipOwner;
        this.friend = friend;
    }

    @Override
    public IModel<String> getActionTitle() {
        return new AbstractReadOnlyModel(){

            @Override
            public Object getObject() {
                if(enable){
                    return new StringResourceModel("AddFriend", null).getObject();
                }
                return new StringResourceModel("Added", null).getObject();
            }
            
        };
    }

    @Override
    public boolean onAction(AjaxRequestTarget art, Component caller) {
        ResultWithObject<Friendship> addFriend = friendshipOwner.addFriend(friend);
        addFriend.displayInWicket(caller);
        if(addFriend.result){
            onSuccess(art,caller);
            enable=false;
        }
        return true;
    }

    @Override
    public IModel<String> getButtonClass() {
        return Model.of(" ");
    }

    @Override
    public boolean isActionEnabled() {
        return enable;
    }
    

    protected void onSuccess(AjaxRequestTarget art, Component caller) {
        
    };
    
}