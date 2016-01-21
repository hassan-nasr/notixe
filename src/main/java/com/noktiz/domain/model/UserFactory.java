/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.NotificationSettings;
import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.auth.UserRoles;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrException;
import org.hibernate.HibernateException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author hossein
 */
public class UserFactory implements Serializable {


    public static Result addUser(User user) {
        return addUser(user, false, true, false);
    }

    public static Result addUser(User user, boolean socialRegister, boolean explicit, boolean active) {
        if(explicit) {
            Result r = staticVerifyInfo(user);
            if (r.result == false)
                return r;
            if (!socialRegister) {
                r = PasswordManager.verifyPassword(user.getPersonalInfo().getPassword());
                if(!r.result)
                    return r;
            }
        }
        String nEmail=EmailAddressUtils.normalizeEmail(user.getEmail());
        if(nEmail==null){
            return new Result(false);
        }

//        if (user.getEmail() == null || user.getEmail().equals("")) {
//            ArrayList<Result.Message> m = new ArrayList<Result.Message>();
//            m.add(new Result.Message("email.empty", Result.Message.Level.error));
//            return new Result(new ArrayList<Result.Message>(m), Boolean.FALSE);
//
//        }
        User load = User.loadUserWithEmailId(nEmail,false);
        if(load!=null && (load.getActive()==null || load.getActive()==false)){
//            Result ans=new UserFacade(load).delete();
            load.setFirstName(user.getFirstName());
            load.setLastName(user.getLastName());
            load.setGender(user.getGender());
            load.setLocation(user.getLocation());
            load.getPersonalInfo().setPassword(user.getPersonalInfo().getPassword());
            user=load;
            load=null;
        }
        if (load == null) {
            NotificationSettings ns = NotificationSettings.getNewNotificationSettings();
            if(user.getNotificationSettings()==null) {
                user.setNotificationSettings(ns);
//                ns.setUser(user);
            }
            else {
                ns = user.getNotificationSettings();
            }

            if (!socialRegister && !active) {
                if (deactivateUser(user,explicit)) {
                    return new Result(false);
                }
            }
            else{
                user.setActive(true);
            }

            HSF.get().beginTransaction();
            try {
                if(!socialRegister && explicit)
                    PasswordManager.changePassword(user, user.getPersonalInfo().getPassword());
                if(user.getCredential()!=null);
                    user.getCredential().setUser(null);
                user.save();
                user.getCredential().setUser(user);
                user.getCredential().setEmailId(nEmail);
                new BaseManager().update(user.getCredential());
                ns.setUser(user);
                ns.save();
                user.getPersonalInfo().setUser(user);
                new BaseManager().update(user.getPersonalInfo());
                ArrayList<Result.Message> m = new ArrayList<Result.Message>();
                m.add(new Result.Message("message.account_created", Result.Message.Level.success,user.getEmail()));

                UserFacade userFacade = new UserFacade(user);
                if(socialRegister) {
                    userFacade.activate();
                }
                Result result = userFacade.addRole(UserRoles.RoleEnum.USER);
                if(result.result) {
                    result = new Result(m, Boolean.TRUE);
                }
                else{
                    result= new Result(new Result.Message("message.problem_creating_account", Result.Message.Level.error),false);
                }
                if(result.result)
                    userFacade.updateInSolr();
                return result;

            } catch (HibernateException ex) {
                HSF.get().roleback();
                Logger.getLogger(UserFacade.class).info("HibernateException", ex);
                return new Result(false);
            }  catch (SolrException ex){
                Logger.getLogger(UserFacade.class).info("SolrException", ex);
                return new Result(true);
            }
            finally {
                HSF.get().commitTransaction();
            }
        } else {
            ArrayList<Result.Message> m = new ArrayList<Result.Message>();
            m.add(new Result.Message("message.user_exists", Result.Message.Level.success));
            return new Result(new ArrayList<Result.Message>(m), Boolean.FALSE);
        }

    }

