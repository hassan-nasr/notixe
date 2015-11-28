package com.noktiz.ui.web.endorse;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.entity.endorse.EndorseManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.user.SingleUserImageWithLinkPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

/**
 * Created by hasan on 2014-10-07.
 */
public class EndorseOverviewPanel extends BasePanel {
    private final IndicatingAjaxLink2 removeFromProfileButton;
    private final User profileOwner;
    boolean showAdd =false;
    boolean showRemove =false;
    private final IndicatingAjaxLink2 addButton;
    private final IndicatingAjaxLink2 removeButton;
    private final EndorseManager endorseManager = new EndorseManager();
    private Integer userInSiteEndorseIndex=-1;
    private final NotificationFeedbackPanel feedback;
    public EndorseOverviewPanel(String id, final String context, final User profileOwner, final List<Endorse> current, final int explicitView) {
        super(id);
        this.profileOwner = profileOwner;
        setOutputMarkupId(true);
        feedback = new NotificationFeedbackPanel("feedback");
        add(feedback);
        feedback.setOutputMarkupId(true);
        final UserFacade userInSite = getUserInSite();
        if(userInSite.getUser().getActive())
            showAdd =true;
        for (int i = 0; i < current.size(); i++) {
            Endorse endorse = current.get(i);
            if (endorse.getSender().equals(userInSite.getUser())) {
                showAdd = false;
                userInSiteEndorseIndex = i;
                showRemove = true;
            }
        }
//        for (int i = 0; i < 20; i++) {
//            current.add(new Endorse(context,userInSite.getUser(),profileOwner));
//        }
        Label title = new Label("title",context);
        add(title);
        final ListView points= new ListView("persons", Model.of(current)) {
            int index=-1;
            int foundUser=0;
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                index =-1;
            }

            @Override
            protected void populateItem(ListItem item) {
                index++;
                Endorse endorse = (Endorse) item.getModelObject();
                if(endorse.getSender().equals(endorse.getReceiver()) || (index>explicitView+foundUser))
                {

                    item.add(new WebMarkupContainer("item").setVisible(false));
                    item.add(new WebMarkupContainer("more").setVisible(false));
                    foundUser=1;
                }
                else if(index== explicitView +foundUser){
                    StringBuilder names=new StringBuilder();
                    for (int i = index; i < current.size(); i++) {
                        Endorse endorse1 = current.get(i);
                        names.append(endorse1.getSender().getName()).append(", ");
                    }
                    Image image = new Image("more", new PackageResourceReference(EndorseOverviewPanel.class,"dots.jpg"));
                    item.add(image);
                    image.add(new AttributeModifier("data-content", names));

                    item.add(new WebMarkupContainer("item").setVisible(false)) ;
                }
                else {
                    item.add(new WebMarkupContainer("more").setVisible(false));
                    item.add(new SingleUserImageWithLinkPanel("item", new UserFacade(endorse.getSender()), 40));
                }
//                Image image = new Image("img", "");
//                item.add(image);
//
//                String imageUrl = RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
//                        ImageManagement.getUserImageParameter(new UserFacade(endorse.getSender()), ImageManagement.ImageSize.small)).toString();
//                image.add(new AttributeModifier("src", imageUrl));
//                image.add(new AttributeModifier("data-content", endorse.getSender().getName()));
            }
        };
        add(points);
        points.setOutputMarkupId(true);
        addButton = new IndicatingAjaxLink2("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Endorse newEndorse=new Endorse(context,userInSite.getUser(),profileOwner);
                try{
                    Result res= endorseManager.addEndorse(newEndorse);
                    current.add(0,newEndorse);
                    userInSiteEndorseIndex=0;
                    showAdd =false;
                    showRemove =true;
                    setVisibilities();
                    target.add(addButton,removeButton,EndorseOverviewPanel.this);
                } catch (ResultException e) {
                    e.getResult().displayInWicket(feedback);

                }
                target.add(feedback);
            }
        };
        add(addButton);
        addButton.setOutputMarkupId(true);
        removeButton = new IndicatingAjaxLink2("remove") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                endorseManager.remove(current.get(userInSiteEndorseIndex));
                current.remove((int)userInSiteEndorseIndex);
                userInSiteEndorseIndex=-1;
                showAdd =true;
                showRemove =false;
                setVisibilities();
                target.add(EndorseOverviewPanel.this);
            }
        };
        add(removeButton);
        removeButton.setOutputMarkupId(true);

        removeFromProfileButton = new IndicatingAjaxLink2("removeFromProfile") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                UserFacade userFacade = new UserFacade(profileOwner);
                userFacade.removeEndorseContext(context);
                userFacade.save();
                setBody(Model.of("removed"));
                add(new AttributeAppender("class", " disabled"));
                setEnabled(false);
                target.add(EndorseOverviewPanel.this);

            }
        };
        add(removeFromProfileButton);
        removeFromProfileButton.setOutputMarkupId(true);
        setVisibilities();

    }

    private void setVisibilities() {
        if(profileOwner.equals(getUserInSite().getUser())){
            addButton.setVisible(false);
            removeButton.setVisible(false);
            removeFromProfileButton.setVisible(true);
        }
        else {
            addButton.setVisible(showAdd);
            removeButton.setVisible(showRemove);
            removeFromProfileButton.setVisible(false);
        }
    }
}
