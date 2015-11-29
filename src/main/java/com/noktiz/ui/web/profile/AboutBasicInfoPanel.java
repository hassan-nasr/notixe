package com.noktiz.ui.web.profile;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.domain.social.facebook.FacebookUtils;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.TooltipBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Created by hasan on 2014-10-15.
 */
public class AboutBasicInfoPanel extends BasePanel {

    private final UserFacade userFacade;
    NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "./assets/css/style.css")));
    }

    public AboutBasicInfoPanel(String id, UserFacade userFacade) {
        super(id);
        add(feedback);
        feedback.setOutputMarkupId(true);
        this.userFacade = userFacade;
        Image profilePicture = new Image("profilePicture", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(userFacade, ImageManagement.ImageSize.large));
        add(profilePicture);
        Label nameLabel = new Label("nameLabel", userFacade.getName());
        add(nameLabel);
        showVerifiedWith(userFacade);
        showAddFriend(userFacade);
        Long thanksUser = userFacade.getScores().getThanksUserNotExact();
        Long thanksCount = userFacade.getScores().getThanksCountNotExact();
        if (thanksUser == 0) {
            Label tc = new Label("thankCount");
            tc.setVisible(false);
            Label tu = new Label("userCount");
            add(tu, tc);
        } else {
            Label tc = new Label("thankCount", "+" + thanksCount);
            Label tu = new Label("userCount", "+" + thanksUser);
            add(tu, tc);
        }
        if (getUserInSite().getUser().getId() != null && !getUserInSite().equals(userFacade))
            add(new Label("mutualTrust", getMutualTrustedFriedsCountString(userFacade)));
        else
            add(new Label("mutualTrust").setVisible(false));
    }

    public String getMutualTrustedFriedsCountString(UserFacade userFacade) {
        return new StringResourceModel("mutualTrustCountMessage", null, new Object[]{Friendship.getMutualTrustedFriendsCount(userFacade.getUser(), getUserInSite().getUser())}).getObject();
    }

    private void showAddFriend(final UserFacade userFacade) {
        final WebMarkupContainer action = new WebMarkupContainer("action");
        add(action);
        action.setOutputMarkupId(true);
        //add Action
        AjaxLink addBack = new IndicatingAjaxLink2("addBack") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getUserInSite().addFriend(userFacade);
                this.setEnabled(false);
                this.replace(new Label("addBackMessage", getString("Done")));
                target.add(action);
            }
        };
        action.add(addBack);
        String message;
        message = "add " + userFacade.getGender().getObject();
        Label label = new Label("addBackMessage", message);
        addBack.add(label);
        if (!getUserInSite().doIOwnerTheFriendshipOf(userFacade) && getUserInSite().getUser().getActive() &&
                !userFacade.getUser().equals(getUserInSite().getUser())) {
            addBack.setVisible(true);
        } else {
            addBack.setVisible(false);
        }
        // Un Follow
        AjaxLink follow = new IndicatingAjaxLink2("follow") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Result result = getUserInSite().toggleFollow(userFacade);
                result.displayInWicket(this);
                target.add(feedback);
                target.add(action);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                if(getUserInSite().getUser().getActive()==false){
                    setVisible(false);
                    return;
                }
                Friendship friendship = userFacade.getFriendship(getUserInSite());
                setVisible(friendship!=null);
                if(friendship!=null){
                    if(friendship.getDontFollow()) {
                        setBody(Model.of(getString("follow.again")));
                    }
                    else{
                        setBody(Model.of(getString("unfollow")));
                    }
                }
            }
        };
        action.add(follow);
        follow.add(new TooltipBehavior(getString("unfollow.intro"), TooltipBehavior.Position.bottom));
    }

    private void showVerifiedWith(UserFacade userFacade) {
        if (getUserInSite().doIHaveAccessToProtectedInfo(userFacade)) {
            Label email = new Label("email", userFacade.getEmail());
            add(email);
        } else
            add(new WebMarkupContainer("email").setVisible(false));
        if (userFacade.getCredential().getFacebookInfo().getFacebook_id() != null) {
            ExternalLink facebookLink = new ExternalLink("facebookLink", FacebookUtils.getProfileLink(userFacade));
            add(facebookLink);
        } else {
            WebMarkupContainer facebookLink = new WebMarkupContainer("facebookLink");
            facebookLink.setVisible(false);
            add(facebookLink);
        }
    }

    @Override
    public String getPageTitle() {
        return new StringResourceModel("profileTitle", null, new Object[]{userFacade.getName()}).getObject();
    }
}
