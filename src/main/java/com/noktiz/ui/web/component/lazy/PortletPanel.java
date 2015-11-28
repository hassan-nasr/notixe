package com.noktiz.ui.web.component.lazy;

import com.noktiz.ui.web.base.IAction;
import com.noktiz.ui.web.base.UserPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by hasan on 2014-12-02.
 */
public class PortletPanel extends UserPanel {
    public PortletPanel(String id, Component body , IModel title){
        this(id,body,title,null,true);
    }
    public PortletPanel(String id, Component body , IModel title,boolean transparentBody) {
        this(id,body,title,null,transparentBody);
    }
    public PortletPanel(String id, Component body , IModel title,List<IAction> actions){
        this(id,body,title,actions,true);
    }
    public PortletPanel(String id, Component body , IModel title,List<IAction> actions,boolean transparentBody) {
        super(id);
        WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);
        if(transparentBody){
            container.add(new AttributeAppender("class"," transparentWhite portlet-body "));
        }
        container.add(body);
        ListView actionsList = new ListView("actions",actions){

            @Override
            protected void populateItem(ListItem item) {
                IAction action=(IAction)item.getDefaultModelObject();
                item.add(new Label("title",action.getActionTitle()));
                WebMarkupContainer icon = new WebMarkupContainer("icon");
                item.add(icon);
                icon.add(new AttributeModifier("class",action.getActionIcon()));
                icon.setVisible(action.getActionIcon()!=null);
                item.add(new AttributeModifier("href",action.getDestinationUrl()));
            }
        };
        add(actionsList);
        add(new Label("title",title));
    }
}
