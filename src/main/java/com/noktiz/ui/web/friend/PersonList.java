/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.behavior.SearchBehavior;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.interfaces.ISearchable;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel2;
import com.noktiz.ui.web.component.lazy.LoadMoreList;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

/**
 *
 * @author Hossein
 */
public final class PersonList extends Panel implements ISearchable {

    private final Integer count;
    private final Boolean showloadMoreButton;
    WebMarkupContainer wmc;
    String searchContent = " searchContent ";
    String searchContentClass = "searchContent";
    Form form;
    LoadMoreList list;
    NotificationFeedbackPanel feedback;
    WebMarkupContainer feedbackwmc;

    /**
     *  @param noMoreMessage null means default and empty String means nothing
     */
    public PersonList(String id, IModel<? extends PersonListProvider> personListProvider, String emptyMessage, Integer count, String noMoreMessage, Boolean loadMoreButton) {
        super(id, personListProvider);
        this.count = count;
        showloadMoreButton = loadMoreButton;
//        searchContent = searchContent + RandomStringUtils.randomAlphabetic(2) + " ";
//        searchContentClass=searchContent.trim();
        form = new Form("form");
        add(form);
        form.setOutputMarkupId(true);
        wmc = new WebMarkupContainer("wmc");
        form.add(wmc);
        feedbackwmc = new WebMarkupContainer("feedbackwmc");
        wmc.add(feedbackwmc);
        feedback = new NotificationFeedbackPanel("feedback");
        feedbackwmc.add(feedback);
        feedbackwmc.setOutputMarkupId(true);
        TextField search = new TextField("search", Model.of("")) {
            @Override
            public void onConfigure() {
                super.onConfigure();
                setVisible(getObject().isSearchEnable());
            }
        };
        wmc.add(search);

        search.add(new SearchBehavior(this));
        ListView generalActions = new ListView("generalActions", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return PersonList.this.getObject().getActions().getObject();
            }
        }) {
            @Override
            protected void populateItem(ListItem item) {
                final PersonListProvider.IAction iAction = (PersonListProvider.IAction) item.getDefaultModelObject();
                AjaxSubmitLink action;
                action = new IndicatingAjaxSubmitLink2("generalAction") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        boolean onAction = iAction.onAction(target, PersonList.this);
                        if (onAction) {
                            target.add(form);
                        }
                        target.add(feedbackwmc);
                    }

                    @Override
                    public void onConfigure() {
                        super.onConfigure();
                        setEnabled(iAction.isActionEnabled());
                    }
                };
                item.add(action);
                Label l = new Label("generalActionTitle", iAction.getActionTitle());
                action.add(l);
                action.add(new AttributeAppender("class", iAction.getButtonClass()));
            }
        };

        wmc.add(generalActions);
        wmc.setOutputMarkupId(true);
        WebMarkupContainer lazyList = createList();
        wmc.add(lazyList);
//        if(((List) this.list.getDefaultModelObject()).size()>0)
            wmc.add(new Label("emptyMessage"));
//        else {
//            wmc.add(new Label("emptyMessage", emptyMessage));
//        }
    }

    public WebMarkupContainer createList() {
        list = new LoadMoreList("content", getObject(), count, showloadMoreButton, true){
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                String markupId = wmc.getMarkupId();
                response.render(OnDomReadyHeaderItem.forScript("tileResize('"+markupId+"');"));
            }
        };
        return new LazyLoadPanel2("list", list);
    }

    public void refresh(AjaxRequestTarget art, boolean all) {
        WebMarkupContainer lazyList = createList();
        wmc.addOrReplace(lazyList);
        if (art != null) {
            art.add(wmc);
//            wmc.removeAll();
        }
        if (all) {
            list.removeAll();
        }
    }

    private PersonListProvider getObject() {
        return (PersonListProvider) getDefaultModelObject();
    }

    @Override
    public String getSearchElementClass() {
        return "searchElement";
    }

    @Override
    public String getSearchContentClass() {
        return searchContentClass;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String containerId=wmc.getMarkupId();
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(PersonList.class, "PersonList.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/star-rating/css/star-rating.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/bootstrap/star-rating/js/star-rating.js")));

//        response.render(CssHeaderItem.forReference(new PackageResourceReference(PersonList.class, "PersonList.css")));
        response.render(OnDomReadyHeaderItem.forScript("setTimeout(function(){enableTileResize('"+containerId+"')},1000)"));
    }
    public void loadMoreItems(AjaxRequestTarget ajaxRequestTarget){
        list.loadMoreItems(ajaxRequestTarget);
    }
}
