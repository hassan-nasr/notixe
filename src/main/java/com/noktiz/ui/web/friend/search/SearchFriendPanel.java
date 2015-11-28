package com.noktiz.ui.web.friend.search;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.facebook.FacebookFriendProvider;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.friend.fb.SocialFriendListViewer;
import com.noktiz.ui.web.social.FacebookConnect;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Created by hasan on 9/5/14.
 */
public class SearchFriendPanel extends UserPanel {

    WebMarkupContainer wmc;
    private String queryString;
    Form form ;
    public SearchFriendPanel(String id) {
        super(id);
        wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.add(new WebMarkupContainer("searchResult").setVisible(false));
        wmc.setOutputMarkupId(true);
        form = new Form("form");
        add(form);
        TextField<String> query = new TextField<String>("query", new IModel<String>() {
            @Override
            public String getObject() {
                return queryString;
            }

            @Override
            public void setObject(String s) {
                queryString=s;
            }

            @Override
            public void detach() {

            }
        });
        form.add(query);
        IndicatingAjaxSubmitLink2 submit = new IndicatingAjaxSubmitLink2("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if(queryString ==null || queryString.isEmpty())
                    queryString="*";
                showFriends(getUserInSite(),queryString,target);
            }
        };
        form.add(submit);
        form.setDefaultButton(submit);

    }

    private void showFriends(UserFacade user, String query, AjaxRequestTarget target) {
        Panel friendsPanel = new PersonList("searchResult", Model.of(new SearchFriendProvider(query,getUserInSite())), null, 200, null, true);
        wmc.addOrReplace(friendsPanel);
        if(target!=null) {
            target.add(wmc);
        }
    }
}

