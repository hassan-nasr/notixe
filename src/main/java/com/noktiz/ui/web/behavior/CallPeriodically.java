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
public class CallPeriodically extends Behavior {
    long periodInMillisecond;
    private String enableVar;
    private boolean disableOnSuccess;
    private String disableBeforCall;
    public CallPeriodically(long periodInMillisecond,String enableVar,boolean disableOnSuccess,String disableBeforCall) {
        this.enableVar=enableVar;
        this.disableBeforCall=disableBeforCall;
        this.periodInMillisecond = periodInMillisecond;
        this.disableOnSuccess=disableOnSuccess;
    }

    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(CallPeriodically.class, "CallPeriodically.js")));
        String markupId = component.getMarkupId();
        StringBuilder sb= new StringBuilder();
        sb.append("CallPeriodically('").append(markupId).append("',").
                append(periodInMillisecond).append(",'").
                append(enableVar).append("','").
                append(disableBeforCall).append("',").
                append(disableOnSuccess).append(");");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }

    
        
}
