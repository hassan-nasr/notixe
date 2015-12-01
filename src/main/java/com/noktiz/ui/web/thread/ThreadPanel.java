/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.thread;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.component.TabAjax;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hossein
 */
public final class
ThreadPanel extends Panel {

    final WebMarkupContainer threadContainer;
    WebMarkupContainer feedbackWMC;
    ThreadProvider currentThreadProvider;
    ThreadList currentThreadList;
    int currentThread;
    TabAjax tabAjax;
    TabAjax.ITabAjaxProvider inbox;
    TabAjax.ITabAjaxProvider draft;
    TabAjax.ITabAjaxProvider compose;
    boolean usePageParameter = true;
    boolean useNavigation = true;
    ThreadPage homePage;

    public ThreadPanel(String id, final ThreadPage homePage) {
        super(id);
        this.homePage = homePage;
        feedbackWMC = new WebMarkupContainer("feedbackwmc");
        add(feedbackWMC);
        feedbackWMC.setOutputMarkupId(true);
        NotificationFeedbackPanel feedback = new NotificationFeedbackPanel("feedback");
        feedbackWMC.add(feedback);
        threadContainer = new WebMarkupContainer("threadContainer");
        add(threadContainer);
        threadContainer.setOutputMarkupId(true);
//        ThreadProvider threadProvider = new ThreadProviderInbox(((UserSession) getSession()).getUser());
//        currentThreadProvider = threadProvider;
//        ThreadList threadList = new ThreadListImpl("thread", threadProvider);
//        currentThreadList = threadList;
//        threadContainer.add(threadList);
        inbox = new TabAjax.ITabAjaxProvider() {
            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                homePage.shortenThreads(target);
                IRequestParameters rp = RequestCycle.get().getRequest().getRequestParameters();
                Set<String> pn = rp.getParameterNames();
                boolean successToGoToThread = false;
                ThreadFacade threadFacade = null;
                if (pn.contains("threadId") && usePageParameter) {
                    usePageParameter = false;
                    StringValue threadId = rp.getParameterValue("threadId");
                    try {
                        Long threadIdStr = threadId.toOptionalLong();
                        UserFacade user = ((UserSession) getSession()).getUser();
                        ResultWithObject<ThreadFacade> thread = user.getThread(threadIdStr);
                        if (thread != null) {
                            threadFacade = thread.object;
                        }
                        successToGoToThread = thread.result;
                    } catch (StringValueConversionException ex) {
                        successToGoToThread = false;
                    }
                }
                ThreadProvider threadProvider = new ThreadProviderInbox(((UserSession) getSession()).getUser());
                currentThreadProvider = threadProvider;
                ThreadList threadList = new ThreadListImpl("thread", threadProvider);
                currentThreadList = threadList;
                threadContainer.addOrReplace(threadList);
                useNavigation = true;
                if (successToGoToThread) {
                    threadList.onSelectThread(target, threadFacade, -1);
                    useNavigation = false;
                }
                if (target != null) {
                    target.add(threadContainer);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("Conversations", null);
            }

            @Override
            public String getIcon() {
                return "icon-comments-alt";
            }

            @Override
            public String getId() {
                return "inbox";
            }
        };
        draft = new TabAjax.ITabAjaxProvider() {
            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                homePage.shortenThreads(target);
                ThreadProvider threadProvider = new ThreadProviderDraft(((UserSession) getSession()).getUser());
                currentThreadProvider = threadProvider;
                ThreadList threadList = new ThreadListImpl("thread", threadProvider);
                currentThreadList = threadList;
                threadContainer.addOrReplace(threadList);
                useNavigation = true;
                if (target != null) {
                    target.add(threadContainer);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("Drafts", null);
            }

            @Override
            public String getIcon() {
                return "icon-file";
            }
            @Override
            public String getId() {
                return "draft";
            }
        };
        compose = new TabAjax.ITabAjaxProvider() {
            @Override
            public boolean onSelect(AjaxRequestTarget target) {
                homePage.shortenThreads(target);
                SendMessage sendMessage;
                IRequestParameters rp = RequestCycle.get().getRequest().getRequestParameters();
                Set<String> pn = rp.getParameterNames();
                if (pn.contains("to") && usePageParameter) {
                    usePageParameter = false;
                    StringValue to = rp.getParameterValue("to");
                    String email = to.toOptionalString();
                    ResultWithObject<UserFacade> load = UserFactory.loadUserWithEmail(email, true);
                    if (load.result) {
                        sendMessage = new SendMessageImpl("thread", load.object.getUser());
                    } else {
                        sendMessage = new SendMessageImpl("thread");
                    }

                } else {
                    sendMessage = new SendMessageImpl("thread");
                }
                useNavigation = true;
                threadContainer.addOrReplace(sendMessage);
                if (target != null) {
                    target.add(threadContainer);
                }
                return true;
            }

            @Override
            public IModel<String> getTitle() {
                return new StringResourceModel("Create.New", null);
            }

            @Override
            public String getIcon() {
                return "icon-pencil";
            }
            @Override
            public String getId() {
                return "new";
            }
        };
        ArrayList<TabAjax.ITabAjaxProvider> tabProviders = new ArrayList<>();
        tabProviders.add(compose);
        tabProviders.add(inbox);
        tabProviders.add(draft);

        tabAjax = new TabAjax("tabs", new Model(tabProviders),new TabAjax.Options(TabAjax.Options.Type.white));
        add(tabAjax);
        IRequestParameters rp = RequestCycle.get().getRequest().getRequestParameters();
        Set<String> pn = rp.getParameterNames();
        if (pn.contains("to")) {
            tabAjax.setActive(compose, false, null);
            tabAjax.setDefaultTab(compose);
        } else {
            tabAjax.setActive(inbox, false, null);
            tabAjax.setDefaultTab(inbox);
        }

    }

    private class ThreadListImpl extends ThreadList {

        public ThreadListImpl(String id, IDataProvider<ThreadFacade> threadProvider) {
            super(id, threadProvider);
        }

        @Override
        public void onSelectThread(AjaxRequestTarget art, ThreadFacade tf, int number) {
            currentThread = number;
            if (tf.isDraft() && tf.getThread().isTargetVisible()) {
                threadContainer.replace(new SendMessageImpl("thread", tf));
                tabAjax.setActive(compose, false, art);
            } else {
                homePage.expandThreads(art, tf);
                threadContainer.replace(new ViewThreadImpl("thread", Model.of(tf)));
            }
            if (art != null) {
                art.add(threadContainer);
            }
        }

        @Override
        protected void onAjax(AjaxRequestTarget art) {
            art.add(feedbackWMC);
        }
    }

    private class ViewThreadImpl extends ViewThread {

        public ViewThreadImpl(String id, IModel<ThreadFacade> tf) {
            super(id, tf);
        }

        @Override
        protected void onAfterRender() {
            super.onAfterRender();
            ThreadFacade tf = (ThreadFacade) getDefaultModelObject();
            if (tf.isUnread()) {
                tf.markAllAsRead();
            }
        }

        @Override
        protected void onAjax(AjaxRequestTarget art) {
            art.add(feedbackWMC);
        }

        @Override
        protected void onNewer(AjaxRequestTarget target) {
            if (currentThread > 0) {
                Iterator<? extends ThreadFacade> iterator = currentThreadProvider.iterator(currentThread - 1, 1);
                if (iterator.hasNext()) {
                    currentThread--;
                    currentThreadList.onSelectThread(target, iterator.next(), currentThread);
                    return;
                }
            }
            onClose(target);

        }

        @Override
        protected void onOlder(AjaxRequestTarget target) {
            Iterator<? extends ThreadFacade> iterator = currentThreadProvider.iterator(currentThread + 1, 1);
            if (iterator.hasNext()) {
                currentThread++;
                currentThreadList.onSelectThread(target, iterator.next(), currentThread);
                return;
            }
            onClose(target);
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            threadContainer.replace(currentThreadList);
            target.add(threadContainer);
            homePage.shortenThreads(target);
        }

        @Override
        protected boolean hasNewer() {
            return useNavigation;
        }

        @Override
        protected boolean hasOlder() {
            return useNavigation;
        }
    }

    private class ThreadProviderInbox extends ThreadProvider {

        public ThreadProviderInbox(UserFacade user) {
            super(user);
        }

        @Override
        protected List<ThreadFacade> getThreadFacade(long first, long count) {
            List<ThreadFacade> myThreadList = user.getMyThreadList(first, count);
            return myThreadList;
        }

        @Override
        public long size() {
            return user.numOfThreads();
        }
    }

    private class ThreadProviderDraft extends ThreadProvider {

        public ThreadProviderDraft(UserFacade user) {
            super(user);
        }

        @Override
        protected List<ThreadFacade> getThreadFacade(long first, long count) {
            List<ThreadFacade> myThreadList = user.getMyDrafts(first, count);
            return myThreadList;
        }

        @Override
        public long size() {
            return user.numOfDrafts();
        }
    }

    class SendMessageImpl extends SendMessage {

        public SendMessageImpl(String id) {
            super(id);
        }

        private SendMessageImpl(String thread, ThreadFacade tf) {
            super(thread, tf);
        }

        public SendMessageImpl(String id, User user) {
            super(id, user);
        }

        @Override
        protected void onAjax(AjaxRequestTarget target) {
            target.add(feedbackWMC);
        }

        @Override
        protected void onFinish(AjaxRequestTarget target) {
            tabAjax.setActive(inbox, true, target);
//            ThreadProvider threadProvider = new ThreadProviderInbox(((UserSession) getSession()).getUser());
//            ThreadList threadList = new ThreadListImpl("thread", threadProvider);
//            currentThreadList = threadList;
//            threadContainer.replace(threadList);
//            target.add(threadContainer);
        }
    }
}
