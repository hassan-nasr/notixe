/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public class SelectPersonBehavior extends Behavior{
    CheckBox checkbox;
    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }

    public SelectPersonBehavior(CheckBox checkbox) {
        this.checkbox = checkbox;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(SelectPersonBehavior.class, "SelectPersonBehavior.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("SelectPersonBehavior('").append(markupId).append("','").
                append(checkbox.getMarkupId()).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
    
    
    
}
