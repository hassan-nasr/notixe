/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Hossein
 */
public class TooltipBehavior extends Behavior{
    private Position p;
    private String text;
    private IModel<String> model;

    public TooltipBehavior(IModel<String> model, Position p) {
        this.p = p;
        this.model = model;
    }

    public TooltipBehavior(String text,Position p) {
        this.p=p;
        this.text=text;
    }
    public enum Position{
        up,bottom,left,right
    }

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component); //To change body of generated methods, choose Tools | Templates.
        component.add(new AttributeModifier("data-placement", p.toString()));
        component.add(new AttributeModifier("data-original-title", text!=null?text:model.getObject()));
        component.add(new AttributeAppender("class", " tooltips "));
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript("jQuery('.tooltips').tooltip();"));
    }
    
    

    
}
