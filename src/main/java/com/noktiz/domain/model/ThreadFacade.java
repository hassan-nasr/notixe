/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.Message;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.NotificationThanks;
import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.persistance.HSF;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author hossein
 */
public class ThreadFacade implements Serializable {

    private Thread thread;
    private List<MessageFacade> messages = new ArrayList<>();
    private UserFacade me;
    private boolean unread = false;
    private UserFacade participant;
    private UserFacade starter;
    private UserFacade target;
    private Block myBlock;
    private Block participantBlock;
    private Block myBlockBy;
    private Block ParticipantBlockBy;

//    private Block blocked;
    public ThreadFacade(Thread thread, UserFacade me) throws UnauthorizedAccessException {
        me.refresh();
        setMe(me);
        this.thread = thread;
        //set Participant
        UserFacade reallparticipant = null;
        UserFacade anonymousParticipant = null;
        
        if (thread.getTarget() != null && me.getUser().equals(thread.getStarter())) {
            reallparticipant = new UserFacade(thread.getTarget());
            anonymousParticipant = new UserFacade(thread.getTarget(), true);
        }
        else if (thread.getStarter() != null && me.getUser().equals(thread.getTarget())) {
            reallparticipant = new UserFacade(thread.getStarter());
            anonymousParticipant = new UserFacade(thread.getStarter(), true);
        }
        else throw new UnauthorizedAccessException();
        
        if (thread.getStarter().equals(me.getUser())) {
            starter = me;
            myBlock = thread.getStarterBlock();
            participantBlock = thread.getTargetBlock();
            if (thread.isTargetVisible()) {
                setParticipant(reallparticipant);
                ParticipantBlockBy = thread.getTargetBlockedBy();
                target = reallparticipant;
            } else {
                setParticipant(anonymousParticipant);
                target = anonymousParticipant;
            }
        } else {
            target = me;
            myBlock = thread.getTargetBlock();
            participantBlock = thread.getStarterBlock();
            if (thread.isStarterVisible()) {
                starter = reallparticipant;
                ParticipantBlockBy = thread.getStarterBlockedBy();
                setParticipant(reallparticipant);
            } else {
                starter = anonymousParticipant;
                setParticipant(anonymousParticipant);
            }
        }
        this.setTitle(thread.getTitle());
        ArrayList<MessageFacade> ansmf = new ArrayList();
        if(thread.getRelatedRate()!=null){
            Rate relatedRate = thread.getRelatedRate();
            Message question = convertQuestionToMessage(relatedRate.getContext());
            Message answer = convertAnswerToMessage(relatedRate);
            ansmf.add(new MessageFacade(question, me, getParticipant()));
            ansmf.add(new MessageFacade(answer, me, getParticipant()));
        }
        for (Message message : thread.getMessages()) {
            if (message.getBlock() != null && !message.getSender().equals(me.getUser())) {
                continue;
            }
            if (HSF.get().getCurrentSession().contains(thread)) {
                if (message.isDraft() && !message.getSender().equals(me.getUser())) {
                    continue;
                }
            }

            MessageFacade mf = new MessageFacade(message, me, getParticipant());
            if (!mf.isReciverRead() && mf.getReciver().equals(me)) {
                unread = true;
            }
            ansmf.add(mf);
        }
        sortMessages(ansmf);
        setMessages(ansmf);
    }

    public Thread getThread() {
        return thread;
    }

    public UserFacade getStarter() {
        return new UserFacade(this.thread.getStarter());
    }

    public void setStarter(UserFacade starter) {
        thread.setStarter(starter.getUser());
    }

    public String getTitle() {
        return thread.getTitle();
    }

    public void setTitle(String title) {
        thread.setTitle(title);
    }

    public boolean isDraft() {
        return thread.isDraft();
    }

    public void setDraft(boolean draftState) {
        thread.setDraft(draftState);
    }

