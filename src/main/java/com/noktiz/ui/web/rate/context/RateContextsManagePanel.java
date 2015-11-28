package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel2;
import com.noktiz.ui.web.component.lazy.LoadMoreList;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by hasan on 2014-11-03.
 */
public class RateContextsManagePanel extends UserPanel {

    private final boolean small;
    RateContextManager rateContextManager = new RateContextManager();
    private WebMarkupContainer listHolder;


    public RateContextsManagePanel(String id, final UserFacade userFacade, boolean small) {
        super(id);
        this.small = small;
        setOutputMarkupId(true);
        userFacade.refresh();
        final RateContext[] newRateContext = {new RateContext("", "", userFacade.getUser())};

        final WebMarkupContainer modal = new WebMarkupContainer("modal");
        modal.setOutputMarkupId(true);
        add(modal);

        WebMarkupContainer showEdit = new WebMarkupContainer("newRating") {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new AttributeModifier("href", "#" + modal.getMarkupId()));
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
            }
        };
        add(showEdit);
        createNewContextEditor(userFacade, newRateContext, modal);
        WebMarkupContainer close = new WebMarkupContainer("close");
        modal.add(close);
        close.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                newRateContext[0] = new RateContext("", "", userFacade.getUser());
                RateContextEditPanel newContextEditor = createNewContextEditor(userFacade, newRateContext, modal);
                ajaxRequestTarget.add(newContextEditor);
                ajaxRequestTarget.appendJavaScript("$('.closeAction').click()");
            }
        }) ;

        listHolder = new WebMarkupContainer("listHolder");
        add(listHolder);
        listHolder.setOutputMarkupId(true);

        LoadMoreList contextPanels = new LoadMoreList<RateContext>("content", new RateContextDataProvider(userFacade,small),10,true, true);

        listHolder.add(new LazyLoadPanel2("rateContexts",contextPanels));
    }

    private RateContextEditPanel createNewContextEditor(final UserFacade userFacade, final RateContext[] newRateContext, final WebMarkupContainer modal) {
        RateContextEditPanel ret = new RateContextEditPanel("newContext", Model.of(newRateContext[0])) {
            @Override
            public void onAjaxSave(AjaxRequestTarget target, Result result) {
                result.displayInWicket(feedbackPanel);
                target.add(feedbackPanel);
                if(result.result) {
                    target.appendJavaScript("$('.closeAction').click()");
                    newRateContext[0] = new RateContext("", "", userFacade.getUser());
                    RateContextEditPanel newContextEditor = createNewContextEditor(userFacade, newRateContext, modal);
                    target.add(newContextEditor);
                }
                listHolder.addOrReplace(new LoadMoreList<RateContext>("rateContexts", new RateContextDataProvider(userFacade, small),small?5:10,true, false));
                target.add(listHolder);
            }
        };
        ret.init();
        modal.addOrReplace(ret);
        ret.setOutputMarkupId(true);

        return ret;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/bootstrap-modal/js/bootstrap-modal.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/bootstrap-modal/js/bootstrap-modalmanager.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "/assets/plugins/bootstrap-modal/css/bootstrap-modal.css")));

        response.render(OnDomReadyHeaderItem.forScript("$('.switch').bootstrapSwitch()"));
        response.render(new OnDomReadyHeaderItem("App.init()"));
    }

}
