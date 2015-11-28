/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Hossein
 */
public class DualStateTextField extends Panel {

    private IModel<?> editable;
    
    public DualStateTextField(String id, IModel<?> model, IModel<Boolean> editable) {
        super(id, model);
        this.editable = editable;
        TextField field = new TextField("field", model) {

            @Override
            protected void onConfigure() {
                setVisible((Boolean)DualStateTextField.this.editable.getObject());
                if(isVisible()){
                    for (Behavior behavior : DualStateTextField.this.getBehaviors()) {
                        add(behavior);
                    }
                }
            }

        };
        add(field);
        
        Label label = new Label("label", model) {

            @Override
            protected void onConfigure() {
                setVisible(!(Boolean)DualStateTextField.this.editable.getObject());
                if(isVisible()){
                    for (Behavior behavior : DualStateTextField.this.getBehaviors()) {
                        add(behavior);
                    }
                }
            }
        };
        add(label);
    }

}
