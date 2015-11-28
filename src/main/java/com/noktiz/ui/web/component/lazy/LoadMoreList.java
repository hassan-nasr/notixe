package com.noktiz.ui.web.component.lazy;

import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hasan on 2014-11-05.
 */
public class LoadMoreList<T> extends BasePanel implements Serializable,LazyInitComponent {

    private final ListDataProvider<T> provider;
    private final T fromObj;
    private final Integer fromInt;
    private final Integer count;
    private  Boolean showMoreButton = true;
//    private Boolean showMoreButtonIfEmpty
    boolean isStatic;
    private T nextFrom;
    private final WebMarkupContainer
            morePanel = new WebMarkupContainer("morePanel");
    AjaxLink moreButton;
    public String getTitle(){
        return null;
    }
    boolean removeOld = false;
    WebMarkupContainer notifsWMC;
    ListView notifs;
    public boolean isRemoveOld() {
        return removeOld;
    }

    public void setRemoveOld(boolean removeOld) {
        this.removeOld = removeOld;
    }

    public LoadMoreList(String id, ListDataProvider<T> provider, Integer count, Boolean showMoreButton, Boolean lazy) {
        super(id);
        this.showMoreButton = showMoreButton;
//        if(noMoreMessage == null) {
//            noMoreMessage="That's all";
//        }
//        this.noMoreMessage = noMoreMessage;
        this.isStatic = provider.isStatic();
        this.count = count;
        this.provider = provider;
        fromInt = 0;
        fromObj = null;
        if(!lazy)
            init();
    }

    public LoadMoreList(String id, final ListDataProvider<T> provider, final T fromObj, final Integer count, Boolean lazy) {
        super(id);
        this.provider = provider;
        this.fromObj = fromObj;
        this.count = count;
        fromInt = null;
        isStatic = false;
        if(!lazy)
            init();
    }

    public LoadMoreList(String id, ListDataProvider<T> provider, Integer fromInt, Integer count, Boolean lazy) {
        super(id);
        fromObj = null;
        this.fromInt = fromInt;
        this.count = count;
        this.provider = provider;
        isStatic = true;
        if(!lazy)
            init();
    }

    public void init() {

        final List<? extends T> notificationList;
        if (!isStatic) {
            notificationList = this.provider.getElementsBefore(this.fromObj, this.count);
        } else {
            notificationList = this.provider.getElements(fromInt, this.count);
        }
        setDefaultModel(Model.of(notificationList));
        boolean showMore = showMoreButton && this.provider.hasMore();
        notifsWMC = new WebMarkupContainer("notifsWMC");
        addOrReplace(notifsWMC);
        notifsWMC.setOutputMarkupId(true);
        notifs = new ListView("notifs", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return notificationList;
            }
        }) {
            @Override
            protected void populateItem(ListItem item) {
                Object obj = item.getDefaultModelObject();
                BasePanel panel = LoadMoreList.this.provider.getPanel("notif", (T) obj);
                if (panel != null) {
                    item.add(panel);
                } else {
                    item.setVisible(false);
                }
            }
        };
        notifsWMC.add(notifs);
        addOrReplace(morePanel);
        morePanel.setOutputMarkupId(true);
        WebMarkupContainer morePlaceHolder = new WebMarkupContainer("morePlaceHolder");
        morePanel.add(morePlaceHolder);
        nextFrom = ((notificationList.size() > 0) ? notificationList.get(notificationList.size() - 1) : null);
        moreButton = new IndicatingAjaxLink2("moreButton") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                loadMoreItems(ajaxRequestTarget);
            }
        };
        IModel<String> buttonValue = provider.showMoreCaption();
        moreButton.setBody(buttonValue);
        morePanel.add(moreButton);
        if (!showMore) {
            if(buttonValue!= null && buttonValue.getObject()!=null ) {
//                moreButton.setBody(Model.of(noMoreMessage));
                moreButton.add(new AttributeAppender("class", " disabled"));
                moreButton.setEnabled(false);
                moreButton.setVisible(false); // hiding all invisible buttons
            } else {
                moreButton.setVisible(false);
            }
        }
        if(!showMoreButton){
            moreButton.setVisible(false);
        }
        String title = getTitle();
        addOrReplace(new Label("title",title).setVisible(title!=null));

    }
    public void loadMoreItems(AjaxRequestTarget ajaxRequestTarget) {
        LoadMoreList moreData;
        if (!isStatic) {
            moreData = new LoadMoreList("morePlaceHolder", LoadMoreList.this.provider, nextFrom, LoadMoreList.this.count, false);
            morePanel.addOrReplace(moreData);
        } else {
            moreData = new LoadMoreList("morePlaceHolder", LoadMoreList.this.provider, fromInt + count, LoadMoreList.this.count, false);
            morePanel.addOrReplace(moreData);
        }
        moreButton.setVisible(false);
        ajaxRequestTarget.add(morePanel);
        morePanel.add(new AttributeModifier("style", ""));
        if(removeOld &&((List) moreData.getDefaultModelObject()).size()>0)
        {
            notifs.setVisible(false);
            ajaxRequestTarget.add(notifsWMC);
        }

    }

}
