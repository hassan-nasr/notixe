package com.noktiz.ui.web.rate.overview;

import com.noktiz.domain.entity.rate.RateOverview;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.rate.stars.RealStars;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by hasan on 2014-10-07.
 */
public class RateOverviewPanel extends Panel {


    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "./assets/css/pages/news.css")));
    }


    public RateOverviewPanel(String id,List<RateOverview> current) {
        super(id);

        final DateFormat df = new SimpleDateFormat("MMMM dd yy");
        Label title = new Label("title",current.get(0).getEndorseContext().getTitle());
        add(title);
        ListView points= new ListView("item", Model.of(current)) {

            @Override
            protected void populateItem(ListItem item) {
                RateOverview rateOverview = (RateOverview) item.getModelObject();

                item.add(new Label("date",df.format(rateOverview.getFrom()) + " to " + df.format(rateOverview.getTo())));
                item.add(new RealStars("avg", rateOverview.getEndorseAvg(),50));
                item.add(new Label("count","by " + rateOverview.getEndorseCount() +" rates"));

            }
        };
        add(points);
    }
}
