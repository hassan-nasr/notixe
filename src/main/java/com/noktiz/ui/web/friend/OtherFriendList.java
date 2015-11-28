/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author Hossein
 */
public final class OtherFriendList extends Panel {

    PersonList friendList;
    final OtherFriendListProvider otherFriendListProvider;
    PersonListProvider select;
    public OtherFriendList(String id, final UserFacade me) {
        super(id);
        otherFriendListProvider = new OtherFriendListProvider(me) {

            @Override
            protected void showFriendListOf(User friendshipOwner,AjaxRequestTarget art, Component caller) {
                select = new AnOtherFriendListProvider(new UserFacade(friendshipOwner), me) {
                    @Override
                    protected boolean onBack(AjaxRequestTarget art, Component caller) {
                        select= otherFriendListProvider;
                        friendList.refresh(art,true);
                        return true;
                    }
                };
                friendList.refresh(art,true);
            }
        };
        select=otherFriendListProvider;
        friendList = new PersonList("friendList", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return select;
            }
        }, null, 30, null, true);
        add(friendList);
    }
}
