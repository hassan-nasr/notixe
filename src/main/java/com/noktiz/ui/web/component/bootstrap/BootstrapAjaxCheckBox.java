package com.noktiz.ui.web.component.bootstrap;

import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;

/**
 * Created by hasan on 2014-11-24.
 */
public abstract class BootstrapAjaxCheckBox extends AjaxCheckBox {
    public BootstrapAjaxCheckBox(String id) {
        super(id);
        setOutputMarkupId(true);
    }

    public BootstrapAjaxCheckBox(String id, IModel<Boolean> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(new OnDomReadyHeaderItem("$('#"+getOutputMarkupId()+"').bootstrapSwitch()"));
    }
}
