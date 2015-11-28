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
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public class PressKeyEnableBehavior extends Behavior{
    String enable;

    public PressKeyEnableBehavior(String enable) {
        this.enable = enable;
    }
    
    @Override
    public void beforeRender(Component component) {
        super.beforeRender(component); //To change body of generated methods, choose Tools | Templates.
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(PressKeyEnableBehavior.class, "PressKeyEnableBehavior.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("PressKeyEnableBehavior('").append(markupId).append("','").
                append(enable).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
    
}
