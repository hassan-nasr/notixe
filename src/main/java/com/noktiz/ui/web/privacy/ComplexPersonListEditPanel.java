package com.noktiz.ui.web.privacy;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.privacy.ComplexPersonList;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.TagInput;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.io.Serializable;
import java.util.*;

/**
 * Created by hasan on 2014-11-03.
 */
public class ComplexPersonListEditPanel extends UserPanel {


    private final String includeTrustedString = "AllTrustedFriends";
    User newToAddObj;
    ListMultipleChoice includedUsers;
    CheckBox friends;
    WebMarkupContainer wmc;


    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "/assets/plugins/chosen-bootstrap/chosen/chosen.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "/assets/plugins/chosen-bootstrap/chosen/chosen.jquery.js")));
//        response.render(new OnDomReadyHeaderItem("$(\"[id='"+friends.getMarkupId() +"']\").bootstrapSwitch();"));
    }

    public ComplexPersonListEditPanel(String id, final IModel<ComplexPersonList> invitedPersons, final UserFacade user) {
        super(id);
        setOutputMarkupId(true);
//        friends = new CheckBox("allFriends", new IModel<Boolean>() {
//            @Override
//            public Boolean getObject() {
//                return invitedPersons.getObject().isIncludeMyTrustedFriends();
//            }
//
//            @Override
//            public void setObject(Boolean object) {
//                invitedPersons.getObject().setIncludeMyTrustedFriends(object);
//            }
//
//            @Override
//            public void detach() {
//
//            }
//        }) {
////            @Override
////            protected void onUpdate(AjaxRequestTarget target) {
////                includedUsers.setVisible(!getModelObject());
////                target.add(wmc);
////            }
//
//            @Override
//            public void renderHead(IHeaderResponse response) {
//                super.renderHead(response);
//                response.render(new OnDomReadyHeaderItem("$('.switch').bootstrapSwitch()"));
//            }
////            @Override
////            public void renderHead(IHeaderResponse response) {
////                render(new OnDomReadyHeaderItem("('"+getMarkupId()+"').on('switch-change', function () {\n" +
////                        "    console.log(\"inside switchchange\");\n" +
////                        "    toggleWeather();\n" +
////                        "})";
////                super.renderHead(response);
////            }
//
////            @Override
////            protected () {
////                super.onBeforeRender();
////                add(new AttributeModifier("onSwitchChange","$('"+getMarkupId()+"').click()"))
////            }
//        };
//        add(friends);
//        friends.setOutputMarkupId(true);


        wmc = new WebMarkupContainer("iuwmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        includedUsers = new ListMultipleChoice("includedUsers",  new IModel<List>() {
            @Override
            public List getObject() {
                List<Object> includeUsers = new ArrayList<>();

                if(invitedPersons.getObject().isIncludeMyTrustedFriends())
                    includeUsers.add(includeTrustedString);
                return includeUsers;
            }

            @Override
            public void setObject(List list) {
                Set<User> newIncludeUsers = new HashSet<>();
                for (Object item : list) {
                    if(item instanceof String)
                    {
                        if(((String) item).equals(includeTrustedString))
                            invitedPersons.getObject().setIncludeMyTrustedFriends(true);
                    }
                    else if(item instanceof User)
                        newIncludeUsers.add((User) item);

                }
                invitedPersons.getObject().setIncludeUsers(newIncludeUsers);
            }

            @Override
            public void detach() {

            }
        }, new IModel<List>() {
            @Override
            public List getObject() {
                return getFriends(user,invitedPersons.getObject());
            }

            @Override
            public void setObject(List list) {

            }

            @Override
            public void detach() {

            }
        }, new IChoiceRenderer<Object>() {
            @Override
            public Object getDisplayValue(Object object) {
                if(object instanceof User) {
                    return ((User) object).getName() + " (" + ((User) object).getEmail() + ")";
                }
                else if(object instanceof String){
                    return getString((String) object);
                }
                return null;
            }

            @Override
            public String getIdValue(Object object, int index) {
                return String.valueOf(index);
//                return null;
            }

        }){
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                response.render(new OnDomReadyHeaderItem("$('.chosen').chosen()"));
            }
        };
        wmc.add(includedUsers);
        includedUsers.setOutputMarkupId(true);
//        if(invitedPersons.getObject().isIncludeMyTrustedFriends())
//            includedUsers.setVisible(false);

    }

    private List<Object> getFriends(UserFacade user, ComplexPersonList object) {
//        UserFacade userFacade = new UserFacade(user);
//        userFacade.refresh();
//        user=userFacade.getUser();

        HashSet<Object> ret = new HashSet<>();
        ret.add(includeTrustedString);
        for (Friendship friendship : user.getFriends()) {
           ret.add(friendship.getFriend());
        }
        ret.addAll(object.getIncludeUsers());

        return new ArrayList<>(ret);
    }
}
