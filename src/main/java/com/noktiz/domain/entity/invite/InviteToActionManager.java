package com.noktiz.domain.entity.invite;

import com.noktiz.domain.model.BaseManager;

/**
 * Created by hasan on 2014-11-03.
 */
public class InviteToActionManager extends BaseManager{
    public void sendInvite(InviteToAction inviteToAction){
        saveAsNew(inviteToAction);
    }
}
