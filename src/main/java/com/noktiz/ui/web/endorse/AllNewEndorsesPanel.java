package com.noktiz.ui.web.endorse;

import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.entity.endorse.EndorseManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.base.UserPanel;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import com.noktiz.ui.web.user.SingleUserImageWithLinkPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;
import java.util.Set;


/**
 * Created by hasan on 2014-10-15.
 */
public class AllNewEndorsesPanel extends UserPanel {
    EndorseManager endorseManager = new EndorseManager();
    boolean showRemoved =true;
//    CheckBox showRemovedBox;
    boolean showRemovedInside = false;
    boolean hasRemoved = false;
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AllNewEndorsesPanel.class, "endorse.css")));
    }
    public AllNewEndorsesPanel(String id) {
        super(id);
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        add(wmc);
        setOutputMarkupId(true);
        final UserFacade user = getUserInSite();

        final List<Endorse> newEndorses = endorseManager.getUnApprovedEndorsesNotEfficient(user.getUser());
        final Set<String> removedEndorses = user.getUser().getRemovedEndorseContexts();


        final AjaxLink showRemovedButton = new IndicatingAjaxLink2("showRemoved"){
            String text = "show removed endorses";
            @Override
            public IModel<?> getBody() {
                return Model.of(text);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                showRemovedInside=!showRemovedInside;
                if(showRemovedInside == true){
                    text = (getString("hideRemovedEndorsed"));
                }
                else {
                    text = getString("showRemovedEndorses");
                }
                target.add(wmc,this);
            }


        };

        if(removedEndorses.size()==0)
            showRemovedButton.setVisible(false);

        final ListView newEndorsesList = new ListView("newEndorses",newEndorses){
            @Override
            protected void populateItem(final ListItem item) {
                showRemoved = showRemovedInside;
                item.setOutputMarkupId(true);
                final Endorse endorse = (Endorse) item.getModelObject();
//                WebMarkupContainer c=  new TransparentWebMarkupContainer("wmc");
//                item.add(c);
                if(removedEndorses.contains(endorse.getContext()) && !showRemoved){
                    item.setVisible(false);
                    return;
                }

//                final WebMarkupContainer done = new WebMarkupContainer("done");
//                item.add(done);
//                done.setVisible(false);
                final IndicatingAjaxLink2 addToProfile = new IndicatingAjaxLink2("addToProfile") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        user.approveEndorseContext(endorse);
                        user.save();
//                        done.setVisible(true);
                        target.add(item);
                        setEnabled(false);
                        setBody(Model.of("done"));
                        add(new AttributeAppender("class", " disabled"));
//                        setVisible(false);
                    }

                };
                item.add(addToProfile);
                item.add(new SingleUserImageWithLinkPanel("senderImage",new UserFacade(endorse.getSender()),50));
                item.add(new Label("senderName", endorse.getSender().getName()));
                item.add(new Label("endorseContext", endorse.getContext()));
            }
        };
        newEndorsesList.setOutputMarkupId(true);
        wmc.add(newEndorsesList);

        add(showRemovedButton);
    }
}
