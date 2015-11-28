/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.notifications;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.NotificationAddFriend;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel2;
import com.noktiz.ui.web.notifications.add.ConfirmAddFriend;
import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author Hossein
 */
public final class NotificationPanelAddFriend extends BaseNotificationPanel {

    public NotificationPanelAddFriend(String id, IModel<NotificationAddFriend> obj) {
        super(id, obj);
        final User friend = obj.getObject().getFriend();
        final User owner = obj.getObject().getOwner();

        AjaxLink addBack=null;
        Link<Object> viewProfile = null;
        final IModel<String> actionTitle = new Model<>(getString("ViewProfile"));

//        WebMarkupContainer modal = new WebMarkupContainer("modal");
//        add(modal);
//        modal.setOutputMarkupId(true);
        if (Friendship.load(owner, friend) == null) {

//            LazyLoadPanel confirmAddFriendLazy = new LazyLoadPanel("modalContent", new ConfirmAddFriend("content", new UserFacade(friend)));
//            modal.add(confirmAddFriendLazy);

            addBack = new AjaxLink("action1") {
//                @Override
//                public void renderHead(IHeaderResponse response) {
//                    response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/bootstrap-modal/js/bootstrap-modal.js")));
//                    response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/bootstrap-modal/js/bootstrap-modalmanager.js")));
//                    response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "/assets/plugins/bootstrap-modal/css/bootstrap-modal.css")));
//                }
//
//                @Override
//                protected void onBeforeRender() {
//                    super.onBeforeRender();
//                    add(new AttributeModifier("href", "#" + modal.getMarkupId()));
//                    add(new AttributeModifier("data-toggle", "modal"));
//                }
                @Override
                public void onClick(AjaxRequestTarget target) {
//                    confirmAddFriendLazy.load(target);
                    new UserFacade(owner).addFriend(new UserFacade(friend));
                    obj.getObject().setSeen(true);
                    obj.getObject().update();
                    this.setEnabled(false);
                    actionTitle.setObject("done");
                    target.add(getParent());
                }
            };
            viewProfile = new BookmarkablePageLink<>("action1", ProfilePage.class,new PageParameters().add("userid",friend.getId()));
//                actionTitle.setObject("add " + friend.getGender().getObject()+ " back");


        }
        else{
//            modal.setVisible(false);
        }
        IModel<String> message =  new StringResourceModel("friend.add.message", obj, new Object[]{
            new PropertyModel(obj, "friend.name"),
            new AbstractReadOnlyModel<Object>() {
                @Override
                public Object getObject() {
                    return friend.getGender().getObject();
                }
            }
        });
        IModel<String> title = new StringResourceModel("friend.add.title", null, new Object[]{
                new PropertyModel(obj, "friend.name")
            });

        Image img = new Image("photo", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(new UserFacade(obj.getObject().getFriend()), ImageManagement.ImageSize.medium));


                init(title, message, img, null, viewProfile, actionTitle);
    }
}
