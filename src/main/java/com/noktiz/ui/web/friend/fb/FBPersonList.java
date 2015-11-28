package com.noktiz.ui.web.friend.fb;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.social.facebook.FacebookFriendProvider;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.friend.PersonList;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Created by hasan on 7/21/14.
 */
public class FBPersonList extends Panel {

    public FBPersonList(String id) {

        super(id);
        UserFacade userInSite = ((UserSession)Session.get()).getUser();
        User user = (User) HSF.get().getCurrentSession().load(User.class,userInSite.getUser().getId());


        UserFacade userFacade = new UserFacade(user);
//        userFacade.addOrUpdateCredential("facebook_access_token","CAACEdEose0cBAIqnjKdJwMBgqs3ZAB1ZB1AT6qcLsjiz2flWom6xLpFZCHvZAMTdRS05DMkeJTZCKL9az7bIn6VtEAG5o4v52tFZA63JMaIkyccuMcKVCS75ZBpMMYvVH3rMZCSNA4WQkPQky8JZBB8ZBQYdaqoaGA6vQ76kaOTsYe8i1ghZA6UuwWKvfQZAG4GCZCbJ8ZBTZChuke5ipaYwDU6N7b8");
//        userFacade.addOrUpdateCredential("facebook_id","me");
        PersonList personList = new PersonList("fbfriends", Model.of(new SocialFriendListViewer(userFacade, new FacebookFriendProvider())), null, 30, null, true);
        this.add(personList);
    }


}
