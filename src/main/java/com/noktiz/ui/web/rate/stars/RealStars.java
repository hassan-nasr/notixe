package com.noktiz.ui.web.rate.stars;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by hasan on 2014-10-13.
 */
public class RealStars extends Panel {
    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(this.getClass(), "RealStars.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(this.getClass(), "RealStars.js")));
        response.render(OnDomReadyHeaderItem.forScript("$(function() {\n" +
                "    $('span.realStars').stars();\n" +
                "});"));
    }


    public RealStars(String id, double value, int size) {
        super(id);
        Label stars = new Label("stars", value);
        add(stars);

        stars.add(new AttributeAppender("size", size));
    }
}
