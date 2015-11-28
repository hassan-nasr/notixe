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
public class ThreadListBehavoir extends Behavior{

    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }
    Component target;

    public ThreadListBehavoir(Component target) {
        this.target = target;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference
                (ThreadListBehavoir.class, "ThreadListBehavoir.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("setClick('").append(markupId).append("','").
                append(target.getMarkupId()).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
    
    
}
