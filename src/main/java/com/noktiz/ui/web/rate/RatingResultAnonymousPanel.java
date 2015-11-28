/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

/**
 *
 * @author Hossein
 */
public final class RatingResultAnonymousPanel extends Panel {

    DataView rateList;

    public RatingResultAnonymousPanel(String id, IDataProvider data) {
        super(id);
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        add(wmc);
        rateList = new DataView("rateList", data, 10) {
            @Override
            protected void populateItem(Item item) {
                Pair<String[], Rate> myItem = (Pair<String[], Rate>) item.getDefaultModelObject();
                String[] ss = myItem.getLeft();
                if (ss!=null) {
                    WebMarkupContainer time = new WebMarkupContainer("time");
                    item.add(time);
                    time.add(new Label("s1", ss[0]));
                    time.add(new Label("s2", ss[1]));
                }
                else{
                    WebMarkupContainer time = new WebMarkupContainer("time");
                    time.setVisible(false);
                    item.add(time);
                }
                Rate rate =  myItem.getRight();
                WebMarkupContainer ratePanel = new WebMarkupContainer("ratePanel");
                item.add(ratePanel);
                for (int i = 0; i < 5; i++) {
                    WebMarkupContainer star = new WebMarkupContainer("star-" + (i + 1));
                    ratePanel.add(star);
                    if (i < rate.getRate()) {
                        star.add(new AttributeAppender("class", "icon-star"));
                    } else {
                        star.add(new AttributeAppender("class", "icon-star-empty"));
                    }
                }
                Label text = new Label("text", rate.getComment());
                ratePanel.add(text);
                AjaxLink reply = new AjaxLink("reply") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                    }

                };
                ratePanel.add(reply);

            }
        };
        wmc.add(rateList);


    }
    public DataView getDataView(){
        return rateList;
    }
}
