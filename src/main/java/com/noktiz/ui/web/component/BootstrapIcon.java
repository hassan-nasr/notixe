/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.component;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Hossein
 */
public class BootstrapIcon extends WebMarkupContainer{

    public BootstrapIcon(String id, IModel<?> model) {
        super(id, model);
        add(new AttributeAppender("class", model, " "));
    }
    
}
