package com.noktiz.ui.web.component.infobox;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hossein on 12/6/2014.
 */
public abstract class InfoBoxProvider implements Serializable {
    private List actions = new ArrayList<>();
    private String alignmentClass;

    public InfoBoxProvider(){
        alignmentClass=null;
    }

    public void setAlignmentClass(String alignmentClass) {
        this.alignmentClass = alignmentClass;
    }

    public abstract String getImageUrl();

    public boolean getImageEnable(){
        return true;
    };
    public abstract String getTitleResourceKey();

    public abstract String getTextResourceKey();

    public List<IAction> getActions() {
        return actions;
    }

    public String getAlignmentClass() {
        return alignmentClass;
    }


    public static abstract class IAction implements Serializable{
        public abstract IModel<String> getActionTitle();

        public boolean isActionEnabled() {
            return true;
        }

        /**
         * @param art
         * @return
         */
//        public abstract boolean onAction(AjaxRequestTarget art, Component caller);

        public IModel<String> getButtonClass() {
            return Model.of(" red ");
        }

        public abstract WebMarkupContainer getLink(String id);
    }
}
