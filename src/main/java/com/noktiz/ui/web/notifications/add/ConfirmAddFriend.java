package com.noktiz.ui.web.notifications.add;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.lazy.LazyInitComponent;
import com.noktiz.ui.web.profile.AboutBasicInfoPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by hasan on 29 January 15.
 */
public class ConfirmAddFriend extends UserPanel implements LazyInitComponent {
    private final UserFacade userFacade;

    public ConfirmAddFriend(String id,UserFacade userFacade) {
        super(id);
        this.userFacade = userFacade;
    }

    @Override
    public void init() {
        add(new AboutBasicInfoPanel("friendBasicInfo",userFacade));
    }
}
