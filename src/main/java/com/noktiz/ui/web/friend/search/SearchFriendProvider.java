package com.noktiz.ui.web.friend.search;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.FriendProvider;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.friend.fb.SocialRegisteredPerson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hassan on 4/20/2015.
 */
public class SearchFriendProvider extends PersonListProvider {
    private final String query;
    private final UserFacade viewer;

    public SearchFriendProvider(String query, UserFacade viewer) {
        this.query = query;
        this.viewer = viewer;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isSearchEnable() {
        return false;
    }

    @Override
    public List<? extends IPerson> getElements(Integer from, Integer count) {
        List<IPerson> ret = new ArrayList<>();
        List<UserFacade> userFacades = UserFacade.loadByQuery(query, from, count);
        for (UserFacade userFacade : userFacades) {
            ret.add(new RegisteredPerson(userFacade,viewer));
        }
        if(userFacades.size()<count)
            hasMore=false;
        return ret;
    }

    @Override
    public boolean isStatic() {
        return true;
    }
}
