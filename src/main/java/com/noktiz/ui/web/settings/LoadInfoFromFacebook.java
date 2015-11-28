package com.noktiz.ui.web.settings;

import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.facebook.FacebookUtils;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.social.FacebookConnect;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hasan on 9/16/14.
 */
public class LoadInfoFromFacebook extends UserPanel {
    FacebookConnect facebookConnect = null;
    IndicatingAjaxLink2 getDataFromFacebookButton = null;
    UserFacade userFacade= null;
    boolean firstRun = true;
    private IModel<MarkupContainer> toUpdate;

    public LoadInfoFromFacebook(String id, IModel<MarkupContainer> toUpdate) {
        super(id);
        userFacade = getUserInSite();
        this.toUpdate = toUpdate;
        final UserFacade userFacade = getUserInSite();
        List<String> permissions = Arrays.asList("public_profile");
        getDataFromFacebookButton=  new IndicatingAjaxLink2("getDataFromFacebookButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                doAction(target);
            }
        };
        add(getDataFromFacebookButton);
        getDataFromFacebookButton.setVisible(false);
        facebookConnect= new FacebookConnect("facebookConnect",userFacade,permissions,getString("getDataFromFacebook"),null,false){
            @Override
            public void onAccessGranted(AjaxRequestTarget target) {
                if(firstRun == true){
                    this.setVisible(false);
                    getDataFromFacebookButton.setVisible(true);
                }
                else
                    doAction(target);
            }
        };
        facebookConnect.setOutputMarkupId(true);
        getDataFromFacebookButton.setOutputMarkupId(true);
        add(facebookConnect);
        firstRun=false;

    }
    public void doAction(AjaxRequestTarget target){
        new FacebookUtils().fillBasicInfo(userFacade.getUser(), true);
        userFacade.save();
        target.add(toUpdate.getObject());
        if(facebookConnect != null){
            facebookConnect.setVisible(false);
            target.add(facebookConnect);
        }
        getDataFromFacebookButton.setVisible(true);
        target.add(getDataFromFacebookButton);
    }

}
