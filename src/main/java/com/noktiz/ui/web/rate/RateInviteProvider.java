package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.entity.rate.NotificationRateInvite;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
//import com.noktiz.ui.web.dashboard.notif.RateInviteExtendedNotificationPanel;
import com.noktiz.ui.web.dashboard.notif.RateInviteNotificationPanel;
import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by hasan on 2014-12-02.
 */
public class RateInviteProvider extends ListDataProvider<NotificationRateInvite> {
    UserFacade userFacade;
    UserFacade sender;
    UpdateListLoadType type;
    boolean hasMore = true;

    public RateInviteProvider(UserFacade userFacade, UserFacade sender,UpdateListLoadType type) {
        super(false);
        this.userFacade = userFacade;
        this.sender = sender;
        this.type = type;
    }

    @Override
    public List<NotificationRateInvite> getElements(Integer from, Integer count) {
        throw new NotImplementedException();
    }

    @Override
    public List<NotificationRateInvite> getElementsBefore(NotificationRateInvite From, Integer count) {
        List<NotificationRateInvite> ret = NotificationRateInvite.loadByUserAndSender(userFacade.getUser(), sender.getUser(), From, count);
        hasMore= ret.size()>=count;
        return ret;
    }

    @Override
    public BasePanel getPanel(String name, NotificationRateInvite obj) {
        if (obj != null) {
            NotificationRateInvite naf = (NotificationRateInvite) obj;
            RateInviteNotificationPanel npaf = new RateInviteNotificationPanel(name, new Model(naf));
            return (npaf);
        }
        return null;
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

    public enum UpdateListLoadType {
        All,
        New,
        Subscribed
    }
}
