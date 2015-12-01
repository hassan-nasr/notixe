package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.model.ThreadFacade;

import java.util.Date;

/**
 * Created by hassan on 30/11/2015.
 */
public class ThreadView {
    Long conversationId;
    Boolean amIAnonymous;
    Boolean isPartnerAnonymous;
    Long partnerId;
    Integer messageCount;
    Boolean thanked;
    Boolean canIThank;
    String blocked;
    Long relatedRateId;
    String title;
    Boolean isUnread;
    Date lastMessage;
    Integer unreadCount;
    String lastMessageSender;
    Boolean isDraft;

    public ThreadView(ThreadFacade threadFacade) {

        conversationId = threadFacade.getId();
        amIAnonymous = !threadFacade.amIvisible();
        isPartnerAnonymous = threadFacade.getParticipant().isAnonymous();
        if(!isPartnerAnonymous)
            partnerId = threadFacade.getParticipant().getUser().getId();
        messageCount = threadFacade.getMessages().size();
        isUnread = threadFacade.isUnread();
        thanked = threadFacade.didIThank() || threadFacade.isTargetThanks();
        canIThank = threadFacade.canIThank(threadFacade.getParticipant());
        Block myBlock = threadFacade.getMyBlock();
        Block otherBlock = threadFacade.getParticipantBlock();
        if(myBlock!=null && !myBlock.isDeleted()){
            blocked="IBlocked";
        }
        else if(otherBlock != null && !otherBlock.isDeleted())
            blocked="OtherBlocked";
        else
            blocked="False";
        title = threadFacade.getTitle();
        final Rate relatedRate = threadFacade.getRelatedRate();
        if(relatedRate !=null)
            relatedRateId = relatedRate.getId();
        isDraft = threadFacade.isDraft();
    }

    public Boolean getDraft() {
        return isDraft;
    }

    public void setDraft(Boolean draft) {
        isDraft = draft;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Boolean getAmIAnonymous() {
        return amIAnonymous;
    }

    public void setAmIAnonymous(Boolean amIAnonymous) {
        this.amIAnonymous = amIAnonymous;
    }

    public Boolean getPartnerAnonymous() {
        return isPartnerAnonymous;
    }

    public void setPartnerAnonymous(Boolean partnerAnonymous) {
        isPartnerAnonymous = partnerAnonymous;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Boolean getThanked() {
        return thanked;
    }

    public void setThanked(Boolean thanked) {
        this.thanked = thanked;
    }

    public Boolean getCanIThank() {
        return canIThank;
    }

    public void setCanIThank(Boolean canIThank) {
        this.canIThank = canIThank;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public Long getRelatedRateId() {
        return relatedRateId;
    }

    public void setRelatedRateId(Long relatedRateId) {
        this.relatedRateId = relatedRateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getUnread() {
        return isUnread;
    }

    public void setUnread(Boolean unread) {
        isUnread = unread;
    }

    public Date getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Date lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessageSender() {
        return lastMessageSender;
    }

    public void setLastMessageSender(String lastMessageSender) {
        this.lastMessageSender = lastMessageSender;
    }
}
