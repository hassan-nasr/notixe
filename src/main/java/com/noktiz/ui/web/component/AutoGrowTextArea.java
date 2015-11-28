/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.component;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Hossein
 */
public class AutoGrowTextArea extends TextArea{

    public AutoGrowTextArea(String id) {
        super(id);
        init();
    }

    public AutoGrowTextArea(String id, IModel model) {
        super(id, model);
        init();
    }

    private void init(){
        this.setEscapeModelStrings(false);
        this.add(new com.noktiz.ui.web.behavior.AutoGrowTextAreaBehavior());
        add(new AttributeAppender("style", " ;resize: none; "));
    }

    
    
    
}
