package com.noktiz.ui.web.error;

import com.noktiz.ui.web.BasePage;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.base.IAction;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hossein on 4/28/2015.
 */
public class BaseErrorPage extends BaseUserPage {
    private IModel<String> title;
    private IModel<String> body;
    private ArrayList<IAction> actions;

    public BaseErrorPage() {
        super(null);
    }

    public IModel<String> getTitle() {
        return title;
    }

    public IModel<String> getBody() {
        return body;
    }

    public ArrayList<IAction> getActions() {
        return actions;
    }




    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("title",getTitle()));
        add(new MultiLineLabel("body_",getBody()));
        ListView actionsList = new ListView("action", getActions()) {
            @Override
            protected void populateItem(ListItem item) {
                IAction action = (IAction) item.getDefaultModelObject();
                item.add(new Label("title",action.getActionTitle()));
                item.add(new AttributeModifier("href",action.getDestinationUrl()));
            }
        };
        add(actionsList);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }
    @Override
    public boolean isErrorPage() {
        return true;
    }
}
