package com.noktiz.ui.web.component.lazy;

import com.noktiz.ui.web.behavior.IndicatingBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Created by hasan on 2014-12-26.
 */
public class LazyLoadPanel extends Panel {
    private final LazyInitComponent body;
    Boolean done=false ;
    WebMarkupContainer wmc;
    AjaxLink link;
    public LazyLoadPanel(String id, boolean autoLoad, LazyInitComponent body) {
        super(id);
        this.body = body;
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        WebMarkupContainer contentPlaceHolder = new WebMarkupContainer("content");
        wmc.add(contentPlaceHolder);
        wmc.add(new Image("loadingIndicator",new PackageResourceReference(IndicatingBehavior.class, "ajax-loader2.gif")));
        link = new AjaxLink("loaderLink") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                load(ajaxRequestTarget);
            }


            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                response.render(new OnDomReadyHeaderItem("setTimeout(function(){$('#" + getMarkupId() + "').click()},1000)"));
            }

        };
        add(link);
        if(!autoLoad)
            link.setVisible(false);
        link.setOutputMarkupId(true);
    }
    public void load(AjaxRequestTarget ajaxRequestTarget) {
        if(done && !body.canReInit())
            return;
        body.init();
        wmc.addOrReplace((Panel) body);
        wmc.addOrReplace(new WebMarkupContainer("loadingIndicator"));
        ajaxRequestTarget.add(wmc);
        done = true;
    }

}
