package com.noktiz.ui.web.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by Hossein on 2/27/2015.
 */
@Deprecated
public class OverrideFormSubmitOnEnter extends Behavior {
    private AjaxSubmitLink submitLink;

    public OverrideFormSubmitOnEnter(AjaxSubmitLink submitLink) {
        this.submitLink = submitLink;
        if(submitLink!=null){
            submitLink.setOutputMarkupId(true);
        }
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(
                new JavaScriptResourceReference(OverrideFormSubmitOnEnter.class,
                        "OverrideFormSubmitOnEnter.js")));
        StringBuilder script;
        if (submitLink == null) {
            script = new StringBuilder().append("preventSubmit('" + component.getMarkupId() + "')");
        } else {
            script = new StringBuilder().append("ajaxSubmitForm('" + component.getMarkupId() +
                    "','"+submitLink.getMarkupId()+
                    "')");
        }
        response.render(OnDomReadyHeaderItem.forScript(script));
    }
}
