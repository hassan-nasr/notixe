package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.ui.web.base.UserPanel;

/**
 * Created by hasan on 2014-11-04.
 */
public class RateContextFastEditViewPanel extends UserPanel {


    public RateContextFastEditViewPanel(String id, RateContext rateContext) {
        super(id);
        final RateContextViewEditPanel viewPanel = new RateContextViewEditPanel("viewPanel", rateContext, false);
        add(viewPanel);

    }

}
