/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author Hossein
 */
public abstract class FlexibleDataView<T> extends Panel implements IPageable {

    private long currentPage = 0;
    private boolean reload = true;
    private long pageSize;
    IDataListProvider<T> dataProvider;
    public ListView list;

    public FlexibleDataView(String id, IDataListProvider<T> dataprovider, long pageSize) {
        super(id);
        this.pageSize = pageSize;
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be grater than 0");
        }
        this.dataProvider = dataprovider;
        list = new ListView("listview", new AbstractReadOnlyModel<List<T>>() {
            
            @Override
            public List getObject() {
                List<? extends T> iterator= new ArrayList<>();
                if (reload) {
                    reload = false;
                    iterator = dataProvider.iterator(getBeginNumber(), getPageSize());
                }
                return iterator;
            }

            private long getBeginNumber() {
                return currentPage * getPageSize();
            }
        }) {

            @Override
            protected void populateItem(ListItem item) {
                FlexibleDataView.this.populateItem(item);
            }

            @Override
            protected void onAfterRender() {
                reload = true;
            }

        };
        add(list);
    }

    @Override
    public long getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(long page) {
        currentPage = page;
    }

    @Override
    public long getPageCount() {
        return (long) Math.ceil((dataProvider.size() + 0.0) / (pageSize + 0.0));
    }

    public Long getPageSize() {
        return pageSize;
    }
    
    protected abstract void populateItem(ListItem item);

}
