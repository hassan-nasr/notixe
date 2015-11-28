/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.Response;

/**
 *
 * @author Hossein
 */
public abstract class AjaxPageNavigator extends Panel {

    public AjaxPageNavigator(String id, final IPageable iPageable) {
        super(id);
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc"){

            @Override
            public void renderHead(IHeaderResponse response) {
                String js="jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
            
            
        };
        add(wmc);
        wmc.setOutputMarkupId(true);
        AjaxLink first = new AjaxLink("first") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                iPageable.setCurrentPage(0);
                target.add(wmc);
                onAjax(target);
            }

            @Override
            public boolean isEnabled() {
                return iPageable.getCurrentPage()!=0;
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender(); //To change body of generated methods, choose Tools | Templates.
                if(!isEnabled()){
                    add(new AttributeModifier("class", "tooltips btn mini disabled "));
                }
                else{
                    add(new AttributeModifier("class", " tooltips btn mini  "));
                }
            }
        };
        wmc.add(first);
        AjaxLink previous = new AjaxLink("previous") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                final long currentPage = iPageable.getCurrentPage();
                if(currentPage>0){
                    long targetPage = currentPage-1;
                    if(targetPage>=iPageable.getPageCount()){
                        targetPage=0;
                    }
                    iPageable.setCurrentPage(targetPage);
                    onAjax(target);
                }
                target.add(wmc);
            }

            @Override
            public boolean isEnabled() {
                return iPageable.getCurrentPage()!=0;
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender(); //To change body of generated methods, choose Tools | Templates.
                if(!isEnabled()){
                    add(new AttributeModifier("class", "tooltips btn mini yellow-stripe disabled "));
                }
                else{
                    add(new AttributeModifier("class", " tooltips btn mini yellow-stripe "));
                }
            }
        };
        wmc.add(previous);
        AjaxLink next = new AjaxLink("next") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                final long currentPage = iPageable.getCurrentPage();
                if(currentPage<iPageable.getPageCount()){
                    iPageable.setCurrentPage(currentPage+1);
                    onAjax(target);
                }
                target.add(wmc);
            }

            @Override
            public boolean isEnabled() {
                return iPageable.getCurrentPage()<iPageable.getPageCount()-1;
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender(); //To change body of generated methods, choose Tools | Templates.
                if(!isEnabled()){
                    add(new AttributeModifier("class", "tooltips btn mini yellow-stripe disabled "));
                }
                else{
                    add(new AttributeModifier("class", " tooltips btn mini yellow-stripe "));
                }
            }
        };
        wmc.add(next);
        AjaxLink last = new AjaxLink("last") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                iPageable.setCurrentPage(iPageable.getPageCount()-1);
                target.add(wmc);
                onAjax(target);
            }

            @Override
            public boolean isEnabled() {
                return iPageable.getCurrentPage()<iPageable.getPageCount()-1;
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender(); //To change body of generated methods, choose Tools | Templates.
                if(!isEnabled()){
                    add(new AttributeModifier("class", "tooltips btn mini yellow-stripe disabled "));
                }
                else{
                    add(new AttributeModifier("class", " tooltips btn mini yellow-stripe "));
                }
            }
        };
        wmc.add(last);
        
    }

    public abstract void onAjax(AjaxRequestTarget target);
}
