package com.noktiz.domain.init;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.QuestionLanguage;
import com.noktiz.domain.entity.user.Role;
import com.noktiz.domain.i18n.Local;
import com.noktiz.domain.i18n.LocalManager;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.auth.UserRoles;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by hasan on 2014-12-22.
 */
public class InitDataFiller {

    static LocalManager localManager = new LocalManager();

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        Boolean drop=null;
        for (String arg : args) {
            if(arg.equals("drop"))
                drop=true;
            else if(arg.equals("nodrop"))
                drop= false;
        }

        if(drop==null){
            System.out.println("drop database?");
            Scanner cin = new Scanner(System.in);
            drop = cin.nextBoolean();
        }
        dropDatabase(drop);
        HSF.configure(new Configuration().configure().buildSessionFactory(), true);
        fill();

    }

    public static void fill() throws IOException {
        fillSettings();
        Local local = fillLocal();
        fillQLanguage();
        fillRoles();
        createSelfAccount(local);
    }

    private static Local fillLocal() throws IOException {
        Properties settings = new Properties();
        settings.load(InitDataFiller.class.getResourceAsStream("local.properties"));
        Integer count = Integer.valueOf(settings.getProperty("count"));
        Local ret=null;
        for (int i = 0; i < count; i++) {
            Local local = new Local();
            local.setId(Long.valueOf(settings.getProperty("local." + i + ".id", null)));
            local.setActive(Boolean.valueOf(settings.getProperty("local." + i + ".active", "true")));
            local.setCountry(settings.getProperty("local." + i + ".country"));
            local.setLanguage(settings.getProperty("local." + i + ".language"));
            local.setName(settings.getProperty("local." + i + ".name"));
            local.setDeleted(false);
            localManager.saveAsNew(local);
            if (ret == null)
                ret = local;
        }
        return ret;
    }
    private static void fillQLanguage() throws IOException {
        Properties settings = new Properties();
        settings.load(InitDataFiller.class.getResourceAsStream("qLanguage.properties"));
        Integer count = Integer.valueOf(settings.getProperty("count"));
        for (int i = 0; i < count; i++) {
            QuestionLanguage questionLanguage = new QuestionLanguage();
//            questionLanguage.setId(Long.valueOf(settings.getProperty("qLanguage." + i + ".id", null)));
            questionLanguage.setActive(Boolean.valueOf(settings.getProperty("qLanguage." + i + ".active", "true")));
            questionLanguage.setCountry(settings.getProperty("qLanguage." + i + ".country",""));
            questionLanguage.setName(settings.getProperty("qLanguage." + i + ".name"));
            questionLanguage.setDeleted(false);
            localManager.saveAsNew(questionLanguage);
        }
    }

    private static void fillRoles() {
        Role.createRoles();
    }

    private static void dropDatabase(Boolean drop) throws ClassNotFoundException, SQLException {

        Configuration hibernateConfiguration = new Configuration().configure();
        String dbname = getDBName(hibernateConfiguration.getProperty("hibernate.connection.url").split("\\?")[0].split("/")[3]);
        int dbnameIndex = hibernateConfiguration.getProperty("hibernate.connection.url").indexOf(dbname);
        Connection connect = DriverManager
                .getConnection(hibernateConfiguration.getProperty("hibernate.connection.url").substring(0, dbnameIndex),
                        hibernateConfiguration.getProperty("hibernate.connection.username"),
                        hibernateConfiguration.getProperty("hibernate.connection.password"));
        Statement statement = connect.createStatement();
        try {
            Class.forName(hibernateConfiguration.getProperty("hibernate.connection.driver_class"));
            // setup the connection with the DB.

            // statements allow to issue SQL queries to the database
            // resultSet gets the result of the SQL query
            if (drop)
                statement.execute("DROP Database IF Exists " + dbname);
        }finally {
            statement.execute("CREATE DATABASE if not exists " + dbname + "\n" +
                    "CHARACTER SET utf8\n" +
                    "COLLATE utf8_general_ci;");
//            connect.commit();
        }

    }

    private static String getDBName(String s) {
        return s;
    }

    private static void createSelfAccount(Local local) throws IOException {
        Properties settings = new Properties();
        settings.load(InitDataFiller.class.getResourceAsStream("SelfUser.properties"));
        User self = User.loadUserWithEmail(settings.getProperty("email"),false);
        if(self !=null)
            return;
        self = new User();
        self.setEmail(settings.getProperty("email"));
        self.setFirstName(settings.getProperty("firstName"));
        self.setLastName(settings.getProperty("lastName"));
        self.getPersonalInfo().setPassword(settings.getProperty("password"));
        self.getPersonalInfo().setLocal(local);
        self.setGender(User.Gender.Group);
        self.getRoles().add(Role.getRole(UserRoles.RoleEnum.ADMIN));
        new UserFactory().addUser(self, false, true, true);
        SystemConfigManager.getCurrentConfig().getProperties().put("SelfUserId",self.getId().toString());
        new SystemConfigManager().update(SystemConfigManager.getCurrentConfig());

    }

    private static void fillSettings() throws IOException {
        Properties settings = new Properties();
        settings.load(InitDataFiller.class.getResourceAsStream("system.properties"));
        SystemConfig currentConfig = SystemConfigManager.getCurrentConfig();
        if(currentConfig == null){
            currentConfig = new SystemConfig();
        }
        for (Map.Entry<Object, Object> objectObjectEntry : settings.entrySet()) {
            currentConfig.getProperties().put(objectObjectEntry.getKey().toString(),objectObjectEntry.getValue().toString());
        }
        new SystemConfigManager().update(currentConfig);
    }
}
