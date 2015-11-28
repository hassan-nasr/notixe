package com.noktiz.ui.web.component.bootstrap;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * Created by hasan on 2014-11-24.
 */
public class RateField extends TextField<Double> {
    public RateField(String id) {
        super(id,Double.class);
        setOutputMarkupId(true);
    }

    public RateField(String id, Class<Double> type) {
        super(id, type);
        setOutputMarkupId(true);
    }

    public RateField(String id, IModel<Double> model) {
        super(id, model,Double.class);
        setOutputMarkupId(true);
    }

    public RateField(String id, IModel<Double> model, Class<Double> type) {
        super(id, model, type);
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new OnDomReadyHeaderItem("$('#"+getMarkupId()+"').rating()"));
    }
}
