/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.thread;

import com.noktiz.domain.model.MessageFacade;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.domain.model.TimeManager;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.TimeElapse;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public abstract class ViewThread extends BasePanel {

    boolean noreply = false;

    public ViewThread(String id, IModel<ThreadFacade> tf) {
        super(id, tf);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        add(wmc);
        wmc.setOutputMarkupId(true);
        ThreadInfoPanel threadInfo = new ThreadInfoPanel("threadInfo",tf) {
            @Override
            public void onBlock(AjaxRequestTarget art) {

            }

            @Override
            public void onUnblock(AjaxRequestTarget art) {

            }
        };
        add(threadInfo);
        final NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        wmc.add(feedback);
        feedback.setOutputMarkupId(true);
        AjaxLink newer = new AjaxLink("newer") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onNewer(target);
            }

            @Override
            public boolean isVisible() {
                return hasNewer();
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
        };
        wmc.add(newer);
        AjaxLink older = new AjaxLink("older") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onOlder(target);
            }

            @Override
            public boolean isVisible() {
                return hasNewer();
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "jQuery('.tooltips').tooltip();";
                response.render(OnDomReadyHeaderItem.forScript(js));
            }
        };
        wmc.add(older);
        String titleStr = getObj().getTitle();
        if(titleStr==null || titleStr.trim().equals("")){
            titleStr=getString("NoSubject");
        }

        Label title = new Label("title", titleStr);
        
        wmc.add(title);
        final ListView thread = new ListView("thread", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return getObj().getMessages();
            }
        }) {
            @Override
            protected void populateItem(final ListItem li) {
                final MessageFacade obj = (MessageFacade) li.getModelObject();
//                WebMarkupContainer head = new WebMarkupContainer("head");
//                li.add(head);
                li.setOutputMarkupId(true);
                Image image = new Image("image", ImageManagement.getUserImageResourceReferece(),
                        ImageManagement.getUserImageParameter(obj.getSender(), ImageManagement.ImageSize.medium));
                li.add(image);
                Label sender = new Label("sender", obj.getSender().getName());
                li.add(sender);
                WebMarkupContainer newwmc = new WebMarkupContainer("new") {
                    @Override
                    public void onConfigure() {
                        super.onConfigure();
                        setVisible(!obj.isReciverRead());
                    }
                };
                li.add(newwmc);
                WebMarkupContainer blockedwmc = new WebMarkupContainer("blocked") {
                    @Override
                    public void onConfigure() {
                        super.onConfigure();
                        setVisible(obj.getBlock() != null);
                    }
                };
                li.add(blockedwmc);
                Label sendDate = new Label("sendDate", new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
//                        return TimeManager.getDate(obj.getSendDate());
                        return getFormattedUserDate(obj.getSendDate());
                    }
                });
                li.add(sendDate);
                TimeElapse timeElapse = new TimeElapse("elapseTime", Model.of(obj.getSendDate()), 100, null);
                li.add(timeElapse);
                final WebMarkupContainer content = new WebMarkupContainer("content");
                li.add(content);
                content.setOutputMarkupId(true);
                final WebMarkupContainer sentMessage = new WebMarkupContainer("sentMessage");
                content.add(sentMessage);
                sentMessage.add(new AttributeModifier("dir", new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        return obj.getDir();
                    }
                }));
                sentMessage.setOutputMarkupId(true);
                final WebMarkupContainer draft = new WebMarkupContainer("draft");
                content.add(draft);
                draft.setOutputMarkupId(true);
                MultiLineLabel text;
                text = new MultiLineLabel("text", new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        return obj.getContent();
                    }
                });
                text.add(new AttributeModifier("dir", new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        return obj.getDir();
                    }
                }));
                sentMessage.add(text);
                if (obj.isDraft()) {
                    sentMessage.setVisible(false);
                    ReplyPanel draftPanel = new ReplyPanel("draftPanel", Model.of(obj.getContent()), new AbstractReadOnlyModel<String>() {
                        @Override
                        public String getObject() {
                            return obj.getDir();
                        }
                    }) {
                        @Override
                        protected void onSend(String text, String dir) {
                            obj.setContent(text);
                            obj.setDir(MessageFacade.convertDir(dir));
                            Result publish = obj.publish();
                            if (publish.result == true) {
//                                MultiLineLabel sent = new MultiLineLabel("text", obj.getContent());
//                                sentMessage.addOrReplace(sent);
                                sentMessage.setVisible(true);
                                draft.setVisible(false);
                            }
                            publish.displayInWicket(this);
                        }

                        @Override
                        protected void onSave(String text, String dir, boolean autosave) {
                            if (text.trim().equals("")) {
                                return;
                            }
                            obj.setContent(text);
                            obj.setDir(MessageFacade.convertDir(dir));
                            Result r = obj.save();
                            if (!autosave) {
                                r.displayInWicket(wmc);
                            }
                        }

                        @Override
                        protected void onAjax(AjaxRequestTarget target) {
                            ViewThread.this.onAjax(target);
                            target.add(li);
                            target.add(feedback);

                        }

                        @Override
                        public void onDiscard(AjaxRequestTarget target) {
                            Result discard = obj.discard();
                            if (discard.result) {
                                if (obj.isThreadDeleted()) {
                                    onClose(target);
                                } else {
                                    li.setVisible(false);
                                }
                            } else {
                                discard.displayInWicket(this);
                            }

                        }
                    };
                    draft.add(draftPanel);
                } else {

                    draft.setVisible(false);
                }

            }
        };
        wmc.add(thread);
        thread.setReuseItems(true);
        ReplyPanel reply = new ReplyPanel("reply", Model.of(""), Model.of("ltr")) {
            MessageFacade mf = null;
            boolean published = false;
            boolean discard = false;

            @Override
            public boolean isVisible() {
                ThreadFacade tf = (ThreadFacade) ViewThread.this.getDefaultModelObject();
                if (!(tf.isDraft() && !tf.getMessages().isEmpty())) {
                    noreply = false;
                    return true;
                } else {
                    noreply = true;
                    return false;
                }
            }

            @Override
            protected void onSend(String text, String dir) {
                if (text.trim().equals("")) {
                    return;
                }
                if (mf == null) {
                    ResultWithObject<MessageFacade> ans = getObj().reply(
                            getObj().getMe(), getObj().getParticipant(), text,
                            MessageFacade.convertDir(dir));
                    mf = ans.object;
                    ans.displayInWicket(this);
                    if (ans.result == true) {
                        published = true;
                        setDefaultModelObject("");
                        mf = null;
                    }
                } else {
                    mf.setContent(text);
                    Result publish = mf.publish();
                    publish.displayInWicket(wmc);
                    if (publish.result == true) {
                        published = true;
                        setDefaultModelObject("");
                        mf = null;
                    }
                }
            }

            @Override
            protected void onSave(String text, String dir, boolean autosave) {
                if (text.trim().equals("")) {
                    return;
                }
                if (mf == null) {
                    ResultWithObject<MessageFacade> ans = getObj().createMessage(
                            getObj().getMe(), getObj().getParticipant(), text,
                            MessageFacade.convertDir(dir));
                    mf = ans.object;
                    if (!autosave) {
                        ans.displayInWicket(wmc);
                    }
                } else {
                    mf.setContent(text);
                    Result save = mf.save();
                    if (!autosave) {
                        save.displayInWicket(wmc);
                    }
                }
            }

            @Override
            protected void onAjax(AjaxRequestTarget target) {
                if (published || discard) {
                    published = false;
                    discard = false;
                    target.add(wmc);
                    target.add(this);
                }
                ViewThread.this.onAjax(target);
                target.add(feedback);
            }

            @Override
            public void onDiscard(AjaxRequestTarget art) {
                if (mf != null) {
                    mf.discard();
                    mf = null;
                    getObj().getMessages().remove(getObj().getMessages().size() - 1);
                }
                discard = true;
                setDefaultModelObject("");
            }
        };
        add(reply);
        reply.setOutputMarkupId(true);

    }

    ThreadFacade getObj() {
        return (ThreadFacade) getDefaultModelObject();
    }

    protected abstract void onAjax(AjaxRequestTarget art);

    protected abstract void onNewer(AjaxRequestTarget target);

    protected abstract boolean hasNewer();

    protected abstract boolean hasOlder();

    protected abstract void onOlder(AjaxRequestTarget target);

    protected abstract void onClose(AjaxRequestTarget target);
}
