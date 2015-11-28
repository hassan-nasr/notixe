package com.noktiz.ui.web.base;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.auth.UserSession;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by hasan on 9/16/14.
 */
public class UserPanel extends Panel {

    public UserPanel(String id) {
        super(id);
    }

    public UserPanel(String id, IModel<?> model) {
        super(id, model);
    }

    public UserSession getUserSession(){
        return ((UserSession)getSession());
    }
    public UserFacade getUserInSite(){
        return getUserSession().getUser();
    }
}
