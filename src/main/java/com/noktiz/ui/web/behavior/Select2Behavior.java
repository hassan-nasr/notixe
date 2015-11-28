package com.noktiz.ui.web.behavior;

import com.noktiz.ui.web.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by Hossein on 12/11/2014.
 */
public class Select2Behavior extends Behavior {
    @Override
    public void onConfigure(Component component) {
        component.setOutputMarkupId(true);
        super.onConfigure(component);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        String id=component.getMarkupId();
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/select2/select2.min.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/select2/select2_metro.css")));
        response.render(new OnDomReadyHeaderItem("jQuery('#"+id+"').select2();"));
    }
}
