package com.noktiz.domain.migration;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.Credential;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.user.Role;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.auth.UserRoles;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Created by Hossein on 3/30/2015.
 */
public class RoleMigration {
    public static void main(String [] args){
        HSF.configure(new Configuration().configure().buildSessionFactory(), true);
        Role.createRoles();
        Query query = HSF.get().getCurrentSession().createQuery("From User");
        List<User> users = (List<User>)query.list();
        HSF.get().beginTransaction();
        try {
            for (User user : users) {
                UserFacade userFacade = new UserFacade(user);
                userFacade.addRole(UserRoles.RoleEnum.USER);
            }
            HSF.get().commitTransaction();
        }catch (HibernateException ex){
            HSF.get().roleback();
            Logger.getLogger(EmailIdMigration.class).error(ex);
        }
    }

}
