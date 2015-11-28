package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.component.TabAjax;
import com.noktiz.ui.web.rate.context.RateContextsManagePanel;
import com.noktiz.ui.web.utils.ChangePageUrl;
//import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;

/**
 * Created by hasan on 9/26/14.
 */

@AuthorizeInstantiation("USER")
public class RatingsPage extends BaseUserPage {
    RateContextManager rateContextManager = new RateContextManager();
//    @Override
//    public void renderHead(IHeaderResponse response) {
//        super.renderHead(response);
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/bootstrap-editable/bootstrap-editable/css/bootstrap-editable.css")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/bootstrap-editable/bootstrap-editable/css/bootstrap-editable.js")));
//    }

    boolean displayIntroduction;
    WebMarkupContainer wmc;
    public RatingsPage() {
        super("rating");
//        add(new BookmarkablePageLink("manage", RateContextManagePage.class));
        if (!UserProperties.TRUE.equals(getUserInSite().getProperty(UserProperties.RATE_INTRODUCTION_SEEN))) {
            getUserInSite().setProperty(UserProperties.RATE_INTRODUCTION_SEEN, UserProperties.TRUE);
            displayIntroduction=true;
        }
        else{
            displayIntroduction=false;
        }
        wmc= new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        TabAjax.ITabAjaxProvider myRatings = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new RatingsDonePanel("content"));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("My.Answers", null);
            }

            @Override
            public String getIcon() {
                return "icon-chevron-up";
            }

            @Override
            public String getId() {
                return "ratings";
            }

            @Override
            public int getIntroNumber() {
                return 11;
            }

            @Override
            public String getIntro() {
                return !displayIntroduction?null:"intro.ratings";
            }
        };
        TabAjax.ITabAjaxProvider myRatingContexts = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new RateContextsManagePanel("content",getUserInSite(), false));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("Questions", null);
            }

            @Override
            public String getIcon() {
                return "icon-question";
            }
            @Override
            public String getId() {
                return "manage";
            }
            @Override
            public int getIntroNumber() {
                return 9;
            }

            @Override
            public String getIntro() {
                return !displayIntroduction?null: "intro.manage";
            }
        };
        TabAjax.ITabAjaxProvider myRates = new TabAjax.ITabAjaxProvider() {

            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                wmc.addOrReplace(new RatingsOfDetailedPanel("content"));
                if(target!=null){
                    target.add(wmc);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("Others.Answers", null);
            }

            @Override
            public String getIcon() {
                return "icon-chevron-down";
            }

            @Override
            public String getId() {
                return "rates";
            }
            @Override
            public int getIntroNumber() {
                return 10;
            }

            @Override
            public String getIntro() {
                return !displayIntroduction?null: "intro.rates";
            }
        };

        ArrayList<TabAjax.ITabAjaxProvider> itabs= new ArrayList<>();
        itabs.add(myRatingContexts);
        itabs.add(myRates);
        itabs.add(myRatings);


        TabAjax tabs=new TabAjax("tabs", new Model(itabs), new TabAjax.Options(TabAjax.Options.Type.white));
        tabs.setOutputMarkupPlaceholderTag(false);
//        String active = getRequest().getRequestParameters().getParameterValue("sub").toString(itabs.get(0).getId());
//        TabAjax.ITabAjaxProvider toActive = null;
//        if(active != null)
//        for (TabAjax.ITabAjaxProvider itab : itabs) {
//            if(active.equals(itab.getId()))
//                toActive = itab;
//        }
//        if(toActive == null) {
//            setResponsePage(RatingsPage.class);
//        }
//        tabs.setActive(toActive,true,null);
        add(tabs);
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        displayIntroduction=false;
    }
}
