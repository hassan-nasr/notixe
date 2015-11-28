/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.thread;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.MessageFacade;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.behavior.CallPeriodically;
import com.noktiz.ui.web.behavior.PressKeyEnableBehavior;
import com.noktiz.ui.web.behavior.SaveBehavior;
import com.noktiz.ui.web.behavior.Select2Behavior;
import com.noktiz.ui.web.component.*;

import java.util.*;

import com.noktiz.ui.web.component.typeAhead.TypeAheadBehavior;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public abstract class SendMessage extends Panel {

    TextField receiver;
    TextField<String> title;
    private TextArea<String> message;
    String messageObj = "";
    String titleObj = "";
    String dirObj = "ltr";
    User receiverObj;
    UserFacade userFacade;
    MessageFacade mf;
    ThreadFacade tf;
    Map<String, User> idToUser;
    List<String> othersFriends;
    ChangePropertyInput dir;
    public SendMessage(String id, ThreadFacade tf) {
        super(id);
        if (tf != null) {
            if (tf.getParticipant() != null) {
                receiverObj = tf.getParticipant().getUser();
            }
            if (tf.getTitle() != null) {
                titleObj = tf.getTitle();
            }
            if (!tf.getMessages().isEmpty()) {
                messageObj = tf.getMessages().get(0).getContent();
                dirObj=tf.getMessages().get(0).getDir();
                mf = tf.getMessages().get(0);
            }
            this.tf = tf;
        }

        init();
    }

    public SendMessage(String id) {
        super(id);
        init();
    }
    public SendMessage(String id,User user){
        super(id);
        receiverObj=user;
        init();
    }

    private void init() {
        Form form;
        userFacade = ((UserSession) getSession()).getUser();
        userFacade.refresh();
        form = new Form("form");
        add(form);
        List<Friendship> othersFriendships = userFacade.getOthersFriends();
        List<User>receiverUsers= new ArrayList<>();
        for (Friendship f:othersFriendships){
            receiverUsers.add(f.getFriendshipOwner());
        }
        receiverUsers.add(null);
        DropDownChoice<User> receiver= new DropDownChoice<User>("receiver",new IModel<User>() {
            @Override
            public User getObject() {
                return receiverObj;
            }

            @Override
            public void setObject(User user) {
                receiverObj=user;
            }

            @Override
            public void detach() {
            }
        },receiverUsers,new IChoiceRenderer() {
            @Override
            public Object getDisplayValue(Object o) {
                if(o==null){
                    return "";
                }
                return userToDisplayText((User)o);
            }

            @Override
            public String getIdValue(Object o, int i) {
                if(o == null)
                    return "null";
                return ((User) o).getId().toString();
//                return String.valueOf(i);
            }
        });
//        receiver = new TextField("receiver", new IModel() {
//            @Override
//            public Object getObject() {
//                return userToDisplayText(receiverObj);
//            }
//
//            @Override
//            public void setObject(Object object) {
//                receiverObj = idToUser.get(object);
//            }
//
//            @Override
//            public void detach() {
//            }
//        });
//        receiver.add(new TypeAheadBehavior() {
//            @Override
//            protected Collection<String> getChoices(String userInput) {
//                return idToUser.keySet();
//            }
//        });
        form.add(receiver);
        receiver.setNullValid(true);
        receiver.add(new Select2Behavior());
        String enableVar = RandomStringUtils.randomAlphabetic(10);
        String saveEnableVar = RandomStringUtils.randomAlphabetic(10);
        title = new TextField<String>("title", new IModel<String>() {
            @Override
            public String getObject() {
                return titleObj;
            }

            @Override
            public void setObject(String object) {
                titleObj = object;
            }

            @Override
            public void detach() {
            }
        });
        form.add(title);
        title.add(new PressKeyEnableBehavior(enableVar));
        message = new AutoGrowTextArea("message", new IModel<String>() {
            @Override
            public String getObject() {
                return messageObj;
            }

            @Override
            public void setObject(String object) {
                messageObj = object;
            }

            @Override
            public void detach() {
            }
        });
        form.add(message);
        message.add(new AttributeModifier("dir", dirObj));
        dir = new ChangePropertyInput("dir", new Model(), message, "dir");
        form.add(dir);
        message.add(new PressKeyEnableBehavior(enableVar));
        AjaxSubmitLink send = new IndicatingAjaxSubmitLink2("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target,form);
                try {
                    UserFacade reciver = getReciver();
                    if (tf == null) {
                        ResultWithObject<ThreadFacade> createThread = userFacade.createThread(reciver, getTitle());
                        if (!createThread.result) {
                            createThread.displayInWicket(this);
                            return;
                        }
                        tf = createThread.object;
                    } else {
                        tf.setTitle(getTitle());
                        tf.setTarget(reciver);
                        Result save = tf.save();
                        if (!save.result) {
                            error(getString("errorSavingMessage"));
                        }
                    }

                    if (mf == null) {
                        ResultWithObject<MessageFacade> createMessage = tf.createMessage(
                                userFacade, reciver, getMessage(),
                                MessageFacade.convertDir(dir.getDefaultModelObjectAsString()));
                        if (createMessage.result) {
                            mf = createMessage.object;
                        } else {
                            createMessage.displayInWicket(this);
                            return;
                        }
                    } else {
                        mf.setContent(getMessage());
                        mf.setDir(MessageFacade.convertDir(dir.getDefaultModelObjectAsString()));
                        mf.setReciver(reciver);
                    }
                    Result publish1 = tf.publish();
                    publish1.displayInWicket(this);
                    if (publish1.result) {
                        Result publish = mf.publish();
                        if (!publish.result) {
                            publish.displayInWicket(this);
                        } else {
                            Component parent =SendMessage.this.getParent();
                            parent.success(getString("messageSend"));
                            onFinish(target);
                        }
                    }

                } finally {
                    onAjax(target);
                }
            }
        };
        form.add(send);

        AjaxSubmitLink save = new IndicatingAjaxSubmitLink2("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target,form);
                try {
                    UserFacade reciver = getReciver();
                    if (tf == null) {
                        ResultWithObject<ThreadFacade> createThread = userFacade.createThread(reciver, getTitle());
                        if (!createThread.result) {
                            createThread.displayInWicket(this);
                            return;
                        }
                        tf = createThread.object;
                    } else {
                        tf.setTitle(getTitle());
                        tf.setTarget(reciver);
                        Result saveState = tf.save();
                        if (!saveState.result) {
                            error(getString("errorSavingMessage"));
                        }
                    }

                    if (mf == null) {
                        ResultWithObject<MessageFacade> createMessage = tf.createMessage(
                                userFacade, reciver, getMessage(),
                                MessageFacade.convertDir(dir.getDefaultModelObjectAsString()));
                        if (createMessage.result) {
                            mf = createMessage.object;
                        } else {
                            createMessage.displayInWicket(this);
                            return;
                        }
                    } else {
                        mf.setContent(getMessage());
                        mf.setDir(MessageFacade.convertDir(dir.getDefaultModelObjectAsString()));
                        mf.setReciver(reciver);
                    }
                    Result r = mf.save();

//                    r.displayInWicket(form);
                } finally {
                    target.add(this);
                    onAjax(target);
                }
            }
        };
        form.add(save);
        save.add(new CallPeriodically(20000, saveEnableVar, true,enableVar));
        save.setOutputMarkupId(true);
        save.add(new SaveBehavior(enableVar,saveEnableVar));
        AjaxSubmitLink discard = new IndicatingAjaxSubmitLink2("discard") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (tf != null) {
                    tf.delete();
                    tf=null;
                }
                Component parent =SendMessage.this.getParent();
                parent.info(getString("MessageDiscarded"));
                onAjax(target);
                onFinish(target);
                
            }
        };
        form.add(discard);
    }

    private String getTitle() {
        return title.getDefaultModelObjectAsString();
    }

    private String getMessage() {
        return message.getDefaultModelObjectAsString();
    }

    private UserFacade getReciver() {
        User reciverStr = (User) receiverObj;
        return new UserFacade(reciverStr);
//        if (reciverStr == null) {
//            return null;
//        }
//        return UserFactory.loadUserWithEmailId(EmailAddressUtils.normalizeEmail(reciverStr.getEmail()),true).object;
    }

    protected abstract void onAjax(AjaxRequestTarget target);

    protected abstract void onFinish(AjaxRequestTarget target);

    private String userToDisplayText(User user) {
        if (user != null) {
            return user.getName() + " (" + user.getEmail() + ")";
        }
        return "";
    }
}
