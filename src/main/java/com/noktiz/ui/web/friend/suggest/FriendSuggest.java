package com.noktiz.ui.web.friend.suggest;

import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.friend.FriendListPage;
import com.noktiz.ui.web.component.infobox.SimpleInfoBoxWithImage;
import com.noktiz.ui.web.component.lazy.LazyInitComponent;
import com.noktiz.ui.web.component.lazy.LoadMoreList;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.infobox.SimpleInfoBoxProvider;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;
import java.util.Locale;

/**
 * Created by hasan on 2014-12-19.
 */
public class FriendSuggest extends BasePanel implements LazyInitComponent{

    public FriendSuggest(String id) {
        super(id);
    }

    @Override
    public void init() {
        setOutputMarkupId(true);
        new SocialConnectionManager().lookForNewFriends(getUserInSite());
        LoadMoreList<SocialConnection> friends = new LoadMoreList<SocialConnection>("friends", new SuggestedFriendDataProvider(getUserInSite()), 2, true, false){
            @Override
            public void renderHead(IHeaderResponse response) {
                String markupId = FriendSuggest.this.getMarkupId();
                response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(PersonList.class, "PersonList.js")));
                response.render(OnDomReadyHeaderItem.forScript("tileResize('"+markupId+"');"));
                response.render(OnDomReadyHeaderItem.forScript("setTimeout(function(){enableTileResize('"+markupId+"')},1000)"));
            }
        };

        friends.setRemoveOld(true);
        if(((List)friends.getDefaultModelObject()).size()>0 )
        {
            add(friends);
        }else {
            SimpleInfoBoxProvider provider = new SimpleInfoBoxProvider(null, "inviteTitle", "inviteText");
            provider.addAction(new SimpleInfoBoxProvider.IAction() {
                @Override
                public IModel<String> getActionTitle() {
                    return Model.of(getString("InviteFriends"));
                }

                @Override
                public WebMarkupContainer getLink(String id) {
                    return new BookmarkablePageLink(id, FriendListPage.class,new PageParameters().add("sub","searchFriend"));
                }
            });
            provider.setImageEnable(false);
            add(new SimpleInfoBoxWithImage("friends", provider));
        }
    }
}
