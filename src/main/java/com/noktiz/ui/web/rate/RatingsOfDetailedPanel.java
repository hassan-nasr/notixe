package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.entity.rate.SimplePeriod;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.behavior.Select2Behavior;
import com.noktiz.ui.web.friend.PersonList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasan on 9/26/14.
 */
public class RatingsOfDetailedPanel extends UserPanel {
    private final UserFacade userInSite = getUserInSite();
    WebMarkupContainer wmc=null;
    RateContext rateContext;
    RateContextManager rateContextManager = new RateContextManager();
    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/select2/select2.min.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/select2/select2_metro.css")));
//        response.render(OnDomReadyHeaderItem.forScript("$('.select2').select2();"));
    }

    public RatingsOfDetailedPanel(String id) {
        super(id);

        userInSite.refresh();
        List<RateContext> rateContextList = new ArrayList<>();
        rateContextList.addAll(userInSite.getUser().getRateContexts());
        Long contextId = getRequest().getRequestParameters().getParameterValue("contextId").toLong(-1);
        rateContext = getRateContextToVIew(rateContextList, contextId);

        rateContextList.add(null);
        Form form = new Form("form");
        add(form);
        final DropDownChoice myContexts= new DropDownChoice("rateContexts",new IModel() {
            @Override
            public Object getObject() {
                return rateContext;
            }

            @Override
            public void setObject(Object object) {
                 rateContext = (RateContext) object;
            }

            @Override
            public void detach() {

            }
        },rateContextList,new IChoiceRenderer<RateContext>() {
            @Override
            public Object getDisplayValue(RateContext object) {
                if(object == null)
                    return getString("All");
                return object.getTitle();
            }

            @Override
            public String getIdValue(RateContext object, int index) {
                if(object == null)
                    return "";
                return object.getId().toString();
            }
        });
        form.add(myContexts);
        myContexts.add(new Select2Behavior());
        myContexts.setNullValid(true);
        myContexts.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateRates((RateContext) myContexts.getDefaultModelObject());
                target.add(wmc);
            }
        });
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        updateRates(rateContext);
    }

    private RateContext getRateContextToVIew(List<RateContext> rateContextList, Long contextId) {
        RateContext rateContextTemp;
        RateContext rateContext;
        if(contextId!=-1)
            rateContextTemp = rateContextManager.load(contextId);
        else rateContextTemp  = null ;
        if(rateContextList.contains(rateContextTemp))
            rateContext=rateContextTemp;
        else
            rateContext=null;
        return rateContext;
    }

    private void updateRates(RateContext rateContext) {
        PersonList ratings;
        if(rateContext == null) {
            ratings = new PersonList("ratings", Model.of(new RatesOfPersonProvider(userInSite)), null, 20, null, true);
        } else if(rateContext.getTimeBetweenRatings().equals(SimplePeriod.SingleTime)) {
            ratings = new PersonList("ratings", Model.of(new RatesOfPersonProvider(rateContext)), null, 20, null, true);
        }
        else{
            ratings = new PersonList("ratings", Model.of(new RatesOfContextPersonProvider(rateContext)), null, 2, null, true);
        }

//        SerializationUtils.serialize(this);
        wmc.addOrReplace(ratings);
    }
}
