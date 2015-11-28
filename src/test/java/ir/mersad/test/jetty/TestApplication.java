/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.mersad.test.jetty;

import com.noktiz.domain.init.InitDataFiller;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.Application;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

/**
 *
 * @author saeed
 */
public class TestApplication extends Application {

    @Override
    public void configureHibernate() {
        Configuration configure = new Configuration().configure();
        configure.setProperty("hibernate.dialect","org.hibernate.dialect.HSQLDialect");
        configure.setProperty("connection.driver_class","org.hsqldb.jdbcDriver");
        configure.setProperty("hibernate.connection.url","jdbc:hsqldb:file://C:/Users/hasan/Projects/Noktiz/mydb.txt");
        sf = configure.buildSessionFactory();
//        com.noktiz.domain.persistance.SessionFactory.sf=sf;
        HSF.configure(sf);
        IRequestCycleListener listener = HSF.get();
        getRequestCycleListeners().add(listener);
        try {
            InitDataFiller.fill();
        } catch (IOException e) {
            throw new Error("error in init");
        }
    }
}
