package com.noktiz.ui.web.mail;

import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.ui.web.auth.UserRoles;
import com.noktiz.ui.web.component.AutoGrowTextArea;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.restfb.types.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by Sina Labbaf on 2015-03-29.
 */
@AuthorizeInstantiation("ADMIN")
public class MailSenderPanel extends WebPage {
    private String receiverObj;
    private String titleObj;
    private String textObj;
    private String htmlObj;
    TextField<String> receiver;
    TextField<String> title;
    AutoGrowTextArea html;
    AutoGrowTextArea text;


    public MailSenderPanel() {
        super();
        Form form = new Form("form");
        add(form);
        receiver = new TextField<String>("receiver", new IModel<String>(){

            @Override
            public String getObject() {
                return receiverObj;
            }

            @Override
            public void setObject(String s) {
                receiverObj = s;
            }

            @Override
            public void detach() {

            }
        });
        form.add(receiver);

        title = new TextField<String>("title", new IModel<String>(){

            @Override
            public String getObject() {
                return titleObj;
            }

            @Override
            public void setObject(String s) {
                titleObj = s;
            }

            @Override
            public void detach() {

            }
        });
        form.add(title);

        html = new AutoGrowTextArea("html", new IModel<String>() {
            @Override
            public String getObject() {
                return htmlObj;
            }

            @Override
            public void setObject(String s) {
                htmlObj = s;
            }

            @Override
            public void detach() {

            }
        });
        form.add(html);

        text = new AutoGrowTextArea("text", new IModel<String>() {
            @Override
            public String getObject() {
                return textObj;
            }

            @Override
            public void setObject(String s) {
                textObj = s;
            }

            @Override
            public void detach() {

            }
        });
        form.add(text);

        SubmitLink send = new SubmitLink("send") {
            @Override
            public void onSubmit() {
                IEmailProvider.get().send(receiverObj, EmailCreator.createRawEmail(titleObj,htmlObj,textObj));
            }
        };
        form.add(send);

        SubmitLink preview = new SubmitLink("preview") {
            @Override
            public void onSubmit() {
                IEmailProvider.get().send(receiverObj, EmailCreator.createRawEmail(titleObj,htmlObj,textObj));
            }
        };
        form.add(preview);

        Label HTMLPreview = new Label("HTMLPreview", new IModel<String>() {
            @Override
            public String getObject() {
                return htmlObj;
            }

            @Override
            public void setObject(String s) {
                htmlObj=s;
            }

            @Override
            public void detach() {

            }
        });
        HTMLPreview.setRenderBodyOnly(true);
        HTMLPreview.setEscapeModelStrings(false);
        form.add(HTMLPreview);
    }
}
