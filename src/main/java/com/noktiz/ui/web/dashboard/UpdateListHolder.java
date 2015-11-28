/*
 * HomePage.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
package com.noktiz.ui.web.dashboard;

import com.noktiz.domain.entity.UserProperties;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.infobox.SimpleInfoBoxWithImage;
import com.noktiz.ui.web.component.lazy.LoadMoreList;
import com.noktiz.ui.web.dashboard.provider.UpdateListProvider;
import com.noktiz.ui.web.infobox.SimpleInfoBoxProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.List;

//import com.sun.xml.internal.ws.api.WebServiceFeatureFactory;

@AuthorizeInstantiation("USER")
public class UpdateListHolder extends BasePanel {
    UpdateListProvider.UpdateListLoadType updateListLoadType = UpdateListProvider.UpdateListLoadType.New;
    LoadMoreList updateList;
    final WebMarkupContainer wmc;

    public UpdateListHolder(String id) {
        super(id);

        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);

        Form form = new Form("form");
        add(form);
        final DropDownChoice myContexts = new DropDownChoice("mode", new IModel() {
            @Override
            public Object getObject() {
                return updateListLoadType;
            }

            @Override
            public void setObject(Object object) {
                updateListLoadType = (UpdateListProvider.UpdateListLoadType) object;
            }

            @Override
            public void detach() {

            }
        }, Arrays.asList(UpdateListProvider.UpdateListLoadType.values()), new IChoiceRenderer<UpdateListProvider.UpdateListLoadType>() {
            @Override
            public Object getDisplayValue(UpdateListProvider.UpdateListLoadType o) {
                return "show.UpdateListLoadType." + o.toString();
            }

            @Override
            public String getIdValue(UpdateListProvider.UpdateListLoadType o, int i) {
                return o.ordinal() + "";
            }
        }) {
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        form.add(myContexts);
        myContexts.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateList = new LoadMoreList("updateList", new UpdateListProvider(getUserInSite(), updateListLoadType), 5, true, false);
                wmc.addOrReplace(updateList);
                onListUpdate(target);
                target.add(wmc);

            }
        });
        updateList = new LoadMoreList("updateList", new UpdateListProvider(getUserInSite(), updateListLoadType), 10, true, false);
        wmc.add(updateList);
        onListUpdate(null);
//	add(new Label("label", new PropertyModel((UserSession)getSession(),"user.email")));
    }

    private void onListUpdate(AjaxRequestTarget art) {
        int size = ((List) updateList.getDefaultModelObject()).size();
        if (size == 0) {
            SimpleInfoBoxProvider p1 = new SimpleInfoBoxProvider(
                    "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
                    "homeIntro.info1title", "homeIntro.info1text"
            );
            p1.setAlignmentClass("aaa");
            SimpleInfoBoxWithImage simpleInfoBox1 = new SimpleInfoBoxWithImage("infoBox1", p1);
            wmc.addOrReplace(simpleInfoBox1);

            SimpleInfoBoxProvider p2 = new SimpleInfoBoxProvider(
                    "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
                    "homeIntro.info2title", "homeIntro.info2text"
            );
            p2.setAlignmentClass("aaa");
            SimpleInfoBoxWithImage simpleInfoBox2 = new SimpleInfoBoxWithImage("infoBox2", p2);
            wmc.addOrReplace(simpleInfoBox2);
            SimpleInfoBoxProvider p3 = new SimpleInfoBoxProvider(
                    "http://business24.agenturbelmediag.netdna-cdn.com/wp-content/uploads/2014/06/Feedback-Dooder-Shutterstock.com_.jpg",
                    "homeIntro.info3title", "homeIntro.info3text"
            );
            p3.setAlignmentClass("aaa");
            SimpleInfoBoxWithImage simpleInfoBox3 = new SimpleInfoBoxWithImage("infoBox3", p3);
            simpleInfoBox3.setVisible(true);
            simpleInfoBox2.setVisible(true);
            wmc.addOrReplace(simpleInfoBox3);
            updateList.setVisible(false);
        } else {
            Label infoBox1 = new Label("infoBox1");
            wmc.addOrReplace(infoBox1);
            Label infoBox2 = new Label("infoBox2");
            wmc.addOrReplace(infoBox2);
            Label infoBox3 = new Label("infoBox3");
            wmc.addOrReplace(infoBox3);
            infoBox1.setVisible(false);
            infoBox2.setVisible(false);
            infoBox3.setVisible(false);
        }
    }
}
