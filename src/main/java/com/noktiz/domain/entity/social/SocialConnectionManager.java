package com.noktiz.domain.entity.social;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.social.facebook.RegisterFacebookConnection;
import com.noktiz.domain.social.google.GoogleUtils;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import org.hibernate.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by hasan on 2014-12-19.
 */
public class SocialConnectionManager extends BaseManager{

    public List<SocialConnection> getSuggestedFriends(User user, Integer from, Integer count){
        Query query = HSF.get().getCurrentSession().getNamedQuery("SocialConnectionsToSuggest");
        query.setParameter("user",user);
        query.setMaxResults(count);
        query.setFirstResult(from);
        return query.list();
    }

    public List<SocialConnection> getCurrentConnections(User user, SocialConnection.Context context) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("MySocialConnections");
        query.setParameter("user",user);
        query.setParameter("context", context);
        return query.list();
    }
    public List<SocialConnection> getSocialConnectionsToSid(String sid, SocialConnection.Context context) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("SocialConnectionsBySid");
        query.setParameter("sid",sid);
        query.setParameter("context", context);
        return query.list();
    }

    public List<SocialConnection> getRequestedConnections(UserFacade userFacade, SocialConnection.Context context) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("requestedSocialConnections");
        query.setParameter("context", context);
        query.setParameterList("sid", userFacade.getSocialId(context));
        return (List<SocialConnection>) query.list();
    }

    public void delete(SocialConnection s){
        HSF.get().getCurrentSession().delete(s);
    }

    public void lookForNewFriends(UserFacade userFacade){
        userFacade.refresh();
        if(userFacade.getCredential().getFacebookInfo().getFacebook_id()!=null) {
            long FacebookFriendLoadPeriodInMin = SystemConfigManager.getCurrentConfig().getPropertyAsLong(SystemConfig.FacebookFriendLoadPeriodInMin);
            if(userFacade.getProperty(UserProperties.LastFacebookFriendCheck) == null || (new Date().getTime()-Long.parseLong(userFacade.getProperty(UserProperties.LastFacebookFriendCheck)))/60000> FacebookFriendLoadPeriodInMin)
                new RegisterFacebookConnection().addPossibleFriendShips(userFacade);
        }
        if(userFacade.getCredential().getGoogleInfo().getGoogle_id()!=null) {
            long GoogleFriendLoadPeriodInMin = SystemConfigManager.getCurrentConfig().getPropertyAsLong(SystemConfig.GoogleFriendLoadPeriodInMin);
            if(userFacade.getProperty(UserProperties.LastGoogleFriendCheck) == null || (new Date().getTime()-Long.parseLong(userFacade.getProperty(UserProperties.LastGoogleFriendCheck)))/60000>GoogleFriendLoadPeriodInMin)
                new GoogleUtils().addPossibleFriendShips(userFacade);
        }
    }
}
