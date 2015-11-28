package com.noktiz.ui.web.profile;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.access.AccessLevel;
import com.noktiz.domain.entity.access.BasePrivacy;
import com.noktiz.domain.entity.access.PrivacyLevel;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.BasePanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;

import java.util.Arrays;

/**
 * Created by hasan on 2014-10-07.
 */
public class ProtectedPanel extends BasePanel {
    /**
     *
     * @param id
     * @param content  a component with id='content'
     * @param privacy
     * @param noAccessMessage
     */
    public ProtectedPanel(String id, WebMarkupContainer content, BasePrivacy privacy, String noAccessMessage , boolean enableEdit, boolean hideCompletely) {
        super(id);
        UserFacade u = getUserInSite();
        if(u == null){
            u=new UserFacade(new User());
        }
        AccessLevel accessLevel = privacy.getAccessLevel(u.getUser());
        if(accessLevel.equals(AccessLevel.Read) || accessLevel.equals(AccessLevel.Write)){
            add(content);
            WebMarkupContainer noAccessMessageComponent = new WebMarkupContainer("noAccessMessage");
            noAccessMessageComponent.setVisible(false);
            add(noAccessMessageComponent);
        }
        else {
            add(new WebMarkupContainer("content").setVersioned(false));
            add(new MultiLineLabel("noAccessMessage", Model.of(noAccessMessage)));
            if(hideCompletely)
                setVisible(false);
        }
        if(enableEdit){
            final DropDownChoice privacyDropDown = new DropDownChoice("editPrivacy", new Model(privacy.getPrivacyLevel()), Arrays.asList(PrivacyLevel.values()),new ChoiceRenderer(){
                @Override
                public Object getDisplayValue(Object object) {
                    return ((PrivacyLevel) object).toString();
                }

                @Override
                public String getIdValue(Object object, int index) {
                    return String.valueOf(((PrivacyLevel) object).ordinal());
                }
            }){
                @Override
                protected void onSelectionChanged(Object newSelection) {
                    updatePrivacy((PrivacyLevel) this.getDefaultModelObject());
                }

                @Override
                protected boolean wantOnSelectionChangedNotifications() {
                    return true;
                }
            };
            add(privacyDropDown);
        }
        else
            add(new WebMarkupContainer("editPrivacy").setVisible(false));
    }

    protected void updatePrivacy(PrivacyLevel defaultModelObject) {
        throw new UnsupportedOperationException("editPrivacy not implemented here");
    }
}
