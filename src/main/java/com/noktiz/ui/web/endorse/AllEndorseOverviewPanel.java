package com.noktiz.ui.web.endorse;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.entity.endorse.EndorseManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;
import java.util.Map;

/**
 * Created by hasan on 2014-10-07.
 */
public class AllEndorseOverviewPanel extends BasePanel {
    EndorseManager endorseManager = new EndorseManager();
    List<Pair> endorseList;
    public AllEndorseOverviewPanel(String id, final User user) {
        super(id);
        Map<String, List<Endorse>> endorses = endorseManager.getMappedEndorses(new UserFacade(user));
        endorseList= new ArrayList<Pair>();
        for (String key : endorses.keySet()) {
            endorseList.add(new Pair(key,endorses.get(key)));
        }
        Label name = new Label("name", user.getName());
        add(name);
        if(endorses.size() !=0) {
            ListView overviewsComponents = new ListView("item", Model.ofList(endorseList)) {
                @Override
                protected void populateItem(ListItem item) {
                    Pair current = (Pair) item.getModelObject();
                    item.add(new EndorseOverviewPanel("eachRate", (String)current.first, user, (List)current.second, 10));
//                item.add(new RateOverviewPanel("eachRate",current));
                }

            };
            add(overviewsComponents);
        }
        else{
            add(new WebMarkupContainer("item").add(new Label("eachRate", new StringResourceModel("emptyEndorseMessage",null).getObject() + " "+  getPrumoteEndorse(user)).setEscapeModelStrings(true)));

        }
    }
    class Pair implements Serializable{

        public Pair(Object first, Object second) {
            this.first = first;
            this.second = second;
        }
        
        Object first,second;
    }

    private String getPrumoteEndorse(User user) {
        if(user.equals(getUserInSite().getUser()))
            return new StringResourceModel("promoteSelf",null).getObject();
        if(getUserInSite().doIOwnerTheFriendshipOf(new UserFacade(user)))
            return  new StringResourceModel("promoteFriend", null, new Object[]{user.getName()}).getObject();
        return "";
    }
}
