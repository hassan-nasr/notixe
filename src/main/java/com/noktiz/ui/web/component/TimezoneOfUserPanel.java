/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author Hossein
 */
public final class TimezoneOfUserPanel extends TextField {

    public TimezoneOfUserPanel(String id,IModel<Integer> model) {
        super(id,model);
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(TimezoneOfUserPanel.class, "TimezoneOfUserPanel.js")));
        StringBuilder sb = new StringBuilder();
        sb.append("filltimezone('").append(getMarkupId()).append("');");
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }

}
