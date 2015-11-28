/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import com.noktiz.ui.web.BasePanel;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;

/**
 *
 * @author Hossein
 */
public final class TimeElapse extends BasePanel {

    Label l;
    long period;
    String enableVar;
    public TimeElapse(String id, IModel<Date> date, long period,String enableVariable) {
        super(id,date);
        this.period = period;
        enableVar=enableVariable;
        if(enableVar==null || "".equals(enableVar)){
            enableVar="__ENABLE__";
        }
        l = new Label("time");
        add(l);
        l.setOutputMarkupId(true);
    }
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response); //To change body of generated methods, choose Tools | Templates.
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(TimeElapse.class, "TimeElapse.js")));
        String markupId = l.getMarkupId();
        DateTime dateTime = getUserDate(getObj());
        StringBuilder sb = new StringBuilder();
        if (dateTime != null) {
            sb.append("setElapceTime('").append(markupId).append("',").
                    append(period).append(",'").append(enableVar).append("',").
                    append(dateTime.getYear()).
                    append(",").append(dateTime.getMonthOfYear()-1).append(",").
                    append(dateTime.getDayOfMonth()).append(",").
                    append(dateTime.getHourOfDay()).append(",").
                    append(dateTime.getMinuteOfHour()).append(",").
                    append(dateTime.getSecondOfMinute()).append(");");
        } else {
            sb.append("setElapceTime2('").append(markupId).append("',").append(period).append(",'")
                    .append(enableVar).append("');");
        }
        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }

    private Date getObj() {
        return (Date) getDefaultModelObject();
    }
}
