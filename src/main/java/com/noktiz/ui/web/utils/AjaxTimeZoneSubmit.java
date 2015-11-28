package com.noktiz.ui.web.utils;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Created by hasan on 2014-11-12.
 */
public class AjaxTimeZoneSubmit extends Panel {
    public AjaxTimeZoneSubmit(String id) {
        super(id);
        Form f = new Form("form"){
            @Override
            protected void onSubmit() {
                super.onSubmit();
            }
        };
        add(f);
        final HiddenField timeZone = new HiddenField("timeZone", Model.of());
        f.add(timeZone);
        AjaxSubmitLink submitLink = new AjaxSubmitLink("submit",f) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    Integer time = Integer.parseInt(timeZone.getDefaultModelObjectAsString());
                    getSession().setAttribute("timeZoneMinutes", time);
                }catch (Exception e){
                    Logger.getLogger(this.getClass()).error(e);
                }
            }
        };
        f.add(submitLink);
    }
}
