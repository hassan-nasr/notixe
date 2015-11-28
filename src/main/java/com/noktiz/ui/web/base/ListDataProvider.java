package com.noktiz.ui.web.base;

import com.noktiz.ui.web.BasePanel;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Created by hasan on 2014-11-24.
 */
public abstract class ListDataProvider<T> implements Serializable {

    private boolean isStaticVar = true;

    public ListDataProvider(boolean isStaticVar) {
        this.isStaticVar = isStaticVar;
    }

    abstract public List<? extends T> getElements(Integer From, Integer count);

    public abstract BasePanel getPanel(String name, T obj);

    public List<? extends T> getElementsBefore(T From, Integer count) {
        throw new NotImplementedException();
    }
    
    public abstract boolean hasMore();

    public IModel<String> showMoreCaption() {
//TODO:i18n
        return new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                if (hasMore()) {
                    return new StringResourceModel("ShowMore", null).getObject();
                }
                return new StringResourceModel("noMore", null).getObject();
            }
        };
    }

    public boolean isStatic() {
        return isStaticVar;
    }
;

}
