/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.mersad.test.jetty;

import com.noktiz.ui.rest.core.auth.AuthenticationInfoExtractor;
import com.noktiz.ui.web.Application;
import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

/**
 *
 * @author MasterJ
 */
public class RunSample {
    public static final int portNumber = 8093;
    
    public static void main(String[] args) throws Exception {
        
        System.out.println("Say goodbye to YOONES!");
        
        //kill the last opened socket
//        Runtime.getRuntime().exec("fuser -k -n tcp "+portNumber);
        
        Server server = new Server(portNumber);
        /* Setup server (port, etc.) */

        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ServletHolder sh = new ServletHolder(WicketServlet.class);
//        ServletHolder rest = new ServletHolder(com.sun.jersey.spi.container.servlet.ServletContainer.class);
        ServletHolder rest = new ServletHolder(org.glassfish.jersey.servlet.ServletContainer.class);




//        sh.setInitParameter("wicket.configuration", "development");
        sh.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, Application.class.getName());
        sh.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        rest.setInitParameter("jersey.config.server.provider.packages","com.noktiz.ui.rest.services");
        /* Define a variable DEV_MODE and set to false
         * if wicket should be used in deployment mode
         */

//        Boolean DEV_MODE=true;
//        if (!DEV_MODE) {
//            sh.setInitParameter("wicket.configuration" , "deployment");
//        }else{
////            sh.setInitParameter("wicket.configuration", "development");
//        }

        sch.addServlet(sh, "/*");
        sch.addServlet(rest, "/rest/*");
        sch.addFilter(AuthenticationInfoExtractor.class,"/rest/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(sch);
        server.start();
        server.join();
    }
}
