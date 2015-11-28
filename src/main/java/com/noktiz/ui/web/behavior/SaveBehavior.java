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
public class SaveBehavior extends Behavior{

    String enable;
    String saveEnable;
    String enableStr;
    String disableStr;
    public SaveBehavior(String newCharEnable,String saveEnable) {
        this.enable = newCharEnable;
        this.saveEnable = saveEnable;
        this.enableStr = "Save as draft (unsaved)";
        this.disableStr="Save as draft (saved)";
    }
    
    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(SaveBehavior.class, "SaveBehavior.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("SaveBehavior('").append(markupId).append("','").
                append(enable).append("','").
                append(saveEnable).append("','").
                append(enableStr).append("','").
                append(disableStr).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }
    
}
