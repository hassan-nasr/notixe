package com.noktiz.ui.web.friend.fb;

import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.social.FriendProvider;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.friend.SocialPerson;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.io.IOException;
import java.util.*;

/**
 * Created by hasan on 7/21/14.
 */
public class SocialFriendListViewer extends PersonListProvider {
    ArrayList<SocialConnection> registeredFacebookFriends;
    List<SocialConnection> facebookFriends;
//    List<IPerson> persons = new ArrayList<>();
    UserFacade owner;
    public SocialFriendListViewer(UserFacade userFacade, FriendProvider facebookFriendProvider) {

        userFacade.refresh();
        this.owner = userFacade;
        try {
            facebookFriends = userFacade.fetchSocialConnections(facebookFriendProvider);
            List<SocialConnection> notRegisterd = new ArrayList<>();
            for (SocialConnection facebookFriend : facebookFriends) {
                if(facebookFriend.isRegistered())
                    addPerson(new SocialRegisteredPerson(owner,facebookFriend));
                else
                    notRegisterd.add(facebookFriend);

            }
            for (SocialConnection o : notRegisterd) {
                addPerson(new SocialPerson(owner,o));
            }
            addAction(new IAction() {
                @Override
                public IModel<String> getActionTitle() {
                    return Model.of(new StringResourceModel("addOrInviteAllSelected",null));
                }

                @Override
                public boolean onAction(AjaxRequestTarget art, Component caller) {
                    Result result = new Result();
                    int invitedCount=0;
                    int addedCount=0;
                    boolean found=false;
                    for (IPerson<SocialConnection> iPerson : getPersons()) {

                        if(iPerson.isSelect().getObject()){
                            found = true;
                            if(iPerson instanceof SocialPerson) {
                                if(((SocialPerson) iPerson).getObj().isInvited()) {
                                    continue;
                                }
                                Result inviteResult = ((SocialPerson) iPerson).invite();
                                if(!inviteResult.result)
                                    if(inviteResult.messages.size() == 0)
                                        result.messages.add(new Result.Message("message.invite_failed", Result.Message.Level.error,iPerson.getTitle().getObject()));
                                    else
                                        result.messages.addAll(inviteResult.messages);
                                else
                                    invitedCount++;
                            }
                            else if (iPerson instanceof SocialRegisteredPerson) {
                                if(((SocialRegisteredPerson) iPerson).getObj().isInvited()) {
                                    continue;
                                }
                                Result addResult = ((SocialRegisteredPerson) iPerson).add();
                                if(!addResult.result)
                                    if(addResult.messages.size() == 0)
                                        result.messages.add(new Result.Message("message.unknown_error_contact", Result.Message.Level.error));
                                    else
                                        result.messages.addAll(addResult.messages);
                                else
                                    addedCount++;
                            }
                            iPerson.isSelect().setObject(false);
                        }
                    }
                    if(!found)
                        result.messages.add(new Result.Message("message.select_friend_click", Result.Message.Level.info));
                    if(invitedCount>0)
                        result.messages.add(new Result.Message("message.invite_numbers", Result.Message.Level.success,invitedCount));
                    if(addedCount>0)
                        result.messages.add(new Result.Message("message.connect_to_number_of_people", Result.Message.Level.success,addedCount));
                    if(addedCount ==0 && invitedCount ==0)
                        result.messages.add(new Result.Message("message.done", Result.Message.Level.success));
                    result.displayInWicket(caller);

                    return true;

                }
            });
        } catch (AccessDeniedException e) {
            Logger.getLogger(this.getClass()).error(e);
        } catch (IOException e) {
            Logger.getLogger(this.getClass()).error(e);
        }

    }


}
