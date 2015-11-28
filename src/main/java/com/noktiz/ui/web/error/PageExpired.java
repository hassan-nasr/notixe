package com.noktiz.ui.web.error;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.base.IAction;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;

import java.util.ArrayList;

/**
 * Created by Hossein on 4/28/2015.
 */
public class PageExpired extends BaseErrorPage {
    public PageExpired() {
        super();
    }
    @Override
    public IModel<String> getTitle() {
        return new StringResourceModel("pageExpiredTitle",null);
    }

    @Override
    public IModel<String> getBody() {
        return new StringResourceModel("pageExpiredBody",null);
    }

    @Override
    public ArrayList<IAction> getActions() {
        ArrayList<IAction> objects = new ArrayList<>();
        objects.add(new IAction() {
            @Override
            public String getDestinationUrl() {
                return urlFor(Application.get().getHomePage(),null).toString();
            }

            @Override
            public IModel<String> getActionTitle() {
                return  new StringResourceModel("return_home_page",null);
            }
        });
        return objects;
    }
    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);
        response.setStatus(200);
    }
}
