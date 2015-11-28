/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.thread;

import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.behavior.ThreadListBehavoir;
import com.noktiz.ui.web.component.AjaxPageNavigator;
import com.noktiz.ui.web.component.IndicatingAjaxLink2;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

/**
 *
 * @author hossein
 */
public abstract class ThreadList extends BasePanel {

    final int pageSize = 15;

    public ThreadList(String id, final IDataProvider<ThreadFacade> threadProvider) {
        super(id);
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        DataView<ThreadFacade> threadlist;
        threadlist = new DataView<ThreadFacade>("threads", threadProvider, pageSize) {
            @Override
            protected void populateItem(final Item<ThreadFacade> item) {
                final ThreadFacade tf = item.getModelObject();
                String name;
                if (tf.getParticipant() == null) {
                    name = "";
                } else {
                    name = tf.getParticipant().getName();
                }
                final Label participant = new Label("participant", name);
                item.add(participant);
                if (tf.isHidden()) {
                    participant.add(new AttributeAppender("class", " label label-success "));
                }
                WebMarkupContainer blocked = new WebMarkupContainer("blocked") {
                    @Override
                    public boolean isVisible() {
                        return tf.getParticipantBlock() != null;
                    }

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        String js = "jQuery('.tooltips').tooltip();";
                        response.render(OnDomReadyHeaderItem.forScript(js));
                    }
                };
                item.add(blocked);
                WebMarkupContainer iBlocked = new WebMarkupContainer("iBlocked") {
                    @Override
                    public boolean isVisible() {
                        return tf.getMyBlock()!= null;
                    }

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        String js = "jQuery('.tooltips').tooltip();";
                        response.render(OnDomReadyHeaderItem.forScript(js));
                    }
                };
                item.add(iBlocked);
                WebMarkupContainer blockedByCompl = new WebMarkupContainer("blockedByCompl") {
                    @Override
                    public void onConfigure() {
                        super.onConfigure();
                        setVisible(tf.getParticipantBlock() == null && tf.getParticipantBlockBy() != null && tf.isBlockedCompletely() == true);
                    }

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        String js = "jQuery('.tooltips').tooltip();";
                        response.render(OnDomReadyHeaderItem.forScript(js));
                    }
                };
                item.add(blockedByCompl);
                WebMarkupContainer blockedBy = new WebMarkupContainer("blockedBy") {
                    @Override
                    public void onConfigure() {
                        super.onConfigure();
                        setVisible(tf.getParticipantBlock()== null && tf.getParticipantBlockBy()!= null && tf.isBlockedCompletely() == false);
                    }

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        String js = "jQuery('.tooltips').tooltip();";
                        response.render(OnDomReadyHeaderItem.forScript(js));
                    }
                };
                item.add(blockedBy);
                AjaxLink showThread = new AjaxLink("showThread") {
                    @Override
                    public void onClick(AjaxRequestTarget art) {
                        onSelectThread(art, tf, (int) getCurrentPage() * pageSize + item.getIndex());
                    }
                };
                item.add(showThread);
                item.add(new ThreadListBehavoir(showThread));
                String title = tf.getTitle();
                if (title.trim().equals("")) {
                    title = ("<no subject>");
                }
                title=title.substring(0, Math.min(title.length(), 30));
                showThread.add(new Label("title", title));

                String text;
                String date;
                if (tf.getMessages().size() - 1 >= 0) {
                    text = tf.getMessages().get(tf.getMessages().size() - 1).getContent();
                    text = text.substring(0, Math.min(text.length(), 30)).replaceAll("\\s", " ");
//                    date = TimeManager.getDate(tf.getMessages().get(tf.getMessages().size() - 1).getSendDate());
                    date = getFormattedUserDate(tf.getMessages().get(tf.getMessages().size() - 1).getSendDate());
                } else {
                    text = "";
                    date = "";
                }
                showThread.add(new Label("text", text));
                item.add(new Label("date", date));
                if (tf.isUnread()) {
                    item.add(new AttributeAppender("class", " alert alert-warning "));
                }
            }
        };
        wmc.add(threadlist);
        AjaxPageNavigator nav = new AjaxPageNavigator("nav", threadlist) {
            @Override
            public void onAjax(AjaxRequestTarget target) {
                target.add(wmc);
            }
        };
        add(nav);
        IndicatingAjaxLink2 refresh = new IndicatingAjaxLink2("refresh") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(wmc);
            }
        };
        add(refresh);
//        refresh.add(new CallPeriodically(10000, "enable", false, "NULL"));
//        PagingNavigator navigator = new PagingNavigator("navigator", threadlist);
//        add(navigator);
//        new AjaxFallbackDefaultDataTable("da", null, null, FLAG_RESERVED1)

    }

    public abstract void onSelectThread(AjaxRequestTarget art, ThreadFacade tf, int number);

    protected abstract void onAjax(AjaxRequestTarget art);
}
