/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public class IndicatingBehavior extends Behavior implements IAjaxIndicatorAware {

    private Component component;

    @Override
    public void beforeRender(Component component) {
        super.beforeRender(component); //To change body of generated methods, choose Tools | Templates.
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response); //To change body of generated methods, choose Tools | Templates.
        AjaxRequestTarget target = component.getRequestCycle().find(AjaxRequestTarget.class);
//        if (target != null) {
//            final String javascript = "var e = Wicket.$('" + getMarkupId()
//                    + "'); if (e != null && typeof(e.parentNode) != 'undefined') e.parentNode.removeChild(e);";
//
//            target.prependJavaScript(javascript);
//        }
        StringBuilder s = new StringBuilder();
        s.append("jQuery('#").append(component.getMarkupId())
                .append("').append(\"<img src='").append(getImageUrl()).append("' style='display:none; width: 17px; height: 17px' id='")
                .append(getMarkupId()).append("'/>\")");
        response.render(OnDomReadyHeaderItem.forScript(s.toString()));
    }

    static PageParameters pp = new PageParameters();

    @Override
    public String getAjaxIndicatorMarkupId() {
        return getMarkupId();
    }

    private String getImageUrl() {
        return RequestCycle.get().urlFor(new PackageResourceReference(IndicatingBehavior.class, "ajax-loader2.gif"), pp).toString();
    }

    private String getMarkupId() {
        return component.getMarkupId() + "_indicator";
    }

    @Override
    public final void bind(final Component component) {
        this.component = component;
    }

}
