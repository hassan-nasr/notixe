/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import com.noktiz.ui.web.behavior.IndicatingBehavior;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;

/**
 *
 * @author Hossein
 * @param
 */
public abstract class IndicatingAjaxSubmitLink2 extends AjaxSubmitLink implements IAjaxIndicatorAware {

    IndicatingBehavior indicatingBehavior;
    public IndicatingAjaxSubmitLink2(String id) {
        super(id);
        indicatingBehavior = new IndicatingBehavior();
        add(indicatingBehavior);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicatingBehavior.getAjaxIndicatorMarkupId();
    }

}
