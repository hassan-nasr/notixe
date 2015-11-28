package com.noktiz.ui.web.profile;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.access.PrivacyLevel;
import com.noktiz.domain.entity.access.TempPrivacy;
import com.noktiz.domain.entity.rate.NotificationRateInvite;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.component.lazy.LazyLoadPanel2;
import com.noktiz.ui.web.component.lazy.LoadMoreList;
import com.noktiz.ui.web.component.lazy.PortletPanel;
import com.noktiz.ui.web.home.HomePage;
import com.noktiz.ui.web.endorse.AllEndorseOverviewPanel;
import com.noktiz.ui.web.endorse.AllNewEndorsesPanel;
import com.noktiz.ui.web.endorse.EndorseMePanel;
import com.noktiz.ui.web.rate.RateInviteProvider;
import com.noktiz.ui.web.rate.overview.AllRateOverviewPanel;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Created by hasan on 2014-10-07.
 */
public class ProfilePage extends BaseUserPage{


    AllRateOverviewPanel ratePanel;

    public ProfilePage() {
        super("profile".equals(0)?"sdf":"dsf");

        Long userid = getRequest().getRequestParameters().getParameterValue("userid").toLong(-1l);
        User userToView;
        if(userid == -1){
            userToView = getUserInSite().getUser();
        }
        else
            userToView = User.load(userid);
        if(userToView == null || userToView.getId()==null){
            setResponsePage(HomePage.class);
            return;
        }

        UserFacade userToViewFacade = new UserFacade(userToView);
//        ratePanel = new AllRateOverviewPanel("ratesOfPanel",userToView);
//        add(ratePanel);

        add(new AboutBasicInfoPanel("aboutBasicInfo",userToViewFacade));
        add(new ProtectedPanel("newEndorse",new EndorseMePanel("content",userToView),new TempPrivacy(userToView, PrivacyLevel.Friends),"",false,true));
        if(userToView.equals(getUserInSite().getUser())){
             add(new AllNewEndorsesPanel("newEndorsesToAdd"));
        }
        else
            add(new WebMarkupContainer("newEndorsesToAdd").setVisible(false));
        add(new AllEndorseOverviewPanel("endorseOverview",userToView));
//        add(new WebMarkupContainer("endorseOverview"));
        
//        List<User> othersFriends = new ArrayList<>();
//        for (Friendship friendship : userToView.getOthersFriends()) {
//            othersFriends .add(friendship.getFriendshipOwner());
//        }
//        for (int i = 0; i < 20; i++) {
//           othersFriends.add(othersFriends.get(0));
//        }
//        add(new PeopleOnProfile("friendsTrustMe", othersFriends,20, "Trusted By:",100));
        LoadMoreList friendsTrustMe = new LoadMoreList("content", new FriendsOfDataProvider(userToViewFacade) {
            public IModel<String> showMoreCaption() {

                return new AbstractReadOnlyModel() {

                    @Override
                    public Object getObject() {
                        if (hasMore()) {
                            return new StringResourceModel("ShowMore", null).getObject();
                        }
                        return null;
                    }
                };
            }
        }, 10, false, true) {
            @Override
            public String getTitle() {
                return new StringResourceModel("TrustedBy", null).getObject();
            }
        };
        add(new LazyLoadPanel2("friendsTrustMe",friendsTrustMe));
        add(new ProtectedPanel("rateInvitesForMe",new PortletPanel("content",new LazyLoadPanel2("content",new LoadMoreList<NotificationRateInvite>
        ("content",new RateInviteProvider(getUserInSite(),userToViewFacade, RateInviteProvider.UpdateListLoadType.All),2, true, true)),
                new StringResourceModel("QuestionsforYou", null),false),new TempPrivacy(userToView, PrivacyLevel.Friends),"",false,true));

    }

    @Override
    protected String getBodyStyleClass() {
//        if(true)
//            return "";
        return "background";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ProfilePage.class,"profile.css")));
        super.renderHead(response);
    }
}