    private static Result staticVerifyInfo(User user) {
        user.setFirstName(user.getFirstName().trim());
        if(user.getFirstName().isEmpty())
            return new Result(new Result.Message("message.first_name_can_not_empty", Result.Message.Level.error),false);
        user.setLastName(user.getLastName().trim());
        if(user.getLastName().isEmpty())
            return new Result(new Result.Message("message.last_name_can_t_be_empty", Result.Message.Level.error),false);
        if(!user.getName().matches("[\\p{L}\\s]+"))
            return new Result(new Result.Message("message.your_name_should_be_only_in_letters", Result.Message.Level.error),false);
        if(!checkEmail(user.getEmail()))
            return new Result(new Result.Message("message.invalid_email", Result.Message.Level.error),false);
        return new Result(true);
    }

    public static boolean checkEmail(String email) {
        return email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    public static ResultWithObject<UserFacade> loadUserWithEmail(String email,boolean checkActivate) {
        return loadUserWithEmail(email,checkActivate,false);
    }
    public static ResultWithObject<UserFacade> loadUserWithEmail(String email,boolean checkActivate, boolean checkLocked) {

        User load = User.loadUserWithEmail(email,checkActivate);

        if (load == null) {
            ResultWithObject rwo = new ResultWithObject<>(null, new ArrayList(), false);
            rwo.messages.add(new Result.Message("message.user_not_exists", Result.Message.Level.info));
            return rwo;
        }
        if(checkLocked){
            if(load.getPersonalInfo().getLockUntil() !=null && load.getPersonalInfo().getLockUntil().after(new Date())) {
                ResultWithObject rwo = new ResultWithObject<>(null, new ArrayList(), false);
                rwo.messages.add(new Result.Message("message.account_locked", Result.Message.Level.info));
                return rwo;
            }
        }
        return new ResultWithObject<>(new UserFacade(load), null, Boolean.TRUE);

    }public static ResultWithObject<UserFacade> loadUserWithEmailId(String email,boolean checkActivate) {
        User load = User.loadUserWithEmailId(email,checkActivate);
        if (load == null) {
            ResultWithObject rwo = new ResultWithObject<>(null, new ArrayList(), false);
            rwo.messages.add(new Result.Message("message.user_not_exists", Result.Message.Level.info));
            return rwo;
        }
        return new ResultWithObject<>(new UserFacade(load), null, Boolean.TRUE);

    }

    static boolean isBlocked(User sender, User receiver) {
        Block loadBlocked = Block.loadBlocked(sender, receiver);
        return !(loadBlocked == null);
    }

    /**
     *
     * @param activate
     * @return true if code does not exists.
     */
    private static boolean checkActivationCode(String activate) {
        return !PersonalInfo.checkActivateExist(activate);
    }

    private static boolean deactivateUser(User user, boolean sendMail) {
        String activate=null;
        int i;
        for ( i=0;i<10;i++) {
            activate = RandomStringUtils.randomAlphabetic(63);
            if (checkActivationCode(activate)) {
                break;
            }
        }
        
        if (i==11) {
            return true;
        }
        user.getPersonalInfo().setActivate(activate);
        user.setActive(false);
        if(sendMail)
            new UserFacade(user).sendActivationEmail();
        return false;
    }

    public static ResultWithObject<User> addFutureUserOrGetWithEmail(String email){

        if(!checkEmail(email))
            return new ResultWithObject<>(new Result("message.invalid_email",false));
        email=EmailAddressUtils.normalizeEmail(email);
        User user = User.loadUserWithEmailId(email, false);
        if (user == null) {
            String[] name =email.split("[@]+");
            user = new User();
            user.setEmail(email);
            user.setFirstName(name[0]);
            user.setLastName("@"+name[1]);
            Result result = addUser(user,false,false,false);
            return new ResultWithObject<User>(result, user);
        }
        return new ResultWithObject<>(new Result("exist",true),user);
    }
}
