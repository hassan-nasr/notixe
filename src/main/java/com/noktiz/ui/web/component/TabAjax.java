/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import com.noktiz.ui.web.behavior.IndicatingBehavior;
import java.io.Serializable;
import java.util.List;

import com.noktiz.ui.web.behavior.IntroBehavior;
import com.noktiz.ui.web.rate.RatingsPage;
import com.noktiz.ui.web.utils.ChangePageUrl;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public class TabAjax extends Panel implements IAjaxIndicatorAware {

    ITabAjaxProvider active;
        final WebMarkupContainer tabContainer;
    private ITabAjaxProvider defaultTab;

    public TabAjax(String id, IModel<List<ITabAjaxProvider>> tabs){
        this(id, tabs,defaultOptions);
    }
    public TabAjax(String id, IModel<List<ITabAjaxProvider>> tabs,Options options) {
        super(id, tabs);
        if (tabs.getObject().isEmpty()) {
            throw new IllegalArgumentException("tabs could not be empty");
        }
        setActive(tabs.getObject().get(0),false,null);
        String tabContainerClass="ver-inline-menu tabbable margin-bottom-10";
        if(options.type.equals(Options.Type.blue)){
            tabContainerClass="ver-inline-menu tabbable margin-bottom-10";
        }
        else if(options.type.equals(Options.Type.white)){
            tabContainerClass="nav nav-tabs";
        }

        tabContainer = new WebMarkupContainer("tabContainer");
        add(tabContainer);
        tabContainer.add(new AttributeModifier("class", tabContainerClass));
        tabContainer.setOutputMarkupId(true);
        ListView tab = new ListView("tab", getObject()) {
            @Override
            protected void populateItem(ListItem item) {
                final ITabAjaxProvider obj = (ITabAjaxProvider) item.getDefaultModelObject();
                AjaxLink action = new AjaxLinkImpl("action", obj);
                item.add(action);
                action.add(new IndicatingBehavior());
                WebMarkupContainer icon = new WebMarkupContainer("icon");
                action.add(icon);
                icon.add(new AttributeModifier("class", obj.getIcon()));
                Label title = new Label("title", obj.getTitle());
                action.add(title);
                if (obj.equals(active)) {
                    item.add(new AttributeModifier("class", "active"));
                }
                if(obj.getIntro()!=null) {
                    item.add(new IntroBehavior(obj.getIntro(), obj.getIntroNumber(), obj.getIntroPosition()));
                }
            }

            class AjaxLinkImpl extends IndicatingAjaxLink2 {

                private final ITabAjaxProvider obj;

                public AjaxLinkImpl(String id, ITabAjaxProvider obj) {
                    super(id);
                    this.obj = obj;
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    ITabAjaxProvider activeBack = active;
                    active = obj;
                    
                    boolean onSelect = obj.onSelect(target);
                    if (onSelect == true) {
                        target.add(tabContainer);
                    } else {
                        active = activeBack;
                    }
                    onTabChange(active, target);
                }
            }
        };
        tabContainer.add(tab);
    }

    public void onTabChange(ITabAjaxProvider active, AjaxRequestTarget target) {
        CharSequence newUrl = urlFor(getPage().getClass(), new PageParameters().add("sub", active.getId()));
        target.appendJavaScript(ChangePageUrl.getScript(newUrl.toString()));
    }


    private IModel<List<ITabAjaxProvider>> getObject() {
        return (IModel<List<ITabAjaxProvider>>) getDefaultModel();
    }

    public ITabAjaxProvider getActive() {
        return active;
    }

    @Override
    protected void onBeforeRender() {
        List<ITabAjaxProvider> itabs = (List<ITabAjaxProvider>) getDefaultModelObject();
        String active = getRequest().getRequestParameters().getParameterValue("sub").toString(getDefaultTab().getId());
        TabAjax.ITabAjaxProvider toActive = null;
        if(active != null) {

            for (TabAjax.ITabAjaxProvider itab : itabs) {
                if (active.equals(itab.getId()))
                    toActive = itab;
            }
        }
        if(toActive == null) {
            setResponsePage(getPage().getClass(),getPage().getPageParameters());
        }
        setActive(toActive,true,null);
        super.onBeforeRender();
    }

    public void setActive(ITabAjaxProvider active, boolean reload,AjaxRequestTarget art) {
        this.active = active;
        if (reload) {
            IModel<List<ITabAjaxProvider>> object = getObject();
            for (ITabAjaxProvider iTabAjaxProvider : object.getObject()) {
                if (iTabAjaxProvider.equals(active)) {
                    iTabAjaxProvider.onSelect(art);
                    break;
                }
            }
        }
        if(art!=null){
            art.add(tabContainer);
        }
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return "indicator";
    }

    public void setDefaultTab(ITabAjaxProvider defaultTab) {
        this.defaultTab = defaultTab;
    }

    public ITabAjaxProvider getDefaultTab() {
        if(defaultTab==null){
            defaultTab=active;
        }
        if( defaultTab==null){
            List<ITabAjaxProvider> tabs = (List<ITabAjaxProvider>) getDefaultModelObject();
            if(tabs==null || tabs.isEmpty()){
                return null;
            }
            defaultTab=tabs.get(0);
        }
        return defaultTab;


    }

    public static abstract class ITabAjaxProvider implements Serializable {

        public ITabAjaxProvider() {
        }
        /**
         * 
         * @param target
         * @return true if operation done successfully and there is no problem in changing tab
         */
        public abstract boolean onSelect(AjaxRequestTarget target);

        public abstract IModel<String> getTitle();

        public abstract String getIcon();

        public String getIntro(){
            return null;
        }

        public String getId(){
            return null;
        }

        public int getIntroNumber() {
            return 0;
        }

        public IntroBehavior.Position getIntroPosition() {
            return IntroBehavior.Position.auto;
        }
    }
    public static class Options{
        public Type type;

        public Options(Type type) {
            this.type = type;
        }
        
        public enum Type{
            blue,white;
        }
    }
    static Options defaultOptions= new Options(Options.Type.blue);
}
