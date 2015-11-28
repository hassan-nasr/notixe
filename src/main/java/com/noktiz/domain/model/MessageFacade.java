/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.Message;
import com.noktiz.domain.entity.Message.DIR;
import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.NotificationNewMessage;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.social.FacebookConnect;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author hossein
 */
public class MessageFacade implements Serializable {

    Message message;
    private UserFacade sender;
    private UserFacade reciver;
    private UserFacade me;
    private boolean threadDeleted = false;

    MessageFacade(Message message, UserFacade me, UserFacade part) {
        this.message = message;
        this.me = me;
        this.setContent(message.getContent());
        this.setSendDate(message.getSendDate());
        //set Sender and Receiver
        if (message.getSender().equals(me.getUser())) {
            this.setSender(me);
            this.setReciver(part);
        } else {
            this.setSender(part);
            this.setReciver(me);
        }
    }

    public String getContent() {
        return message.getContent();
    }

    public void setContent(String content) {
        message.setContent(content);
    }

    public Block getBlock() {
        return message.getBlock();
    }

    public String getDir() {
        if (message.getDir() == null) {
            return Message.DIR.ltr.toString();
        }
        return message.getDir().toString();
    }

    public static Message.DIR convertDir(String dir) {
        try {
            final Message.DIR dir1 = Message.DIR.valueOf(dir);
            return dir1;
        } catch (IllegalArgumentException ex) {
            return DIR.ltr;
        }
    }

    public void setDir(Message.DIR dir) {
        message.setDir(dir);
    }

    public boolean isDraft() {
        return message.isDraft();
    }

    public void setDraft(boolean draft) {
        message.setDraft(draft);
    }
    Calendar sendDateCalendar = null;

    public Date getSendDate() {
        return message.getSendDate();
//        if (sendDateCalendar == null) {
//            final Session cs = HSF.get().getCurrentSession();
//            Boolean b = cs.contains(me.getUser());
//            if (b == false) {
//                me.refresh();
//            }
//            
//            final String timezone = me.getPersonalInfo().getTimezone();
//            if(!timezone.equals(PersonalInfo.automaticTimezone)){
//                sendDateCalendar = TimeManager.changeTimezone(message.getSendDate(), timezone);
//            }
//            else{
//                final UserSession userSession = (UserSession)(org.apache.wicket.Session.get());
//                sendDateCalendar = TimeManager.changeTimezone(message.getSendDate(), userSession.getTimezoneOffset());
//            }
//        }
//        return sendDateCalendar;
    }

    public void setSendDate(Date sendDate) {
        message.setSendDate(sendDate);
        sendDateCalendar = null;
    }

    public UserFacade getSender() {
        return sender;
    }

    public void setSender(UserFacade sender) {
        this.sender = sender;
    }

    public UserFacade getReciver() {
        return reciver;
    }

    public void setReciver(UserFacade reciver) {
        this.reciver = reciver;
        if (reciver != null) {
            message.setReciver(reciver.getUser());
        } else {
            message.setReciver(null);
        }
    }

    public Boolean isReciverRead() {
        if (me.equals(this.reciver)) {
            return message.isReciverRead();
        }
        return true;
    }

    public void setReciverRead(Boolean reciverRead) {
        message.setReciverRead(reciverRead);
    }

    public Result publish() {
        Result ans = new Result();
//        HSF.get().getCurrentSession().refresh(message);
        if (message.getContent().matches("\\s*")){
            ans.result = false;
            ans.messages.add(new Result.Message("message.can_not_send_empty_message", Result.Message.Level.error));
            return ans;
        }
        if (message.getReciver() == null) {
            ans.result = false;
            ans.messages.add(new Result.Message("message.specify_receiver", Result.Message.Level.error));
            message.save();
            return ans;
        }
        Message oldMessage;
        try {
            oldMessage = message;
            HSF.get().getCurrentSession().evict(message);
            message = (Message) HSF.get().getCurrentSession().merge(message);
        } catch (HibernateException ex) {
            Logger.getLogger(MessageFacade.class).error(ex);
            ans.result = false;
            return ans;
        }
        Thread thread = message.getThread();
//        thread =(Thread) HSF.get().getCurrentSession().load(Thread.class, thread.getId());
//        message.setThread(thread);

        if ((thread.getTargetBlock() != null && message.getSender().equals(thread.getTarget()))
                || thread.getStarterBlock() != null && message.getSender().equals(thread.getStarter())) {
            ans.result = false;
            ans.messages.add(new Result.Message("message.can_not_send_in_block_thread", Result.Message.Level.error));
            message.save();
            return ans;
        }
        boolean annonymous = false;
        if (thread.getStarter().equals(message.getSender())) {
            thread.unblockAllMessagesFrom(thread.getTarget());
            if (thread.getTargetBlockedBy() != null && thread.getTargetBlock() == null) {
                boolean canSend;
                canSend = thread.canStarterSendMoreMessage();
                if (!canSend) {
                    message.setBlock(thread.getTargetBlockedBy());
                }
            } else {
                message.setBlock(thread.getTargetBlockedBy());
            }
            annonymous = !thread.isStarterVisible();
        }
        if (thread.getTarget().equals(message.getSender())) {
            thread.unblockAllMessagesFrom(thread.getStarter());
            if (thread.getStarterBlockedBy() != null && thread.getStarterBlock() == null) {
                boolean canSend;
                canSend = thread.canTargetSendMoreMessage();
                if (!canSend) {
                    message.setBlock(thread.getStarterBlockedBy());
                }
            } else {
                message.setBlock(thread.getStarterBlockedBy());
            }
            annonymous = !thread.isTargetVisible();
        }
        message.setDraft(Boolean.FALSE);
        NotificationNewMessage nnm = null;
        if (message.getBlock() == null) {
            nnm = new NotificationNewMessage(message, message.getReciver(), annonymous);
        }
        setSendDate(new Date());
        HSF.get().beginTransaction();
        try {
            message.setNotification(nnm);
            if (thread.isDraft()) {
                thread.setDraft(false);
                thread.save();
            }
            if (nnm != null) {
                nnm.create();
            }
            message.save();
            HSF.get().commitTransaction();
            ans.result = true;
            ans.messages.add(new Result.Message("message.message_sent", Result.Message.Level.success));
            oldMessage.setBlock(message.getBlock());
            oldMessage.setDraft(message.isDraft());
            return ans;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public Result save() {
        HSF.get().beginTransaction();
        Result ans = new Result();
        try {
            message.save();
            ans.result = true;
            ans.messages.add(new Result.Message("message.message_saved", Result.Message.Level.success));
            HSF.get().commitTransaction();
            return ans;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("InternalException", ex);
            return new Result(false);
        }

    }

    public Result discard() {
        HSF.get().beginTransaction();
        try {
            message.delete();
            if (message.getThread().getMessages().size() == 1) {
                message.getThread().delete();
                threadDeleted = true;
            }
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    Result markAsRead() {
        refresh();
        message.setReciverRead(Boolean.TRUE);
        NotificationNewMessage notification = message.getNotification();
        HSF.get().beginTransaction();
        try {
            if (notification != null) {
                notification.markAsRead();
            }
            message.save();
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public boolean isThreadDeleted() {
        return threadDeleted;
    }

    private void refresh() {
        final Session currentSession = HSF.get().getCurrentSession();
        if (!currentSession.contains(message)) {
            message = (Message) currentSession.merge(message);
        }
    }

}
