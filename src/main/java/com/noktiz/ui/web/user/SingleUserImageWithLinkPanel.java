package com.noktiz.ui.web.user;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * Created by hasan on 2014-11-13.
 */
public class SingleUserImageWithLinkPanel extends BasePanel {
    public SingleUserImageWithLinkPanel(String id, UserFacade user, int imageSize) {
        super(id);
        org.apache.wicket.request.mapper.parameter.PageParameters url = new org.apache.wicket.request.mapper.parameter.PageParameters().add("userid",user.getUser().getId());
        if(user.isAnonymous()){
            url.remove("userid");
        }
        Link link = new BookmarkablePageLink("link",ProfilePage.class, url);
        if(user.isAnonymous())
            link.setEnabled(false);
        add(link);
        String imageUrl = RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(user, ImageManagement.ImageSize.small)).toString();
        Image image = new Image("img", "");
        link.add(image);

        image.add(new AttributeModifier("src", imageUrl));
        image.add(new AttributeAppender("style",";width: "+imageSize+"px"));
        image.add(new AttributeModifier("data-content", user.getName()));
    }
}
