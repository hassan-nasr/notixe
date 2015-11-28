package com.noktiz.ui.web.rate.context;

import com.noktiz.ui.web.BaseUserPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * Created by hasan on 2014-11-03.
 */


@AuthorizeInstantiation("USER")
public class RateContextManagePage extends BaseUserPage {
    public RateContextManagePage() {
        super("rateContext");
        RateContextsManagePanel rateContextsManagePanel = new RateContextsManagePanel("RateContextManagePanel",getUserInSite(), false);
        add(rateContextsManagePanel);
    }
}
