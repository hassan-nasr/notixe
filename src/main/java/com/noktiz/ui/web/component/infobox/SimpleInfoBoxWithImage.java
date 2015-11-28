package com.noktiz.ui.web.component.infobox;

import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by Hossein on 12/6/2014.
 */
public class SimpleInfoBoxWithImage extends BasePanel{


    private final InfoBoxProvider provider;

    public SimpleInfoBoxWithImage(String id,final InfoBoxProvider provider) {
        super(id);
        add(new AttributeAppender("class"," "+provider.getAlignmentClass()+" "));
        this.provider = provider;
        WebMarkupContainer image = new WebMarkupContainer("image"){
            @Override
            protected void onConfigure() {
                setVisible(provider.getImageEnable());
            }
        };
        add(image);
        image.add(new AttributeModifier("src", provider.getImageUrl()));

        Label title = new Label("title",new StringResourceModel(provider.getTitleResourceKey(),null));
        add(title);
        MultiLineLabel text = new MultiLineLabel("text",new StringResourceModel(provider.getTextResourceKey(),null));
        add(text);
        ListView actions = new ListView("actions", provider.getActions()) {
            @Override
            protected void populateItem(ListItem item) {
                final InfoBoxProvider.IAction iAction=(InfoBoxProvider.IAction)item.getModelObject();
                WebMarkupContainer action = iAction.getLink("action");
//                action = new IndicatingAjaxSubmitLink2("generalAction") {
//                    @Override
//                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                        boolean onAction = iAction.onAction(target, SimpleInfoBoxWithImage.this);
//                        if (onAction) {
//                            target.add(form);
//                        }
//                    }
//
//                    @Override
//                    public void onConfigure() {
//                        super.onConfigure();
//                        setEnabled(iAction.isActionEnabled());
//                    }
//                };
                item.add(action);
                Label l = new Label("generalActionTitle", iAction.getActionTitle());
                action.add(l);
                action.add(new AttributeAppender("class", iAction.getButtonClass()));
            }
        };
        add(actions);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if(provider.getAlignmentClass()!=null) {
            response.render(JavaScriptHeaderItem.forReference(
                    new JavaScriptResourceReference(SimpleInfoBoxWithImage.class, "SimpleInfoBoxWithImage.js")));
            response.render(new OnDomReadyHeaderItem("enableAlign('" + provider.getAlignmentClass() + "')"));
        }
    }
}
