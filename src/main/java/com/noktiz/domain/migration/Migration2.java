package com.noktiz.domain.migration;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.QuestionLanguage;
import com.noktiz.domain.entity.user.Role;
import com.noktiz.domain.i18n.Local;
import com.noktiz.domain.i18n.LocalManager;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.auth.UserRoles;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Locale;

/**
 * Created by Hossein on 3/30/2015.
 */
public class Migration2 {

    private static final LocalManager localManager = new LocalManager();

    public static void main(String [] args){
        HSF.configure(new Configuration().configure().buildSessionFactory(), true);
        updateLocal();
        updateFriendshipDontFollow();
        updateQuestionLanguage();
        RoleMigration.main(args);
    }

    private static void updateQuestionLanguage() {
        BaseManager baseManager = new BaseManager();
        baseManager.saveAsNew(new QuestionLanguage("en", ""));
        baseManager.saveAsNew(new QuestionLanguage("fr", ""));
        baseManager.saveAsNew(new QuestionLanguage("de", ""));
        baseManager.saveAsNew(new QuestionLanguage("it", ""));
        baseManager.saveAsNew(new QuestionLanguage("ja", ""));
        baseManager.saveAsNew(new QuestionLanguage("ko", ""));
        baseManager.saveAsNew(new QuestionLanguage("zh", ""));
        baseManager.saveAsNew(new QuestionLanguage("en", ""));
        baseManager.saveAsNew(new QuestionLanguage("fa", ""));
        baseManager.saveAsNew(new QuestionLanguage("ar", ""));
        baseManager.saveAsNew(new QuestionLanguage("es", ""));
    }

    private static void updateFriendshipDontFollow() {
        Query query = HSF.get().getCurrentSession().createQuery("update Friendship f set f.dontFollow =false where f.dontFollow is null");
        query.executeUpdate();
    }

    public static void updateLocal() {
        Local en_US = new Local();
        en_US.setId(1l);
        en_US.setActive(true);
        en_US.setCountry("US");
        en_US.setLanguage("en");
        en_US.setName("English (United States)");
        localManager.saveAsNew(en_US);
        Local fa_IR = new Local();
        fa_IR.setId(2l);
        fa_IR.setActive(true);
        fa_IR.setCountry("IR");
        fa_IR.setLanguage("fa");
        fa_IR.setName("Persian");
        localManager.saveAsNew(fa_IR);

        Query query = HSF.get().getCurrentSession().createQuery("update PersonalInfo p set p.local =:local where p.local is null");
        query.setParameter("local",en_US);
        query.executeUpdate();
    }

}
