package com.noktiz.ui.web.component.lazy;

import org.apache.wicket.*;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * Created by hasan on 2015-01-07.
 */
public class LazyLoadPanel2 extends Panel{
    public LazyLoadPanel2(String id, LazyInitComponent body) {
        super(id);
        add(new AjaxLazyLoadPanel("lazy")
        {
            @Override
            public org.apache.wicket.Component getLazyLoadComponent(String id)
            {
                body.init();
                return (Component) body;
            }
        });
    }
}
