/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public final class ChangePropertyInput extends Panel {

    Component watching;
    String attr;
    TextField property;

    public ChangePropertyInput(String id, IModel<String> model, Component watching, String attr) {
        super(id, model);
        this.attr = attr;
        this.watching = watching;
        property = new HiddenField<>("property", model);
        property.setOutputMarkupId(true);
        add(property);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender(); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ChangePropertyInput.class, "ChangePropertyInput.js")));
        String watchingId = watching.getMarkupId();
        String watcherId = property.getMarkupId();
        StringBuilder sb = new StringBuilder();
        sb.append("ChangePropertyInput('").append(watchingId).append("','").
                append(attr).append("','").
                append(watcherId).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
}
