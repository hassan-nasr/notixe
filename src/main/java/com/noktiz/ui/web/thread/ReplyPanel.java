/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.thread;

import com.noktiz.ui.web.behavior.CallPeriodically;
import com.noktiz.ui.web.behavior.PressKeyEnableBehavior;
import com.noktiz.ui.web.behavior.SaveBehavior;
import com.noktiz.ui.web.component.AutoGrowTextArea;
import com.noktiz.ui.web.component.ChangePropertyInput;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public abstract class ReplyPanel extends Panel {

    public ReplyPanel(String id, IModel<String> text,IModel<String> direction) {
        super(id, text);
        Form form = new Form("form");
        add(form);
        final TextArea textarea = new AutoGrowTextArea("textarea", text);
        form.add(textarea);
        textarea.add(new AttributeModifier("dir", direction));
        final ChangePropertyInput dir;
        dir = new ChangePropertyInput("dir", new Model(), textarea, "dir");
        form.add(dir);
        
        String enableVar= RandomStringUtils.randomAlphabetic(10);
        String saveEnableVar= RandomStringUtils.randomAlphabetic(10);
        textarea.add(new PressKeyEnableBehavior(enableVar));
        AjaxSubmitLink send = new IndicatingAjaxSubmitLink2("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                onSend(textarea.getDefaultModelObjectAsString(),dir.getDefaultModelObjectAsString());
                onAjax(target);
            }
        };
        form.add(send);
        AjaxSubmitLink save = new IndicatingAjaxSubmitLink2("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                onSave(textarea.getDefaultModelObjectAsString(),dir.getDefaultModelObjectAsString(),true);
                target.add(this);
                target.focusComponent(null);
//                onAjax(target);
            }
        };
        form.add(save);
//        AjaxSubmitLink autosave = new IndicatingAjaxSubmitLink2("autosave") {
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                onSave(textarea.getDefaultModelObjectAsString(),dir.getDefaultModelObjectAsString(),true);
//                target.add(save);
//                target.add(this);
//                onAjax(target);
//            }
//        };
//        form.add(autosave);
        save.add(new CallPeriodically(20000, saveEnableVar,true,enableVar));
        save.add(new SaveBehavior(enableVar,saveEnableVar));
        AjaxSubmitLink discard = new IndicatingAjaxSubmitLink2("discard") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                onDiscard(target);
                onAjax(target);
            }
        };
        form.add(discard);
    }

    protected abstract void onSend(String text,String dir);

    protected abstract void onSave(String text,String dir,boolean auto);

    public abstract void onDiscard(AjaxRequestTarget art);

    protected abstract void onAjax(AjaxRequestTarget target);
}
