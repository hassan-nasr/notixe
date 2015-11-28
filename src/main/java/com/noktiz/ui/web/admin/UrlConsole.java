package com.noktiz.ui.web.admin;

import com.noktiz.domain.entity.rate.PredefinedQuestionManager;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.BaseUserPage;
import com.noktiz.ui.web.home.HomePage;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Created by hasan on 9 February 15.
 */
public class UrlConsole extends BaseUserPage {
    public UrlConsole() {
        super("console");
        if(!(getUserInSite().getUser().getId() == SystemConfigManager.getCurrentConfig().getPropertyAsLong("SelfUserId")))
            setResponsePage(HomePage.class);
        String result = "no command matched";
        String command = getRequest().getRequestParameters().getParameterValue("command").toString("");
        if(command.equals("SystemConfigReload")){
            SystemConfigManager.reload();
            result = "SystemConfig reloaded";
        }
        if(command.equals("ReindexPredefinedQuestions")){
            new PredefinedQuestionManager().updateAllSolrIndex();
            result = "predefined question reindexed";
        }
        add(new Label("result",result));
    }
}
