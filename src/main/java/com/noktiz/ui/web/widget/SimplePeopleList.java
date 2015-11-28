package com.noktiz.ui.web.widget;

import com.noktiz.domain.entity.User;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasan on 2014-11-06.
 */
public class SimplePeopleList extends BasePanel {

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(this.getClass(), "SimplePeopleList.css")));
    }

    public SimplePeopleList(String id, List<User> userList, int explicitNum , boolean enableAll , String title) {
        super(id);
         if(title == null)
             title="";
        List<User> explicit = new ArrayList<>();
        List<User> hidden = new ArrayList<>();
        add(new Label("title",title));

//        for (int i = 0; i < 10; i++) {
//            hidden.addAll(userList);
//        }
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            if(i>=explicitNum)
                hidden.add(user);
            else
                explicit.add(user);
        }
        ListView explicitView= new ListView("peopleEx",explicit) {
            @Override
            protected void populateItem(ListItem item) {
                User  user = (User) item.getModelObject();
                ExternalLink link = new ExternalLink("link", RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid", user.getId())).toString());
                item.add(link);
                link.add(new Label("name",user.getName()));
            }
        };
        add(explicitView);
        final String randomClass = "CS"+RandomStringUtils.random(10,false,true);
        ListView hiddenView= new ListView("peopleHi",hidden) {
            @Override
            protected void populateItem(ListItem item) {
                User  user = (User) item.getModelObject();
                ExternalLink link = new ExternalLink("link", RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid", user.getId())).toString());
                item.add(link);
                link.add(new Label("name",user.getName()));
                item.add(new AttributeAppender("class"," "+ randomClass + " s "));
            }

        };
        add(hiddenView);
        WebMarkupContainer showMoreButton = new WebMarkupContainer("showMore");
        add(showMoreButton);
        showMoreButton.add(new AttributeAppender("onclick", "$('." + randomClass + ".s').css('display','block');$('." + randomClass + ".h').css('display','none')"));
        showMoreButton.add(new AttributeAppender("class", " " + randomClass + " h"));
        WebMarkupContainer showLessButton = new WebMarkupContainer("showLess");
        add(showLessButton);
        showLessButton.add(new AttributeAppender("onclick", "$('." + randomClass + ".h').css('display','block');$('." + randomClass + ".s').css('display','none')"));
        showLessButton.add(new AttributeAppender("class"," "+randomClass+" s"));
        if(hidden.size()==0 || enableAll == false)
            showMoreButton.setVisible(false);
    }
}
