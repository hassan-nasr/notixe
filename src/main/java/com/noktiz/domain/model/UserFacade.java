/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import com.noktiz.domain.Utils.SolrUtils;
import com.noktiz.domain.entity.*;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.entity.endorse.Endorse;
import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.entity.notifications.NotificationAddFriend;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.entity.social.GoogleSocialConnection;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.social.SocialConnectionManager;
import com.noktiz.domain.entity.token.SingleAccessToken;
import com.noktiz.domain.entity.user.Role;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.domain.model.image.ImageConversion;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.social.FriendProvider;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.auth.UserRoles;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author hossein
 */
public class UserFacade implements Serializable {

    private User user;
    private Boolean anonymous;

    public String getFirstName() {
        return anonymous ? "Your" : user.getFirstName();
    }

    public void setFirstName(String fristName) {
        if (!anonymous) {
            user.setFirstName(fristName);
        }
    }

    public String getLastName() {
        return anonymous ? "Friend" : user.getLastName();
    }

    public void setLastName(String lastName) {
        if (!anonymous) {
            user.setLastName(lastName);
        }
    }

    public String getEmail() {
        return anonymous ? "~" : user.getEmail();
    }

    public void setEmail(String email) {
        if (anonymous) {
            user.setEmail(email);
        }
    }

    public Scores getScores() {
        Scores scores = user.getScores();
        if (scores == null) {
            scores = new Scores();
            user.setScores(scores);
        }
        final Date lastUpdate = scores.getLastUpdate();
        Date now = new Date();
        Date lastWeek = new Date(now.getTime() - 1000 * 3600 * 24 * 7);
        if (lastUpdate == null || lastUpdate.before(lastWeek)) {
            Long[] thanksCount = user.getThanksCount(false);
            scores.setLastUpdate(now);
            scores.setThanksCount(thanksCount[0]);
            scores.setThanksUser(thanksCount[1]);
            save();
        }
        if (scores.getFriendUpdate() == null || scores.getFriendAddOrRemove() != null && scores.getFriendAddOrRemove().getTime() > scores.getFriendUpdate().getTime()) {
            Long friendsCount = Friendship.friendsCount(user);
            scores.setFriendUpdate(now);
            scores.setFriendCount(friendsCount);
            save();
        }
        return scores;
    }

    public User getUser() {
        return user;
    }

    public ResultWithObject<ThreadFacade> getThread(Long threadId) {
        Thread load = Thread.loadThreadWithId(threadId);
        if (load != null && (load.getStarter().equals(user) || load.getTarget().equals(user) && !load.isDraft())) {
            ThreadFacade threadFacade = new ThreadFacade(load, this);
            return new ResultWithObject<>(threadFacade, new ArrayList(), true);
        } else {
            return new ResultWithObject<>(false);
        }

    }

    /**
     * only in emergency!
     */
    @Deprecated
    public void setUser(User user) {
        this.user = user;
    }

    public Date getJoinDate() {
        return anonymous ? new Date() : getPersonalInfo().getJoinDate();
    }

    public void setJoinDate(Date joinDate) {
        if (!anonymous) {
            getPersonalInfo().setJoinDate(joinDate);
        }
    }

    public Date getBirthdate() {
        return anonymous ? new Date() : user.getBirthdate();
    }

    public void setBirthdate(Date birthdate) {
        if (!anonymous) {
            user.setBirthdate(birthdate);
        }
    }

    public PersonalInfo getPersonalInfo() {
        refresh();
        return user.getPersonalInfo();
    }

    public Boolean isAnonymous() {
        return anonymous;
    }

    public void block(UserFacade blocker) {
        throw new UnsupportedOperationException();
    }

    public UserFacade(User user) {
        this.user = user;
        this.anonymous = false;
    }

    public UserFacade(User user, boolean anonymous) {
        this.user = user;
        this.anonymous = anonymous;
    }

    public boolean doesPasswordMatch(String password) {
        getPersonalInfo();
        return PasswordManager.authenticate(user, password);
    }

