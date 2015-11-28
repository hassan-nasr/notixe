package com.noktiz.ui.web.component.lazy;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by Hossein on 12/18/2014.
 */
@Deprecated
public abstract class ModalAjax extends Panel {
    public ModalAjax(String id) {
        super(id);
    }

    public ModalAjax(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        final WebMarkupContainer modalContainer = new WebMarkupContainer("modalContainer");
        add(modalContainer);
        AjaxLink loadModal = new AjaxLink("loadModal"){

            @Override
            public void onClick(AjaxRequestTarget target) {
                modalContainer.addOrReplace(getModalContent("modal"));
                target.appendJavaScript("");
            }
        };
        modalContainer.add(loadModal);
        loadModal.setOutputMarkupId(true);
        modalContainer.add(new Label("modal"));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    abstract protected Component getModalContent(String id);


}
