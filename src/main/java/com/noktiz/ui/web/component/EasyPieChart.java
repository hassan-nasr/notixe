/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public final class EasyPieChart extends Panel {

        Label chart;
        int percent;
        String color;
        
    public EasyPieChart(String id,String text,Integer percent,String color) {
        super(id);
        chart = new Label("chart",text);
        add(chart);
        chart.add(new AttributeModifier("data-percent", percent));
        chart.setOutputMarkupId(true);
        this.percent=percent;
        this.color=color;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(EasyPieChart.class, "EasyPieChart.js")));
        StringBuilder s= new StringBuilder();
        s.append("EasyPieChart('").append(chart.getOutputMarkupId()).append("',").append(percent).append(",'").append(color).append("')");
        response.render(new OnDomReadyHeaderItem(s.toString()));
    }
    
}
