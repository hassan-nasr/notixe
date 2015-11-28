package com.noktiz.ui.web.dashboard.provider;

import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.entity.rate.NotificationRateInvite;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
//import com.noktiz.ui.web.dashboard.notif.RateInviteExtendedNotificationPanel;
import com.noktiz.ui.web.dashboard.notif.RateInviteNotificationPanel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by hasan on 2014-11-24.
 */
public class UpdateListProvider extends ListDataProvider<BaseNotification> {
    UserFacade userFacade;
    UpdateListLoadType type;
    boolean hasMore = true;

    public UpdateListProvider(UserFacade userFacade, UpdateListLoadType type) {
        super(false);
        this.userFacade = userFacade;
        this.type = type;
    }

    @Override
    public List<BaseNotification> getElements(Integer from, Integer count) {
        List<BaseNotification> ret = userFacade.loadMStream(type.equals(UpdateListLoadType.All), type.equals(UpdateListLoadType.Subscribed), from, count);
        hasMore= ret.size()>=count;
        return ret;
    }

    @Override
    public List<BaseNotification> getElementsBefore(BaseNotification From, Integer count) {
        List<BaseNotification> ret = userFacade.loadMStream(type.equals(UpdateListLoadType.All), type.equals(UpdateListLoadType.Subscribed), From, count);
        hasMore= ret.size()>=count;
        return ret;
    }

    @Override
    public BasePanel getPanel(String name, BaseNotification obj) {
        if (obj instanceof NotificationRateInvite) {
            NotificationRateInvite naf = (NotificationRateInvite) obj;
            RateInviteNotificationPanel npaf = new RateInviteNotificationPanel(name, new Model(naf));
//            RateInviteExtendedNotificationPanel npaf2 = new RateInviteExtendedNotificationPanel(name, new Model(naf));
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
