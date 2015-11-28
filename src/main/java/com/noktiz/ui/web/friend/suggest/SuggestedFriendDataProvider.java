package com.noktiz.ui.web.friend.suggest;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.friend.SocialPerson;
import com.noktiz.ui.web.friend.Tile;
import com.noktiz.ui.web.friend.fb.SocialFriendListViewer;
import com.noktiz.ui.web.friend.fb.SocialRegisteredPerson;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by hasan on 2014-12-19.
 */
public class SuggestedFriendDataProvider extends ListDataProvider<SocialConnection> {
    SocialConnectionManager socialConnectionManager = new SocialConnectionManager();
    UserFacade user;
    boolean hasMore = true;
    public SuggestedFriendDataProvider(UserFacade user) {
        super(true);
        this.user = user;
    }
    @Override
    public List getElements(Integer From, Integer count) {
        List<SocialConnection> ret = socialConnectionManager.getSuggestedFriends(user.getUser(), From, count);
        hasMore = ret.size()>=count;
        return ret;
    }

    @Override
    public BasePanel getPanel(String name, SocialConnection obj) {
        PersonListProvider.IPerson person;
        if(obj.isRegistered()) {
            person = new SocialRegisteredPerson(user, obj);
        }
        else
            person = new SocialPerson(user,obj);
        person.setTileGroupClass(" friendSuggest ");
        return new Tile(name, person, false);
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

}
