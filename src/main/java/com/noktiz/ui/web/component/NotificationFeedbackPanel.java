/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.component;

import java.util.ArrayList;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public final class NotificationFeedbackPanel extends Panel implements IFeedback {

    public NotificationFeedbackPanel(final String id) {
        this(id, null,20000l);
    }
    
    public NotificationFeedbackPanel(final String id,Long timeOut) {
        this(id, null,timeOut);
    }
    
    public NotificationFeedbackPanel(final String id, IFeedbackMessageFilter filter){
        this(id, filter,20000l);
    }
    /**
     * @see org.apache.wicket.Component#Component(String)
     *
     * @param id
     * @param filter
     */
    public NotificationFeedbackPanel(final String id, IFeedbackMessageFilter filter,Long timeOut) {
        super(id);
        setDefaultModel(new FeedbackMessagesModel(this));
        setOutputMarkupId(true);
        if (filter != null) {
            setFilter(filter);
        }
    }

    public final NotificationFeedbackPanel setFilter(IFeedbackMessageFilter filter) {
        getFeedbackMessagesModel().setFilter(filter);
        return this;
    }

    public final FeedbackMessagesModel getFeedbackMessagesModel() {
        return (FeedbackMessagesModel) getDefaultModel();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        FeedbackMessagesModel fmm = getFeedbackMessagesModel();
        StringBuilder success = new StringBuilder();
        StringBuilder info = new StringBuilder();
        StringBuilder error = new StringBuilder();
        StringBuilder warnning = new StringBuilder();
        for (FeedbackMessage fm : fmm.getObject()) {
            if (fm.isRendered()) {
                continue;
            }
            if (fm.getLevel() == FeedbackMessage.SUCCESS) {
                success.append(fm.getMessage()).append("<br/>");
            }
            if (fm.getLevel() == FeedbackMessage.ERROR) {
                error.append(fm.getMessage()).append("<br/>");
            }
            if (fm.getLevel() == FeedbackMessage.INFO) {
                info.append(fm.getMessage()).append("<br/>");
            }
            if (fm.getLevel() == FeedbackMessage.WARNING) {
                warnning.append(fm.getMessage()).append("<br/>");
            }
            fm.markRendered();
        }

        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(NotificationFeedbackPanel.class, "NotificationFeedbackPanel.js")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(NotificationFeedbackPanel.class, "NotificationFeedbackPanel.css")));
        final String infoStr = info.toString();
        if (!infoStr.isEmpty()) {
            response.render(OnDomReadyHeaderItem.forScript("feedback(\"" + infoStr + "\",'info')"));
        }
        final String successStr = success.toString();
        if (!successStr.isEmpty()) {
            response.render(OnDomReadyHeaderItem.forScript("feedback(\"" + successStr + "\",'success')"));
        }
        final String errorStr = error.toString();
        if (!errorStr.isEmpty()) {
            response.render(OnDomReadyHeaderItem.forScript("feedback(\"" + errorStr + "\",'error')"));
        }
        final String warnningStr = warnning.toString();
        if (!warnningStr.isEmpty()) {
            response.render(OnDomReadyHeaderItem.forScript("feedback(\"" + warnningStr + "\",'warnning')"));
        }


    }
}
