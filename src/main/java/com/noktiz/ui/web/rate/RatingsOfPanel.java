/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.AjaxPageNavigator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 *
 * @author Hossein
 */
public final class RatingsOfPanel extends UserPanel {

    public RatingsOfPanel(String id) {
        super(id);
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        RatingResultAnonymousPanel rates = new RatingResultAnonymousPanel("rates", new RatesOfDataProvider(getUserInSite(), new RateManager()));
        wmc.add(rates);

        AjaxPageNavigator nav = new AjaxPageNavigator("nav", rates.getDataView()) {
            @Override
            public void onAjax(AjaxRequestTarget target) {
                target.add(wmc);
            }
        };
        add(nav);

    }
}
