package com.noktiz.domain.migration;

import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.Credential;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.Application;
import org.apache.log4j.Logger;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.response.NullResponse;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Created by Hossein on 1/31/2015.
 */
public class EmailIdMigration {
    public static void main(String [] args){
        HSF.configure(new Configuration().configure().buildSessionFactory(),true);
        Query query = HSF.get().getCurrentSession().createQuery("From User");
        List<User> users = (List<User>)query.list();
        HSF.get().beginTransaction();
        try {
            for (User user : users) {
                Credential credential = user.getCredential();
                if (credential.getEmailId() == null || !credential.getEmailId().contains("@")) {
                    credential.setEmailId(EmailAddressUtils.normalizeEmail(user.getEmail()));
                    user.save();
                }
            }
            HSF.get().commitTransaction();
        }catch (HibernateException ex){
            HSF.get().roleback();
            Logger.getLogger(EmailIdMigration.class).error(ex);
        }


    }
}
