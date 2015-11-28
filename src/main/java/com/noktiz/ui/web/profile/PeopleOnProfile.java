package com.noktiz.ui.web.profile;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.user.SingleUserImageWithLinkPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.List;


/**
 * Created by hasan on 2014-10-30.
 */
public class PeopleOnProfile extends org.apache.wicket.markup.html.panel.Panel {

    public PeopleOnProfile(String id, List<User> user, final int explicitView, String titleString, final int imageSize) {
        super(id);
        Label title = new Label("title",titleString);
        add(title);
        ListView userList = new ListView("items", user) {
            int index=-1;
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                index =-1;
            }
            @Override
            protected void populateItem(ListItem item) {
                User friend = (User) item.getModelObject();
                if( (index>=explicitView))
                {
                    WebMarkupContainer temp = new WebMarkupContainer("item");
                    item.add(temp);
                    temp.setVisible(false);
                    return;
                }
                /*if(index== explicitView ){
                    StringBuilder names=new StringBuilder();
                    for (int i = index; i < current.size(); i++) {
                        Endorse endorse1 = current.get(i);
                        names.append(endorse1.getSender().getName()).append(", ");
                    }
                    Image image = new Image("img", new PackageResourceReference(EndorseOverviewPanel.class,"dots.jpg"));
                    item.add(image);

                    image.add(new AttributeModifier("data-content", names));
                    return;
                }
*/
                item.add(new SingleUserImageWithLinkPanel("item",new UserFacade(friend),imageSize)) ;
                /*Image image = new Image("img", "");
                item.add(image);

                String imageUrl = RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                        ImageManagement.getUserImageParameter(new UserFacade(friend), ImageManagement.ImageSize.small)).toString();
                image.add(new AttributeModifier("src", imageUrl));
                image.add(new AttributeAppender("style",";width: "+imageSize+"px"));
                image.add(new AttributeModifier("data-content", friend.getName()));*/
            }
        };
        add(userList);
        AjaxLink showAll = new IndicatingAjaxLink2("showAll") {
            @Override
            public void onClick(AjaxRequestTarget target) {
            }
        };
        add(showAll);
        if(explicitView >= user.size()) {
            showAll.setVisible(false);
        }
    }
}
