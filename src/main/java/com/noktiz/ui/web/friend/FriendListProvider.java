/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.Scores;
import com.noktiz.domain.model.Result;
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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public class FriendListProvider extends PersonListProvider {

    private UserFacade owner;

    public FriendListProvider(UserFacade owner) {
        this.owner = owner;
//        addFriends(owner.getFriends());
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public IModel<List<IAction>> getActions() {
//        IAction remove = new IAction() {
//            @Override
//            public IModel<String> getActionTitle() {
//                return Model.of("Remove Selected friends");
//            }
//
//            @Override
//            public boolean onAction(AjaxRequestTarget art, Component caller) {
//                List<PersonImpl> persons1 = (List<PersonImpl>) getPersons();
//                int count = 0;
//                for (PersonImpl personImpl : persons1) {
//                    if (personImpl.isSelect().getObject()) {
//                        if (personImpl.removed == false) {
//                            Result r = owner.removeFriend(personImpl.friendship);
//                            if (r.result == true) {
//                                personImpl.removed = true;
//                                count++;
//                            }
//                        }
//                    }
//                }
//                caller.info(count + "person removed from your friend list");
//                return true;
//            }
//
//            @Override
//            public IModel<String> getButtonClass() {
//                return Model.of(" red ");
//            }
//        };
        ArrayList<IAction> arrayList = new ArrayList<IAction>();
//        arrayList.add(remove);
        return new Model(arrayList);

    }


    public final void addFriends(Collection<Friendship> friendships) {
        for (final Friendship friendship : friendships) {
            final PersonImpl person = new PersonImpl(friendship);
            person.addAction(person.new RemoveAction());
            if(checkBidirectional(friendship, person)){
                person.addAction(new SendMessageAction(friendship.getFriend()));
            };
            addPerson(person);
        }
    }

//    public final void addFriend(Friendship friendship) {
//        List<? extends IPerson> persons1 = getPersons();
//        boolean found = false;
//        for (IPerson iPerson : persons1) {
//            PersonImpl personImpl = (PersonImpl) iPerson;
//            if (personImpl.getFriendship().getFriend().equals(friendship.getFriend())) {
//                personImpl.removed = false;
//                found = true;
//                personImpl.setFriendship(friendship);
//            }
//        }
//        if (!found) {
//            final PersonImpl person = new PersonImpl(friendship);
//            person.addAction(person.new RemoveAction());
//            if(checkBidirectional(friendship, person)){
//                person.addAction(new SendMessageAction(friendship.getFriend()));
//            };
//            addPerson(person);
//
//        }
//    }

    @Override
    public void refresh() {
        clearPerson();
        addFriends(owner.getFriends());
    }

    private boolean checkBidirectional(final Friendship friendship, final PersonImpl person) {
        boolean doIOwnerTheFriendshipOf = new UserFacade(friendship.getFriend()).doIOwnerTheFriendshipOf(owner);
        if (doIOwnerTheFriendshipOf) {
            person.bidirectional = true;
            return true;
        }
        return false;
    }

    @Override
    public List getElements(Integer from, Integer count) {
        List<Friendship> friendships = Friendship.loadFriends(owner.getUser(), from, count);
        return addAndRetLoaded(friendships, count);
    }

    private List addAndRetLoaded(List<Friendship> friendships, Integer count) {
        int from = getPersons().size();
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
        Date startDate = From!=null?((PersonImpl) From).getFriendship().getStartDate():new Date(System.currentTimeMillis()+1000l);
        List<Friendship> friendships = Friendship.loadFriendOlder(owner.getUser(), startDate, count);
        return addAndRetLoaded(friendships, count);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    private class PersonImpl extends IPerson<Friendship> {

        boolean bidirectional = false;
        private Friendship friendship;
        public boolean removed;


        public PersonImpl(Friendship friendship) {
            this.friendship = friendship;
        }

        public Friendship getFriendship() {
            return friendship;
        }

        public void setFriendship(Friendship friendship) {
            this.friendship = friendship;
        }


        @Override
        public Friendship getObj() {
            return friendship;
        }

        @Override
        public String getImageUrl() {
            return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(new UserFacade(friendship.getFriend()), ImageManagement.ImageSize.medium)).toString();
        }
        @Override
        public String getOverallLink() {
            return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid",friendship.getFriend().getId())).toString();
        }

        @Override
        public IModel<String> getTitle() {
            return Model.of(friendship.getFriend().getName());
        }

        @Override
        public IModel<String> getSubTitle() {
            Scores thanksCount = new UserFacade(friendship.getFriend()).getScores();
            if (thanksCount.getThanksCountNotExact() > 0) {
                return new StringResourceModel("thanksCountMessage",null, new Object[]{thanksCount.getThanksCountNotExact(), thanksCount.getThanksUserNotExact()});
            }
            return Model.of("");
        }

        @Override
        public IModel<String> getDesc() {
            return new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if (bidirectional) {
                        return friendship.getFriend().getEmail();
                    }
                    return "";
                }
            };
        }

        @Override
        public IModel<String> getBGClass() {
            return new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if (removed) {
                        return " bg-red ";
                    } else if(!bidirectional){
                        return " bg-blue ";
                    }
                    else{
                        return " bg-yellow ";
                    }
                }
            };
        }

        public class RemoveAction extends IAction {

            @Override
            public IModel<String> getActionTitle() {
                return new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        if (removed == false) {
                            return new StringResourceModel("remove",null).getObject();
                        } else {
                            return new StringResourceModel("removed", null).getObject();
                        }
                    }
                };
            }

            @Override
            public boolean isActionEnabled() {
                return !removed;
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                Result r = owner.removeFriend(friendship);
                if (r.result == true) {
                    removed = true;
                }
                r.displayInWicket(caller);
                return true;
            }

            @Override
            public IModel<String> getButtonClass() {
                return new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        if (removed) {
                            return " black ";
                        } else {
                            return " black ";
                        }
                    }
                };
            }
        }
    }
}
