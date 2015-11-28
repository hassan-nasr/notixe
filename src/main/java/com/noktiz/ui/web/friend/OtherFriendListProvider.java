/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.friend.iaction.SendMessageAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public abstract class OtherFriendListProvider extends PersonListProvider {

    private UserFacade userFacade;

    public OtherFriendListProvider(UserFacade userFacade) {
        this.userFacade = userFacade;
//        List<Friendship> othersFriends = userFacade.getOthersFriends();
//        addFriends(othersFriends);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public final void addFriends(Collection<Friendship> friendships) {
        for (final Friendship friendship : friendships) {
            final PersonImpl person = new PersonImpl(friendship);
            person.addAction(new SendMessageAction(friendship.getFriendshipOwner()));
//            person.addAction(person.new ShowFriends());
            addPerson(person);
        }
    }

    private class PersonImpl extends IPerson<Friendship> {

        Friendship friendship;

        public Friendship getFriendship() {
            return friendship;
        }

        private PersonImpl(Friendship friendship) {
            this.friendship = friendship;
        }

        @Override
        public Friendship getObj() {
            return friendship;
        }

        @Override
        public String getImageUrl() {
            return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                    ImageManagement.getUserImageParameter(new UserFacade(friendship.getFriendshipOwner()), ImageManagement.ImageSize.medium)).toString();
        }


        @Override
        public String getOverallLink() {
            return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid",friendship.getFriendshipOwner().getId())).toString();
        }

        @Override
        public IModel<String> getTitle() {
            return Model.of(friendship.getFriendshipOwner().getName());
        }

        @Override
        public IModel<String> getSubTitle() {
            final Long friendCount = new UserFacade(friendship.getFriendshipOwner()).getScores().getFriendCount();
            if (friendCount == 0) {
                return new StringResourceModel("NoFriend", null);
            }
            if (friendCount == 1) {
                return new StringResourceModel("1Friend", null);
            }
            return new StringResourceModel("nFriends", null,friendCount);
        }

        @Override
        public IModel<String> getDesc() {
            return Model.of(friendship.getFriendshipOwner().getEmail());
        }

        public class ShowFriends extends IAction {

            @Override
            public IModel<String> getActionTitle() {
                return new StringResourceModel("FriendList", null);
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                showFriendListOf(friendship.getFriendshipOwner(),art,caller);
                return false;
            }

            @Override
            public IModel<String> getButtonClass() {
                return Model.of(" ");
            }
        }

    }

    @Override
    public List getElements(Integer from, Integer count) {
        List<Friendship> friendships = Friendship.loadImFriendsOf(userFacade.getUser(), from, count);
        return addAndRetLoaded(friendships, count);
    }

    private List addAndRetLoaded(List<Friendship> friendships,int count) {
        Integer from =getPersons().size() ;
        hasMore=friendships.size()>=count;
        addFriends(friendships);
        List ret = new ArrayList();
        List<? extends IPerson> persons1 = getPersons();
        for (int i = from; i < from+count && i<getPersons().size(); i++) {
            ret.add(persons1.get(i));
        }
        return ret;
    }

    @Override
    public List<IPerson> getElementsBefore(IPerson From, Integer count) {
        Date startDate = From!=null?((PersonImpl) From).getFriendship().getStartDate():new Date();
        List<Friendship> friendships = Friendship.loadImFriendsOfBefore(userFacade.getUser(), startDate, count);
        return addAndRetLoaded(friendships, count);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    protected abstract void showFriendListOf(User friendshipOwner,AjaxRequestTarget art, Component caller) ;
    
}
