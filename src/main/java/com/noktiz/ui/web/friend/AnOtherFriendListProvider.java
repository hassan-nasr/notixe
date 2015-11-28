/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.friend.iaction.AddFriendAction;
import com.noktiz.ui.web.friend.iaction.SendMessageAction;
import com.noktiz.ui.web.profile.ProfilePage;
import com.noktiz.ui.web.thread.SendMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public abstract class AnOtherFriendListProvider extends PersonListProvider {

    UserFacade user;

    public AnOtherFriendListProvider(UserFacade friendsOf, UserFacade user) {
        this.user = user;
        List<Friendship> othersFriends = friendsOf.getFriends();
        addFriends(othersFriends);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public IModel<List<IAction>> getActions() {
        IAction action = new IAction() {
            @Override
            public IModel<String> getActionTitle() {
                return Model.of("Back");
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                return onBack(art, caller);
            }

            @Override
            public IModel<String> getButtonClass() {
                return Model.of(" ");
            }
        };
        ArrayList<IAction> arrayList = new ArrayList<>();
        arrayList.add(action);
        return new Model(arrayList);
    }

    public final void addFriends(Collection<Friendship> friendships) {
        for (final Friendship friendship : friendships) {
            final PersonImpl person = new PersonImpl(friendship.getFriend());
            final UserFacade secondStepFriend = new UserFacade(friendship.getFriend());
            int stateCounter = 0;
            if (user.canSendMessageTo(secondStepFriend)) {
                stateCounter++;
                person.addAction(new SendMessageAction(friendship.getFriend()));
            }
            if (!user.doIOwnerTheFriendshipOf(secondStepFriend)) {
                person.addAction(new AddFriendAction(user, secondStepFriend) {
                    @Override
                    protected void onSuccess(AjaxRequestTarget art, Component caller) {
                        person.friendshipState = PersonImpl.FriendshipState.newlyAdded;

                    }
                });
            } else {
                stateCounter += 2;
            }
            switch (stateCounter) {
                case 0:
                    person.friendshipState = PersonImpl.FriendshipState.noFriend;
                    break;
                case 1:
                    person.friendshipState = PersonImpl.FriendshipState.hisFriend;
                    break;
                case 2:
                    person.friendshipState = PersonImpl.FriendshipState.myFriend;
                    break;
                case 3:
                    person.friendshipState = PersonImpl.FriendshipState.both;
                    break;
            }
            addPerson(person);
        }

    }

    private static class PersonImpl extends IPerson<User> {

        User user;
        FriendshipState friendshipState;

        public PersonImpl(User user) {
            this.user = user;
        }


        @Override
        public User getObj() {
            return user;
        }

        @Override
        public String getImageUrl() {
            return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                    ImageManagement.getUserImageParameter(new UserFacade(user), ImageManagement.ImageSize.medium)).toString();
        }

        @Override
        public String getOverallLink() {
            return RequestCycle.get().urlFor(ProfilePage.class,new PageParameters().add("userid",user.getId())).toString();
        }



        @Override
        public IModel<String> getTitle() {
            return Model.of(user.getName());
        }


        @Override
        public IModel<String> getDesc() {
            return new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    switch (friendshipState) {
                        case both:
                        case hisFriend:
                            return user.getEmail();
                        default:
                            return "";
                    }
                }
            };
        }

        @Override
        public IModel<String> getBGClass() {
            return new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    switch (friendshipState) {
                        case both:
                            return " bg-yellow ";
                        case hisFriend:
                            return " bg-green ";
                        case myFriend:
                            return " bg-blue ";
                        case noFriend:
                            return " bg-red ";
                        case newlyAdded:
                            return " bg-purple ";
                        default:
                            return " bg-green ";
                    }
                }
            };

        }

        public enum FriendshipState {

            noFriend, myFriend, hisFriend, both, newlyAdded
        }
    }





    protected abstract boolean onBack(AjaxRequestTarget art, Component caller);
}