    public List<MessageFacade> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageFacade> messages) {
        this.messages = messages;
    }

    public UserFacade getMe() {
        return me;
    }

    public void setMe(UserFacade me) {
        this.me = me;
    }

    public UserFacade getParticipant() {
        return participant;
    }

    public void setParticipant(UserFacade participant) {
        this.participant = participant;
    }

    public boolean isHidden() {
        if (participant == null) {
            return false;
        }
        return participant.isAnonymous();
    }

    public Boolean amIvisible(){
        if (getMe().equals(getStarter())){
            return getThread().isStarterVisible();
        }
            return getThread().isTargetVisible();
    }

    public Result publish() {
        Result ans = new Result();
        if (thread.getTarget() == null) {
            ans.result = false;
            ans.messages.add(new Result.Message("message.specify_receiver", Result.Message.Level.error));
            return ans;
        }

        if (!thread.isStarterVisible() && !this.me.canSendMessageTo(participant)) {
            ans.result = false;
            ans.messages.add(new Result.Message("message.receiver_not_add_you", Result.Message.Level.error));
            return ans;
        }
        thread.setDraft(false);
        Block loadBlocked = null;
        if (!thread.isStarterVisible()) {
            loadBlocked = Block.loadBlocked(thread.getStarter(), thread.getTarget());
            if (loadBlocked != null) {
                thread.setTargetBlockedBy(loadBlocked);
                thread.setBlockedcompletely(loadBlocked);
            }
        }
        HSF.get().beginTransaction();
        try {
            thread.save();
            if (loadBlocked != null) {
                loadBlocked.save();
            }
            HSF.get().commitTransaction();
            ans.result = true;
            return ans;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }

    }

    public Result block(UserFacade blocker, boolean blockByItself) {
        refresh();
        Result r = new Result();
        /**
         * check if blocking is possible
         */
        User blocked;
        Block block = null;
        Block blockedBy = null;
        Integer blockType = 0;//0 for targetBlock and 1 for starterBlock
        if (blocker.getUser().equals(thread.getStarter()) && !thread.isTargetVisible()) {
            blocked = thread.getTarget();
            block = thread.getStarterBlock();
            blockedBy = thread.getStarterBlockedBy();
            blockType = 1;

        } else if (blocker.getUser().equals(thread.getTarget()) && !thread.isStarterVisible()) {
            blocked = thread.getStarter();
            block = thread.getTargetBlock();
            blockedBy = thread.getTargetBlockedBy();
            blockType = 0;

        } else {
            r.result = false;
            r.messages.add(new Result.Message("message.can_not_block_visible", Result.Message.Level.error));
            return r;
        }
        {
            if (block != null) {
                r.result = false;
                r.messages.add(new Result.Message("message.thread_was_blocked", Result.Message.Level.error));
                Logger.getLogger(ThreadFacade.class).error("try to block a blocked thread : " + thread.getId());
                return r;
            }
            boolean newBlocking = true;
            if (blockedBy != null) {
                newBlocking = false;
                block = blockedBy;
            }
//              Session cs = HibernateSessionFactory.get().getCurrentSession();
            HSF.get().beginTransaction();
            try {
                if (block == null) {
                    block = new Block();
                    block.setBlocked(blocked);
                    block.setBlocker(blocker.getUser());
                    block.setStartDate(new Date());
                    block.save();
                }
                if (blockType == 0) {
                    thread.setTargetBlock(block);
                    thread.setTargetBlockedBy(block);
                } else if (blockType == 1) {
                    thread.setStarterBlock(block);
                    thread.setStarterBlockedBy(block);
                }
                setMyBlock(block);
                thread.save();
//                Block 
                if (newBlocking) {
                    Block.BlockByAll(blocker, blocked, block);
                }
                /*
                 otherThreads.addAll(Thread.loadThreadWithUsers(thread.getStarter(), thread.getTarget()));
                 for (Thread otherThread : otherThreads) {
                 if (otherThread.getId().equals(thread.getId())) {
                 continue;
                 }

                 otherThread.setBlockedBy(load);
                 otherThread.save();
                 }*/
                HSF.get().commitTransaction();
                r.result = true;
                r.messages.add(new Result.Message("message.user_blocked", Result.Message.Level.success));
                return r;
            } catch (HibernateException ex) {
                HSF.get().roleback();
                Logger.getLogger(this.getClass()).info("HibernateException", ex);
                return new Result(false);
            }

        }
//        r.result = false;
//        r.messages.add(new Result.Message("you can not block this conversion", Result.Message.Level.error));
//        Logger.getLogger(ThreadFacade.class).error("try to block by stranger. thread: " + thread.getId() + " blocker:" + blocker.getEmail());
//        return r;

    }

    public Result unBlock() {
        return unBlock(me);
    }
    public boolean refresh() {
//        if(user.getId()==null)
//            return true;
        try {
            if (!HSF.get().getCurrentSession().contains(thread)) {
                thread=(Thread)HSF.get().getCurrentSession().merge(thread);
            }
            return true;
        } catch (HibernateException ex) {
//            boolean contains = HSF.get().getCurrentSession().contains(user);
            thread = thread.loadThreadWithId(thread.getId());
//            boolean contains1 = HSF.get().getCurrentSession().contains(user);
            return false;
        }
    }
    public Result unBlock(UserFacade unBlocker) {
        Result ans = new Result();
        Integer BlockType = 0;
        User blocked = null;
        Block block = null;
        refresh();
        if (thread.getStarter().equals(unBlocker.getUser())) {
            block = thread.getStarterBlock();
            blocked = thread.getTarget();
            BlockType = 1;
        } else if (thread.getTarget().equals(unBlocker.getUser())) {
            block = thread.getTargetBlock();
            blocked = thread.getTarget();
            BlockType = 0;
        }
        if (block == null) {
            ans.result = false;
            ans.messages.add(new Result.Message("message.thread_not_blocked", Result.Message.Level.error));
            return ans;
        }
        if (BlockType == 0) {
            thread.setTargetBlock(null);
        }
        if (BlockType == 1) {
            thread.setStarterBlock(null);
        }
        setMyBlock(null);
        HSF.get().beginTransaction();
        try {
            thread.save();
            thread.unblockAllMessagesFrom(blocked);
            block.updateState();
            HSF.get().commitTransaction();
            ans.result = true;
            ans.messages.add(new Result.Message("message.unblock_success", Result.Message.Level.success));
            return ans;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public ResultWithObject<MessageFacade> draft(UserFacade sender, UserFacade receiver, String text, Message.DIR dir) {
        HSF.get().getCurrentSession().refresh(thread);

        ResultWithObject<MessageFacade> createMessage = createMessage(sender, receiver, text, dir);
        return createMessage;
    }

    public ResultWithObject<MessageFacade> reply(UserFacade sender, UserFacade receiver, String content, Message.DIR dir) {
        HSF.get().getCurrentSession().refresh(thread);
        HSF.get().beginTransaction();
        try {
            ResultWithObject<MessageFacade> createMessage = createMessage(sender, receiver, content, dir);
            if (createMessage.result == false) {
                return createMessage;
            }
            Result publish = createMessage.object.publish();
            createMessage.messages = publish.messages;
            createMessage.result = publish.result;
            HSF.get().commitTransaction();
            return createMessage;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new ResultWithObject(false);
        }
    }

    public ResultWithObject<MessageFacade> createMessage(UserFacade sender, UserFacade receiver, String text, Message.DIR dir) {
        me.refresh();
        if(!me.canSendMessageTo(receiver))
            return new ResultWithObject<>(false,"not.allowed");
        Message m = new Message();
        m.setSendDate(new Date());
        m.setSender(sender.getUser());
        m.setReciverRead(Boolean.FALSE);
        m.setContent(text);
        m.setDir(dir);
        if (receiver != null) {
            m.setReciver(receiver.getUser());
        }
        m.setDraft(Boolean.TRUE);
        m.setThread(thread);
        HSF.get().beginTransaction();
        try {
            m.save();
            HSF.get().commitTransaction();
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new ResultWithObject<>(false);
        }

        final MessageFacade mf = new MessageFacade(m, me, receiver);
        messages.add(mf);
        thread.getMessages().add(m);
        ResultWithObject ans = new ResultWithObject();
        ans.result = true;
        ans.object = mf;
        ans.messages.add(new Result.Message("message.message_saved", Result.Message.Level.success));
        return ans;

    }

    private void sortMessages(ArrayList<MessageFacade> ansmf) {
        Collections.sort(ansmf, new Comparator<MessageFacade>() {
            @Override
            public int compare(MessageFacade o1, MessageFacade o2) {
                return o1.getSendDate().compareTo(o2.getSendDate());
            }
        });
    }

    public Result save() {
        HSF.get().beginTransaction();
        try {
            thread.save();
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public void setTarget(UserFacade reciver) {
        if (reciver != null) {
            participant = new UserFacade(reciver.getUser(),!thread.isTargetVisible());
            thread.setTarget(reciver.getUser());
        } else {
            thread.setTarget(null);
        }
    }

    public Result delete() {
        thread = (Thread) HSF.get().getCurrentSession().merge(thread);
        HSF.get().beginTransaction();
        try {
            for (Message message : thread.getMessages()) {
                message.delete();
            }
            thread.delete();
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }

    }

    public Result markAllAsRead() {
        HSF.get().beginTransaction();
        //@todo use sql query instead of for
        try {
            for (MessageFacade messageFacade : messages) {
                if (messageFacade.getReciver().equals(me) && !messageFacade.isReciverRead()) {
                    messageFacade.markAsRead();
                }
            }
            HSF.get().commitTransaction();
            unread = false;
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public boolean isUnread() {
        return unread;
    }

    public boolean isBlockedCompletely() {
        return thread.getBlockedcompletely() != null;
    }

    public Result thanks() {
        NotificationThanks notificationThanks;
        if (participant.equals(starter)) {
            thread.setTargetThanks(true);
            thread.setTargetThanksDate(new Date());
        } else if (me.equals(starter)) {
            thread.setStarterThanks(true);
            thread.setStarterThanksDate(new Date());
        }
        notificationThanks = new NotificationThanks(thread, getParticipant().getUser(), getMe().getUser());
        try {
            save();
//            HSF.get().beginTransaction();
            notificationThanks.create();
//            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public boolean isTargetThanks() {
        return thread.isTargetThanks();
    }

    public Long getId() {
        return thread.getId();
    }

    public void setRelatedRate(Rate relatedRate) {
        thread.setRelatedRate(relatedRate);
    }

    public Rate getRelatedRate() {
        return thread.getRelatedRate();
    }

    public Block getMyBlock() {
        return myBlock;
    }

    public void setMyBlock(Block myBlock) {
        this.myBlock = myBlock;
    }

    public Block getParticipantBlock() {
        return participantBlock;
    }

    public void setParticipantBlock(Block participantBlock) {
        this.participantBlock = participantBlock;
    }

    public Block getParticipantBlockBy() {
        return ParticipantBlockBy;
    }

    public void setParticipantBlockBy(Block ParticipantBlockBy) {
        this.ParticipantBlockBy = ParticipantBlockBy;
    }

    public boolean didIThank() {
        if (starter.equals(me)) {
            return thread.isStarterThanks();
        }
        if (target.equals(me)) {
            return thread.isTargetThanks();
        }
        return false;
    }

    public boolean canIThank(UserFacade user) {
        if(thread.isDraft())
            return false;
        if (user.equals(starter)) {
            Boolean ans = thread.isStarterCanBeThanked();
            if (ans != null) {
                return ans;
            }
            return true;
        }
        if (user.equals(target)) {
            Boolean ans = thread.isTargetCanBeThanked();
            if (ans != null) {
                return ans;
            }
            return true;
        }
        return false;
    }

    public boolean doParticipantThank() {
        if (starter.equals(participant)) {
            return thread.isStarterThanks();
        }
        if (target.equals(participant)) {
            return thread.isTargetThanks();
        }
        return false;
    }

    public Result becomeVisible() {
        refresh();
        Result ans = new Result();
        if (me.equals(starter)) {
            if (!thread.isStarterVisible()) {
                thread.setStarterVisible(true);
                Rate relatedRate = thread.getRelatedRate();
                HSF.get().beginTransaction();
                try {
                    if(relatedRate!=null){
                        relatedRate.setShowSender(true);
                        new RateManager().save(relatedRate);
                    }
                    this.unBlock(target);
                    thread.unblockAllMessagesFrom(me.getUser());
                    thread.setTargetBlockedBy(null);
                    thread.setBlockedcompletely(null);
                    setParticipantBlockBy(null);
                    setParticipantBlock(null);
                    thread.save();
                    ans.result = true;
                    HSF.get().commitTransaction();
                    ans.messages = Arrays.asList(new Result.Message("message.you_are_visible",
                            Result.Message.Level.success));
                } catch (HibernateException ex) {
                    Logger.getLogger(ThreadFacade.class).error("Hibernate exception thread id= " + thread.getId(), ex);
                    ans.result = false;
                    ans.messages = Arrays.asList(new Result.Message("message.unknown_problem",
                            Result.Message.Level.error));
                }
                return ans;
            } else {
                ans.result = false;
                ans.messages = Arrays.asList(new Result.Message("message.you_was_visible",
                        Result.Message.Level.error));
                return ans;
            }
        }
        if (me.equals(target)) {
            if (!thread.isTargetVisible()) {
                thread.setTargetVisible(true);
                Rate relatedRate = thread.getRelatedRate();
                HSF.get().beginTransaction();
                try {
                    if(relatedRate!=null){
                        relatedRate.setShowSender(true);
                        new RateManager().save(relatedRate);
                    }
                    this.unBlock(starter);
                    thread.unblockAllMessagesFrom(me.getUser());
                    thread.setStarterBlockedBy(null);
                    thread.setBlockedcompletely(null);
                    setParticipantBlockBy(null);
                    setParticipantBlock(null);
                    save();
                    HSF.get().commitTransaction();
                    ans.result = true;
                    ans.messages = Arrays.asList(new Result.Message("message.you_are_visible",
                            Result.Message.Level.success));
                } catch (HibernateException ex) {
                    Logger.getLogger(ThreadFacade.class).error("Hibernate exception thread id= " + thread.getId(), ex);
                    ans.result = false;
                    ans.messages = Arrays.asList(new Result.Message("message.unknown_problem",
                            Result.Message.Level.error));
                }
                return ans;
            } else {
                ans.result = false;
                ans.messages = Arrays.asList(new Result.Message("message.you_was_visible",
                        Result.Message.Level.error));
                return ans;
            }
        }
        ans.result = false;
        ans.messages = Arrays.asList(new Result.Message("message.bad_request",
                Result.Message.Level.error));
        return ans;
    }

    private Message convertQuestionToMessage(RateContext rateContext) {
        Message message = new Message();
        String description = rateContext.getTitle();
        if(description==null|| description.equals("")){
            description="<No Description>";
        }
        message.setContent(description);
        message.setSendDate(rateContext.getCreationDate());
        message.setSender(rateContext.getUser());
        message.setDraft(false);
        message.setReciverRead(true);
        return message;
    }

    private Message convertAnswerToMessage(Rate relatedRate) {
        Message message = new Message();
        String description = relatedRate.getComment();
        if( description==null){
            description="<No Comment>";
        }
        message.setContent(description);
        message.setSendDate(relatedRate.getDate());
        message.setSender(relatedRate.getSender());
        message.setDraft(false);
        message.setReciverRead(true);
        return message;
    };
}
