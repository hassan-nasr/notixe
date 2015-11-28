/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.friend;

import com.noktiz.domain.entity.UserProperties;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.TabAjax;
import com.noktiz.ui.web.component.TabAjax.Options.Type;
import com.noktiz.ui.web.friend.FriendList;
import com.noktiz.ui.web.friend.OtherFriendList;
import com.noktiz.ui.web.friend.search.SearchFriendPanel;
import com.noktiz.ui.web.google.GoogleSmartFriendPanel;
import java.util.ArrayList;

import com.noktiz.ui.web.friend.fb.FacebookSmartFriendPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author hossein
 */
@AuthorizeInstantiation("USER")
public final class FriendListPage extends BaseUserPage implements com.noktiz.ui.web.auth.AuthenticatedWebPage{
    WebMarkupContainer wmc;

    boolean displayIntroduction;
    public FriendListPage() {
        super("friends");
        wmc= new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        if (!UserProperties.TRUE.equals(getUserInSite().getProperty(UserProperties.FRIEND_INTRODUCTION_SEEN))) {
            getUserInSite().setProperty(UserProperties.FRIEND_INTRODUCTION_SEEN, UserProperties.TRUE);
            displayIntroduction=true;
        }
        else{
            displayIntroduction=false;
        }
        TabAjax.ITabAjaxProvider myFriends = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new FriendList("content", ((UserSession)getSession()).getUser()));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("My.Friends",null);
            }

            @Override
            public String getIcon() {
                return "icon-circle-arrow-down";
            }

            @Override
            public String getIntro() {
                return !displayIntroduction?null:"intro.myFriends";
            }

            @Override
            public int getIntroNumber() {
                return 10;
            }
            @Override
            public String getId() {
                return "myFriends";
            }
        };
        TabAjax.ITabAjaxProvider facebook = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new FacebookSmartFriendPanel("content",((UserSession)getSession()).getUser()));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("My.Facebook.Friends", null);
            }

            @Override
            public String getIcon() {
                return "icon-facebook-sign";
            }
            @Override
            public String getIntro() {
                return !displayIntroduction?null:"intro.facebook";
            }
            @Override
            public int getIntroNumber() {
                return 14;
            }
            @Override
            public String getId() {
                return "facebook";
            }
        };
        TabAjax.ITabAjaxProvider myFriends2 = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new GoogleSmartFriendPanel("content", ((UserSession)getSession()).getUser()));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("My.Gmail.Contact", null);
            }

            @Override
            public String getIcon() {
                return "icon-envelope";
            }
            @Override
            public String getIntro() {
                return !displayIntroduction?null:"intro.gmail";
            }

            @Override
            public int getIntroNumber() {
                return 13;
            }
            @Override
            public String getId() {
                return "gmail";
            }
        };
        TabAjax.ITabAjaxProvider otherFriends = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new OtherFriendList("content", ((UserSession)getSession()).getUser()));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("I'm.Friend.of", null);
            }

            @Override
            public String getIcon() {
                return "icon-circle-arrow-up";
            }
            @Override
            public String getIntro() {
                return !displayIntroduction?null:"intro.friendof";
            }
            @Override
            public int getIntroNumber() {
                return 12;
            }
            @Override
            public String getId() {
                return "imFriendOf";
            }
        };
        TabAjax.ITabAjaxProvider searchFriends = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new SearchFriendPanel("content"));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("Search", null);
            }

            @Override
            public String getIcon() {
                return "icon-search";
            }
            @Override
            public String getIntro() {
                return !displayIntroduction?null:"intro.searchFriend";
            }
            @Override
            public int getIntroNumber() {
                return 15;
            }
            @Override
            public String getId() {
                return "searchFriend";
            }
        };
        ArrayList<TabAjax.ITabAjaxProvider> itabs= new ArrayList<>();
        itabs.add(myFriends);
        itabs.add(otherFriends);
        itabs.add(myFriends2);
        itabs.add(facebook);
        itabs.add(searchFriends);
        TabAjax tabs=new TabAjax("tabs", new Model(itabs), new TabAjax.Options(Type.white));
        tabs.setOutputMarkupPlaceholderTag(false);

        tabs.setActive(myFriends, true, null);
        add(tabs);
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        displayIntroduction=false;
    }
}
