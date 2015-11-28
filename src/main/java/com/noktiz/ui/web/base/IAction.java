package com.noktiz.ui.web.base;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;

/**
 * Created by Hossein on 4/23/2015.
 */
public abstract class IAction implements Serializable {
    final static Model empty= new Model("");
    public IModel<String> getActionTitle(){
        return new Model<>("");
    }
    public String getActionIcon(){
        return null;
    }
    public abstract String getDestinationUrl();
}
