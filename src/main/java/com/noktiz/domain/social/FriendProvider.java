package com.noktiz.domain.social;

import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hasan on 7/18/14.
 */
public interface FriendProvider {
    public ArrayList<SocialConnection> getConnections(UserFacade u) throws AccessDeniedException, IOException;

    SocialConnection getReverse(SocialConnection connection);
}
