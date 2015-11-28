package com.noktiz.ui.web.rate.overview;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.access.BasePrivacy;
import com.noktiz.domain.entity.access.PrivacyLevel;
import com.noktiz.domain.entity.access.TempPrivacy;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.entity.rate.RateOverview;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.profile.ProtectedPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by hasan on 2014-10-07.
 */
public class AllRateOverviewPanel extends BasePanel {
    RateManager rateManager = new RateManager();
    public AllRateOverviewPanel(String id, final User user) {
        super(id);
        Map<RateContext, List<RateOverview>> overviews = rateManager.getRateOverview(user, null, null);
        ListView overviewsComponents = new ListView("item", Model.of(overviews.values())) {
            @Override
            protected void populateItem(ListItem item) {
                final List<RateOverview> current= (List<RateOverview>) item.getModelObject();
                BasePrivacy privacy = new TempPrivacy(user,PrivacyLevel.Friends);//user.getRateOverviewPrivacy(current.get(0).getEndorseContext());
                item.add(new ProtectedPanel("eachRate",new RateOverviewPanel("content",current),privacy,getString("privateAccessMessage"),user.equals(getUserInSite().getUser()),true){
                    @Override
                    protected void updatePrivacy(PrivacyLevel value) {
//                        user.getRateOverviewPrivacy(current.get(0).getEndorseContext()).setPrivacyLevel(value);
//                        user.save();
                    }
                });
//                item.add(new RateOverviewPanel("eachRate",current));
            }

        };
        add(overviewsComponents);
    }
}
