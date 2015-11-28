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
public class AutoGrowTextAreaBehavior extends Behavior{

    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }
    
    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DisableOnClickBehavior.class, "jquery.autogrow-textarea.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("jQuery('#").append(markupId).append("').autogrow()");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
    
}
