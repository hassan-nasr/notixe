/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public class ItemBehavior extends Behavior{

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
//        response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(ItemBehavior.class, "ItemBehavior.js")));
//        String markupId = component.getMarkupId();
//        StringBuilder sb= new StringBuilder();
//        sb.append("resizeTileOnStart('").append(markupId).append("');");
//        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
}
