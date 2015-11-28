package com.noktiz.ui.web.friend;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.behavior.SelectPersonBehavior;
import com.noktiz.ui.web.component.BootstrapIcon;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.bootstrap.RateField;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by hasan on 2014-11-24.
 */
public class Tile extends BasePanel {

    String searchContent = " searchContent";

    public Tile(String id, final PersonListProvider.IPerson person, boolean selectable) {
        super(id);
        setOutputMarkupId(true);
        add(feedbackPanel);
        Form form = new Form("form");
        add(form);
        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        form.add(wmc);
        if(person.getTileGroupClass()!=null){
            wmc.add(new AttributeAppender("class", " " +person.getTileGroupClass()+" "));
        }
        if (person.isSeparator()) {
            wmc.add(new AttributeAppender("class", " headerTile "));
        } else {
            wmc.add(new AttributeAppender("class", " searchElement "));
        }
        setOutputMarkupId(true);
        CheckBox select = new CheckBox("select", person.isSelect());
        wmc.add(select);
//                wmc.add(new ItemBehavior());
        select.setOutputMarkupId(true);
        if (selectable) {
            wmc.add(new SelectPersonBehavior(select));
        } else {
        }

        {
            String imageLinkUrl = person.getImageLink();
            if (imageLinkUrl == null) {
                imageLinkUrl = "";
            }
            ExternalLink imageLink = new ExternalLink("imgLink", imageLinkUrl);
            wmc.add(imageLink);
            if (imageLinkUrl == "") {
                imageLink.setEnabled(false);
            }

            Image image = new Image("img", "");
            imageLink.add(image);
            String imageUrl = person.getImageUrl();
            image.add(new AttributeModifier("src", imageUrl));

            if (imageUrl == null) {
                imageLink.setVisible(false);
            }
        }
        {
            String linkUrl = person.getTitleLink();
            if (linkUrl == null) {
                linkUrl = "";
            }
            ExternalLink link = new ExternalLink("titleLink", linkUrl) {

                @Override
                protected void onConfigure() {
                    super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                    setVisible(person.getTitleEnable());
                }

            };
            wmc.add(link);
            if (linkUrl == "") {
                link.setEnabled(false);
            }
            Label title = new Label("title", person.getTitle()){
                @Override
                public void renderHead(IHeaderResponse response) {
                    super.renderHead(response);
                    if(person.isSeparator()){
                        return;
                    }
                    if(isMobile) {
                        String js = "jQuery('#" + getMarkupId() + "').tooltip();";
                        response.render(OnDomReadyHeaderItem.forScript(js));
                    }
                }
            };
            title.setOutputMarkupId(true);
            if(isMobile) {
                title.add(new AttributeAppender("data-original-title", person.getTitle()));
            }
            else{
                title.add(new AttributeAppender("title", person.getTitle()));
            }
            link.add(title);
            title.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if ((Boolean)person.getSearchInTitle().getObject()) {
                        return searchContent;
                    }
                    return "";
                }
            }));
        }
        {
            String linkUrl = person.getDescLink();
            if (linkUrl == null) {
                linkUrl = "";
            }
            ExternalLink link = new ExternalLink("descLink", linkUrl) {

                @Override
                protected void onConfigure() {
                    super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                    setVisible(person.getDescEnable());
                }

            };
            wmc.add(link);
            if (linkUrl == "") {
                link.setEnabled(false);
            }

            MultiLineLabel desc = new MultiLineLabel("desc", person.getDesc());

            link.add(desc);

            desc.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if ((Boolean)person.getSearchInDesc().getObject()) {
                        return searchContent;
                    }
                    return "";
                }
            }));
        }
        {
            TextArea inputbox = new TextArea("inputbox", person.getInputBox()) {

                @Override
                protected void onConfigure() {
                    super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                    setVisible(person.getInputBoxEnable());
                }

                @Override
                public void renderHead(IHeaderResponse response) {
                    super.renderHead(response);
                    response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,
                            "/assets/plugins/bootstrap-maxlength/bootstrap-maxlength.js")));
                    if(person.getInputBoxLength() != null){
                        response.render(new OnDomReadyHeaderItem("$('textarea[maxlength]').maxlength({threshold: "+(person.getInputBoxLength()-20)+"});"));
                    }
                }
            };
            if(person.getInputBoxLength() != null){
                inputbox.add(new AttributeModifier("maxlength",person.getInputBoxLength()));

            }
            wmc.add(inputbox);
        }
        {
            String linkUrl = person.getSubtitleLink();
            if (linkUrl == null) {
                linkUrl = "";
            }
            ExternalLink link = new ExternalLink("subtitleLink", linkUrl);
            wmc.add(link);
            if (linkUrl == "") {
                link.setEnabled(false);
            }

            Label subtitle = new Label("subtitle", person.getSubTitle());
            link.add(subtitle);
            subtitle.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    if ((Boolean)person.getSearchInSubtitle().getObject()) {
                        return searchContent;
                    }
                    return "";
                }
            }));
        }
        wmc.add(new AttributeAppender("class", person.getBGClass()));
        RateField rate = new RateField("rate", person.getStars()) {

            @Override
            protected void onConfigure() {
                super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                if (person.getStarsEnable() && person.getStarsEditable()) {
                    setVisible(true);
                } else {
                    setVisible(false);
                }
            }

        };
        wmc.add(rate);

        WebMarkupContainer stars = new WebMarkupContainer("stars") {

            @Override
            protected void onConfigure() {
                super.onConfigure(); //To change body of generated methods, choose Tools | Templates.
                this.setVisible(person.getStarsEnable() && !person.getStarsEditable());
            }

        };
        wmc.add(stars);
        if (person.getStarsEnable()) {
            for (int i = 0; i < 5; i++) {
                final int current = i;
                WebMarkupContainer star = new BootstrapIcon("star-" + (i + 1), new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (current < (Double)person.getStars().getObject()) {
                            return "icon-star";
                        } else {
                            return "icon-star-empty";
                        }
                    }
                });
                stars.add(star);
            }
        } else {
            stars.setVisible(false);
        }

        ListView actions = new ListView("actions", person.getActions()) {
            @Override
            protected void populateItem(ListItem actionItem) {
                final PersonListProvider.IPerson.IAction iaction = (PersonListProvider.IPerson.IAction) actionItem.getDefaultModelObject();
                actionItem.setRenderBodyOnly(true);
                AjaxSubmitLink action = new IndicatingAjaxSubmitLink2("action") {
                    @Override
                    public void onSubmit(AjaxRequestTarget target, Form form) {
                        boolean onAction = iaction.onAction(target, Tile.this);
                        if (onAction) {
                            target.add(Tile.this);
                        }
                        target.add(feedbackPanel);
                    }

                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setEnabled(iaction.isActionEnabled());
                        setVisible(iaction.isActionVisible());
                        if(!iaction.isActionEnabled()){
                            add(new AttributeAppender("class"," disabled"));
                        }
                    }
                };
                actionItem.add(action);
                action.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        if (iaction.getSearchInAction().getObject()) {
                            return searchContent;
                        }
                        return "";
                    }
                }));

                action.add(new AttributeAppender("class", iaction.getButtonClass()));
                Label actionTitle = new Label("actionTitle", iaction.getActionTitle());
                action.add(actionTitle);
            }
        };
        wmc.add(actions);
        actions.setRenderBodyOnly(true);
    }

}
