package com.noktiz.ui.web.rate;

import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.friend.PersonList;
import org.apache.wicket.model.Model;

/**
 * Created by hasan on 9/26/14.
 */
public class RatingsDonePanel extends UserPanel {
    public RatingsDonePanel(String id) {
        super(id);
        PersonList ratings = new PersonList("ratings", Model.of(new RatesDonePersonProvider(getUserInSite())), null, 30, null, true);
//        SerializationUtils.serialize(this);
        this.add(ratings);

    }
}