    public List<ThreadFacade> getMyThreadList(long first, long count) {
        List<com.noktiz.domain.entity.Thread> myThread = com.noktiz.domain.entity.Thread.loadThreadWithUser(user, first, count);

        ArrayList<ThreadFacade> ans = new ArrayList<>();
        for (com.noktiz.domain.entity.Thread thread : myThread) {
            if (thread.getBlockedcompletely() != null && !thread.getStarter().equals(user)) {
                continue;
            }
            if (thread.isDraft()) {
                continue;
            }
            ThreadFacade threadFacade = new ThreadFacade(thread, this);
            ans.add(threadFacade);
        }
        return ans;
    }

    public List<ThreadFacade> getMyDrafts(long first, long count) {
        List<com.noktiz.domain.entity.Thread> myThread = com.noktiz.domain.entity.Thread.loadDraftWithUser(user, first, count);

        ArrayList<ThreadFacade> ans = new ArrayList<>();
        for (com.noktiz.domain.entity.Thread thread : myThread) {
            if (thread.getBlockedcompletely() != null && !thread.getStarter().equals(user)) {
                continue;
            }
            ThreadFacade threadFacade = new ThreadFacade(thread, this);
            ans.add(threadFacade);
        }
        return ans;
    }

    public long numOfThreads() {
        return com.noktiz.domain.entity.Thread.numOfThreads(user);
    }

    public long numOfDrafts() {
        return com.noktiz.domain.entity.Thread.numOfDrafts(user);
    }

    public ResultWithObject<ThreadFacade> createThread(UserFacade receiver, String title) {
        ResultWithObject<ThreadFacade> r = new ResultWithObject<>();

        com.noktiz.domain.entity.Thread t = new com.noktiz.domain.entity.Thread();
        t.setStarter(this.getUser());
        if (receiver != null) {
            t.setTarget(receiver.getUser());
        }
        t.setTitle(title);
        t.setDraft(true);
        HSF.get().beginTransaction();
        try {
            t.save();
            HSF.get().commitTransaction();
            r.result = true;
            ThreadFacade tf = new ThreadFacade(t, this);
            r.object = tf;
            return r;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new ResultWithObject(false);
        }

    }

