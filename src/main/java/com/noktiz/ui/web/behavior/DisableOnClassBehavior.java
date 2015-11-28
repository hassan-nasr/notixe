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
public class DisableOnClassBehavior extends  Behavior{
    String enable;
    String htmlClass;
    public DisableOnClassBehavior(String enable,String htmlClass) {
        this.enable = enable;
        this.htmlClass=htmlClass;
    }
    
    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DisableOnClassBehavior.class, "DisableOnClassBehavior.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("DisableOnClassBehavior('").append(markupId).append("','").
                append(htmlClass).append("','").
                append(enable).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
}
