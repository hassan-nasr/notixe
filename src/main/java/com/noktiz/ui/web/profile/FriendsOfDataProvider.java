package com.noktiz.ui.web.profile;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
import com.noktiz.ui.web.user.SingleUserImageWithLinkPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hasan on 2014-12-07.
 */
public class FriendsOfDataProvider extends ListDataProvider<Friendship> {
    UserFacade userFacade;
    boolean hasMore = true;
    public FriendsOfDataProvider(UserFacade userFacade) {
        super(false);
        this.userFacade = userFacade;
    }

    @Override
    public List getElements(Integer From, Integer count) {

        List<Friendship> friendships = Friendship.loadImFriendsOf(userFacade.getUser(), From, count);

        hasMore = count>=friendships.size();
        return friendships;
    }

    @Override
    public BasePanel getPanel(String name, Friendship obj) {
        return new SingleUserImageWithLinkPanel(name,new UserFacade(obj.getFriendshipOwner()),50);
    }

    @Override
    public List<Friendship> getElementsBefore(Friendship From, Integer count) {
        Date startDate = From!=null?(From).getStartDate():new Date();
        List<Friendship> friendships = Friendship.loadImFriendsOfBefore(userFacade.getUser(), startDate, count);
        hasMore = friendships.size()>=count;
        return friendships;
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

}