    public Result startThread(UserFacade receiver, String title, String text, Message.DIR dir) {
        ResultWithObject<ThreadFacade> create;
        HSF.get().beginTransaction();
        try {
            create = createThread(receiver, title);
            if (create.result == false) {
                return create;
            }
            create.object.getThread().setStarterCanBeThanked(true);
            create.object.getThread().setTargetCanBeThanked(false);
            create.object.publish();
            create.object.reply(this, receiver, text, dir);
            HSF.get().commitTransaction();
            create.result = true;
            create.messages.add(new Result.Message("message.message_sent", Result.Message.Level.success));
            return create;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public List<Friendship> getOthersFriends() {
        refresh();
        return user.getOthersFriends();
    }

    public Result newDraft(UserFacade receiver, String title, String text, Message.DIR dir) {
        ResultWithObject<ThreadFacade> create;
        HSF.get().beginTransaction();
        try {
            create = createThread(receiver, title);
            if (create.result == false) {
                return create;
            }
            create.object.getThread().setStarterCanBeThanked(true);
            create.object.getThread().setTargetCanBeThanked(false);
            create.object.draft(this, receiver, text, dir);
            create.result = true;
            create.messages.add(new Result.Message("message.saved_as_draft", Result.Message.Level.success));
            HSF.get().commitTransaction();
            return create;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public boolean canSendMessageTo(UserFacade receiver) {
        return Friendship.canStart(this.user, receiver.getUser());
    }

    /**
     * or do i trust friend
     */
    public boolean doIOwnerTheFriendshipOf(UserFacade friend) {
        if (!user.getActive()) {
            return false;
        }
        return Friendship.exists(friend.getUser(), this.user) != null;
    }

    public Friendship getFriendship(UserFacade friend) {
        if (!user.getActive()) {
            return null;
        }
        return Friendship.exists(friend.getUser(), this.user);
    }

    /**
     *
     * @param mail
     * @return ResultWithObject : result is true if friend add successfully and false if friend have been invited or invited now.
     */
    public ResultWithObject<Friendship> addFriendByEmail(String mail) {
        ResultWithObject<UserFacade> load = UserFactory.loadUserWithEmailId(mail, true);
        if (load.result == false) {
            Result invite = inviteWithEmail(mail);
            return new ResultWithObject<>(null,invite.messages,false);
        }
        return addFriend(load.object);
    }
    public ResultWithObject<Friendship> addFriend(UserFacade friend) {
        refresh();
        friend.refresh();
        User me = this.user;
        ResultWithObject<Friendship> result = new ResultWithObject<>();
        if (me.equals(friend.getUser())) {
            result.result = false;
            result.messages.add(new Result.Message("message.can_not_friend_yourself", Result.Message.Level.error));
            return result;
        }
        Friendship exists = this.getFriendship(friend);
        if (exists != null && !exists.isDeleted()) {
            result.result = true;
            result.messages.add(new Result.Message("message.friend_already", Result.Message.Level.info));
            return result;
        }
        Friendship fr;
        final Date now = new Date();
        if (exists == null) {
            fr = new Friendship();
            fr.setFriendshipOwner(me);
            fr.setFriend(friend.user);
            fr.setStartDate(now);
        } else {
            fr = exists;
            fr.setDeleted(false);
        }
        me.getFriends().add(fr);
        friend.user.getOthersFriends().add(fr);
        NotificationAddFriend notif = NotificationAddFriend.loadByUserAndFriend(friend.user, me);
        boolean createNotification;
        if (notif == null) {
            createNotification = true;
            notif = new NotificationAddFriend(me, friend.user);
        } else {
            createNotification = false;
            notif.setSendDate(now);
        }
        me.getScores().setFriendAddOrRemove(now);
        HSF.get().beginTransaction();
        try {
            fr.save();
            me.save();
            friend.user.save();
            if (!createNotification) {
                notif.update();
            } else {
                notif.create();
            }
            result.object = fr;
            result.result = true;
            result.messages.add(new Result.Message("message.friend_created", Result.Message.Level.success,fr.getFriend().getName() ));
            HSF.get().commitTransaction();
            inviteToRateContexts(fr.getFriend());
            return result;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new ResultWithObject<>(false);
        }

    }

    private void inviteToRateContexts(User friend) {
        RateContextManager rateContextManager = new RateContextManager();
        for (RateContext rateContext : getUser().getRateContexts()) {
            rateContextManager.inviteNewFriendIfInvited(rateContext, friend);
        }
    }

    public static Result removeFriend(Friendship fr) {
//        final Session s = HibernateSessionFactory.get().getCurrentSession();
        fr = (Friendship) getCurrentSession().merge(fr);
        HSF.get().beginTransaction();
        UserFacade userFacade = new UserFacade(fr.getFriendshipOwner());
        userFacade.getScores().setFriendAddOrRemove(new Date());
        try {
            removeRateInviteNotification(fr);
            fr.delete();
            userFacade.save();
            HSF.get().commitTransaction();
            Result r = new Result();
            r.result = true;
            r.messages.add(new Result.Message("message.remove_success", Result.Message.Level.success));
            return r;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(UserFacade.class).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public static Session getCurrentSession() {
        return HSF.get().getCurrentSession();
    }

    private static void removeRateInviteNotification(Friendship fr) {
        Query query = getCurrentSession().getNamedQuery("DeleteRateInviteByUserAndUser");
        query.setParameter("invitee", fr.getFriend());
        query.setParameter("sender", fr.getFriendshipOwner());
        query.executeUpdate();
        for (RateContext rateContext : fr.getFriendshipOwner().getRateContexts()) {
            rateContext.getInvitedPersons().dontIncludeUser(fr.getFriend());
        }
    }

    public List<Friendship> getFriends() {
        refresh();
        return user.getFriends();

    }

    public String getName() {
        if(isAnonymous())
            return (new StringResourceModel("YourFriend",null)).getString();
        if (getLastName().startsWith("@"))
            return getFirstName()+getLastName();
        return getFirstName() + " " + getLastName();
    }

    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.user);
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserFacade other = (UserFacade) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "UserFacade{" + "user=" + user + ", fristName=" + getFirstName() + ", lastName=" + getLastName() + '}';
    }

    public List<BaseNotification> loadMyNotifications() {
        return BaseNotification.loadUnreadByUser(user);
    }

    public List<BaseNotification> loadMStream(boolean loadSeen, boolean loadSubscribedOnly, int start, int count) {
        return BaseNotification.loadByUserInUpdateList(user, start, loadSeen, loadSubscribedOnly, count);
    }

    public List<BaseNotification> loadMStream(boolean loadSeen, boolean loadSubscribedOnly, BaseNotification start, int count) {
        return BaseNotification.loadByUserInUpdateListBefore(user, start, loadSeen, loadSubscribedOnly, count);
    }

    public Long getNumberOfUnreadNotifications() {
        return BaseNotification.getNumberOfUnreadNotifications(user);
    }

    public boolean refresh() {
//        if(user.getId()==null)
//            return true;
        try {
            if (!getCurrentSession().contains(user)) {
                try {
                    if (user.getId() == null)
                        return true;
                }catch (Exception  e){

                }
                user = (User) getCurrentSession().merge(user);
            }
            return true;
        } catch (StaleObjectStateException ex) {
//            boolean contains = HSF.get().getCurrentSession().contains(user);
                user = User.load(user.getId());
//            boolean contains1 = HSF.get().getCurrentSession().contains(user);
            return false;
        }
    }

    public Result addPossibleContacts(List<SocialConnection> contacts) {
        Result r = new Result(true);
        HSF.get().beginTransaction();
        try {
            for (int i = 0; i < contacts.size(); i++) {
                SocialConnection contact = contacts.get(i);
                contact.setOwner(user);
                if (getSocialConnections().contains(contact)) {
                    r.result = false;
                    return r;
                }
                getCurrentSession().saveOrUpdate(contact);
                user.getSocialConnections().add(contact);
            }
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        } finally {
            if(!HSF.get().isRolledBack()){
                HSF.get().commitTransaction();
            }
        }
        user = (User) getCurrentSession().load(User.class, user.getId());
//        ArrayList<Result.Message> messages = new ArrayList<>();
//        r.messages = messages;
        return r;
    }

    public Result addPossibleContact(SocialConnection contact) {
        refresh();
        List<SocialConnection> connections = new ArrayList<>();
        connections.add(contact);
        Result result = addPossibleContacts(connections);
        if (result.result == false) {
            result.messages.add(new Result.Message( "message.friend_was_invited", Result.Message.Level.info,contact.getName()));
        }
        return result;
    }

    public Result updatePossibleContact(SocialConnection contact) {
        HSF.get().beginTransaction();
        try {
            getCurrentSession().update(contact);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
        return new Result(Boolean.TRUE);
    }

    public List<SocialConnection> fetchSocialConnections(FriendProvider provider) throws AccessDeniedException, IOException {
        ArrayList<SocialConnection> connections = provider.getConnections(this);
//        user.getSocialConnections().addAll(connections);
        return connections;
    }

    public Set<SocialConnection> getSocialConnections() {
        try {
            Set<SocialConnection> socialConnections = user.getSocialConnections();
            if (socialConnections.size() < 0) {
                return null;
            }
            return socialConnections;

        } catch (Exception e) {
            refresh();
            return user.getSocialConnections();
        }
    }

    public Credential getCredential() {
        return user.getCredential();
    }

    public void setGender(User.Gender object) {
        try {
            user.setGender(object);
        } catch (IllegalArgumentException ex) {
        }
    }

    public User.Gender getGender() {
        final User.Gender gender = user.getGender();
        return gender;
    }

    public Result save() {
        if(user.getId() == null){
            return new Result("You should create user through UserFactory class!", Result.Message.Level.error);
        }
        HSF.get().beginTransaction();
        try {
            user.save();
            HSF.get().commitTransaction();
            updateInSolr();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public Result changePassword(String newPassStr) {
        PasswordManager.changePassword(user, newPassStr);
        return save();
    }

    public NotificationSettings getNotificationSettings() {
        refresh();
        return user.getNotificationSettings();
    }

    public String getPicture() {
        return user.getPictureId();
    }

    public Result setPicture(InputStream imageInputStream, Integer x1, Integer y1, Integer x2, Integer y2) {
        BufferedImage bufferedImage;
        try {
            ImageIO.setUseCache(false);
            bufferedImage = ImageIO.read(imageInputStream);
            if(x1 != null && y1 != null && x2 !=null && y2!= null) {
                x1=Math.max(x1,0);
                x2=Math.min(x2,bufferedImage.getWidth()-1);
                y1=Math.max(y1,0);
                y2=Math.min(y2,bufferedImage.getHeight()-1);
                bufferedImage = bufferedImage.getSubimage(x1, y1, x2-x1, y2-y1);
            }
        } catch (IOException ex) {
            Logger.getLogger(UserFacade.class).error("exception in image", ex);
            return new Result(false);
        }
        return setPicture(bufferedImage);
    }

    public Result setPicture(BufferedImage bufferedImage) {
        BufferedImage[] userImages = ImageConversion.resizeUserImage(bufferedImage);
        ImageManagement.ImageSize[] size = new ImageManagement.ImageSize[]{ImageManagement.ImageSize.small, ImageManagement.ImageSize.medium, ImageManagement.ImageSize.large};
        boolean success = false;
        for (int i = 0; i < 5; i++) {

            String id = RandomStringUtils.randomAlphabetic(15);
            Boolean saveNewImage = ImageManagement.saveNewImage(user.getClass(), id, userImages, size);
            if (saveNewImage) {
                user.setPictureId(id);
                success = true;
                break;
            }
        }
        if (success != true) {
            return new Result(false);
        }
        return new Result(true);
    }

    /**
     *
     * @return array which first element is number of user thanks and second
     * element is number of user thankers
     */
    Long[] getThanksCount(boolean exact) {
        return user.getThanksCount(exact);
    }

    SocialConnectionManager socialConnectionManager= new SocialConnectionManager();

    public Result connectToRequestedUsers(SocialConnection.Context context) {

        List<SocialConnection> result = socialConnectionManager.getRequestedConnections(this,context);
        for (SocialConnection connection : result) {
            new UserFacade(connection.getOwner()).addFriend(this);
            connection.setDismissed(true);
            new SocialConnectionManager().update(connection);
        }
        return new Result(new Result.Message("message.connect_to_number_of_people", Result.Message.Level.success,result.size()), true);
    }


    public String[] getSocialId(SocialConnection.Context context) {
        switch (context) {
            case Facebook:
                return new String[]{user.getCredential().getFacebookInfo().getFacebook_id()};
            case Email:
                List<String> ret = new ArrayList<>();
                if (user.getCredential().getGoogleInfo() != null && user.getCredential().getGoogleInfo().getPrimary_email() != null) {
                    ret.add(user.getCredential().getGoogleInfo().getPrimary_email());
                }
                ret.add(user.getEmail());
                return ret.toArray(new String[ret.size()]);
            default:
                return null;
        }

    }

    public Result inviteWithEmail(String email) {
        if (!UserFactory.checkEmail(email)) {
            return new Result("Please enter a valid Email Address", Result.Message.Level.error);
        }
        GoogleSocialConnection connection = new GoogleSocialConnection(getUser(), SocialConnection.Context.Email, email, true);
        Result result = addPossibleContact(connection);
        if (result.result == false) {
            return result;
        }
        result = connection.sendInvite();
        new SocialConnectionManager().update(connection);
        return result;

    }

    public Result inviteSocialConnection(SocialConnection friend) {
        Result result = friend.sendInvite();
        if (result.result == true) {
            friend.setInvited(true);
            addPossibleContact(friend);
            save();
            return new Result(new Result.Message("message.friend_invited", Result.Message.Level.success,friend.getName()), true);
        }
        return new Result(new Result.Message("message.problem_inviting_friend", Result.Message.Level.error,friend.getName()), false);
    }

    public Result delete() {
        HSF.get().beginTransaction();
        try {
            user.delete();
            HSF.get().commitTransaction();
            return new Result(true);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new Result(false);
        }
    }

    public Result sendForgetPasswordEmail() {
        SingleAccessToken token = new SingleAccessTokenManager().generateAccessToken(
                user,
                SingleAccessToken.Type.ResetPassword,
                Integer.valueOf(SystemConfigManager.getCurrentConfig().getProperty("ForgetPasswordTokenTimeOut")),
                Integer.valueOf(SystemConfigManager.getCurrentConfig().getProperty("ForgetPasswordTokenAllowdUseTime"))
        );
        IEmailProvider.IEmail email = EmailCreator.createForgetPasswordEmail(user, token);
        boolean result = IEmailProvider.get().send(user.getEmail(), email);
        if (result) {
            return new Result(new Result.Message("message.password_rest_mail_sent", Result.Message.Level.success), true);
        } else {
            return new Result(new Result.Message("message.problem_sending_password_reset_mail", Result.Message.Level.error), false);
        }

    }

    public Result sendActivationEmail() {
        IEmailProvider.IEmail createActivationEmail = EmailCreator.createActivationEmail(user);
        if (IEmailProvider.get().send(user.getEmail(), createActivationEmail)) {
            return new Result(new Result.Message("message.activation_email_sent", Result.Message.Level.success), true);
        } else {
            return new Result(new Result.Message("message.problem_sending_activation_email", Result.Message.Level.error), false);
        }
    }

    public boolean isSetPassword() {
        return (user.getPersonalInfo().getPassword() != null);
    }

    public void approveEndorseContext(Endorse endorse) {
        refresh();
        user.getApprovedEndorseContexts().add(endorse.getContext());
        user.getRemovedEndorseContexts().remove(endorse.getContext());
    }

    public void removeEndorseContext(String endorse) {
        user.getApprovedEndorseContexts().remove(endorse);
        user.getRemovedEndorseContexts().add(endorse);
    }

    public void activate() {
        user.setActive(true);
        connectToRequestedUsers(SocialConnection.Context.Facebook);
        connectToRequestedUsers(SocialConnection.Context.Email);
        user.getPersonalInfo().setActivate(null);

        //add by self account
        if("true".equals(SystemConfigManager.getCurrentConfig().getProperty("AddSelfOnRegistration")))
            new UserFacade(user.load(SystemConfigManager.getCurrentConfig().getPropertyAsLong("SelfUserId"))).addFriend(this);
    }

    public String getProperty(String property){
        return user.getProperty(property);
    }
    public void setProperty(String property,String value){
        user.setProperty(property,value);
    }

    public boolean doIHaveAccessToProtectedInfo(UserFacade userFacade) {
        if(this.user==null|| this.user.getId()==null){
            return false;
        }
        return equals(userFacade) || userFacade.doIOwnerTheFriendshipOf(this);
    }

    public boolean addWrongLoginAttempt() {
        boolean ret =getPersonalInfo().addWrongLoginAttempt();
        new BaseManager().update(getPersonalInfo());
        return ret;
    }
    public void resetLoginAttempt() {
        getPersonalInfo().resetLoginAttempt();
        new BaseManager().update(getPersonalInfo());
    }

    public UserRoles getUserRoles(){
        Set<Role> roles = getRoles();
        UserRoles userRoles = new UserRoles();
        for (Role role : roles) {
            userRoles.add(role.getName());
        }
        return userRoles;
    }

    public Set<Role> getRoles(){
        refresh();
        return user.getRoles();
    }
    public Result addRole(UserRoles.RoleEnum role){
        boolean result = getRoles().add(Role.getRole(role));
        Result ans=null;
        if(result){
            HSF.get().beginTransaction();
            try{
                ans = save();
                return ans;
            }catch (HibernateException ex){
                Logger.getLogger(UserFacade.class).error("can not add role to "+user,ex);
                HSF.get().roleback();
                return new Result(new Result.Message("message.can_not_add_role", Result.Message.Level.error,user.getName()),false);
            }
            finally {
                HSF.get().commitTransaction();
            }

        }
        else {
            ans = new Result(true);
            return ans;
        }

    }
    public Result removeRole(UserRoles.RoleEnum role){
        boolean result = getRoles().remove(Role.getRole(role));
        Result ans=null;
        if(result){
            HSF.get().beginTransaction();
            try{
                ans = save();
                return ans;
            }catch (HibernateException ex){
                Logger.getLogger(UserFacade.class).error("can not add role to "+user,ex);
                HSF.get().roleback();
                return new Result(new Result.Message("message.can_not_add_role", Result.Message.Level.error,user.getName()),false);
            }
            finally {
                HSF.get().commitTransaction();
            }

        }
        else {
            ans = new Result(new Result.Message("message.role_not_exist", Result.Message.Level.error),result);
            return ans;
        }

    }

    public ResultWithObject<Friendship> toggleFollow(UserFacade userFacade) {

        Friendship friendship = userFacade.getFriendship(this);
        if(friendship==null){
            return new ResultWithObject(null,Arrays.asList(new Result.Message("message.notFriend", Result.Message.Level.error,userFacade.getName())),false);
        }
        friendship.setDontFollow(!friendship.getDontFollow());
        boolean save = friendship.save();
        if(save){
            if(friendship.getDontFollow()) {
                return new ResultWithObject(friendship,Arrays.asList(new Result.Message("message.dontFollow_changeToTrue", Result.Message.Level.success,userFacade.getName())), true);
            }
            else{
                return new ResultWithObject(friendship,Arrays.asList(new Result.Message("message.dontFollow_changeToFalse", Result.Message.Level.success,userFacade.getName())), true);
            }
        }
        else{
            return new ResultWithObject(friendship,Arrays.asList(new Result.Message("message.unknown_problem", Result.Message.Level.error)),false);
        }
    }


    static String solrServerAddress;
    private static void init() {
        solrServerAddress=SystemConfigManager.getCurrentConfig().getProperty("solrServerAddress");
        if(solrServerAddress == null)
            solrServerAddress="http://localhost:8983/solr/";
        solrServerAddress = solrServerAddress+"users";
    }

    public static HttpSolrClient getSolrClient() {
        return new HttpSolrClient(solrServerAddress);
    }


    public static void updateAllSolrIndex(){
        List<User> users = getCurrentSession().getNamedQuery("loadAllActiveUsers").list();
        try {
            for (User u : users) {
                SolrInputDocument toAdd = getSolrDocument(u);
                getSolrClient().add(toAdd);
            };
            getSolrClient().commit();
        } catch (SolrServerException | IOException e) {
            Logger.getLogger(UserFacade.class).error(e);
        }
    }

    public static SolrInputDocument getSolrDocument(User u) {
        SolrInputDocument toAdd= new SolrInputDocument();
        toAdd.addField("firstName",u.getFirstName());
        toAdd.addField("lastName",u.getLastName());
        toAdd.addField("active", u.getActive());
        String emails = u.getEmail();
        if(u.getCredential().getGoogleInfo()!=null && u.getCredential().getGoogleInfo().getPrimary_email()!=null)
        emails += " " + u.getCredential().getGoogleInfo().getPrimary_email();
        toAdd.addField("emails", emails);
        toAdd.addField("id",u.getId());
        return toAdd;
    }
    static{
        init();
        updateAllSolrIndex();
    }
    public void updateInSolr(){
        try {
            if(user.isDeleted() || !user.getActive()){
                getSolrClient().deleteById(user.getId().toString());
            }
            else
                getSolrClient().add(getSolrDocument(user));
            getSolrClient().commit();
        } catch (IOException | SolrServerException e) {
            Logger.getLogger(getClass()).error(e);
        }
    }

    public static List<UserFacade> loadByQuery(String query, int from, int count){
        SolrQuery solrQuery;
        solrQuery = new SolrQuery(SolrUtils.escapeString(query));

        solrQuery.setStart(from);
        solrQuery.setRows(count);
        SolrClient client = getSolrClient();
        List<UserFacade > ret = new ArrayList<>();
        try {
            QueryResponse response = client.query(solrQuery);
            for (SolrDocument entry : response.getResults()) {
                ret.add(new UserFacade(User.load(Long.parseLong((String) entry.getFieldValue("id")))));
            }
        } catch (SolrServerException e) {
            Logger.getLogger(UserFacade.class).error(e);
        }
        return ret;
    }
}

