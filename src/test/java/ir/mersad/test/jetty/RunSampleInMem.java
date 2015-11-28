/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.mersad.test.jetty;
import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author MasterJ
 */
public class RunSampleInMem {
    public static final int portNumber = 8093;
    
    public static void main(String[] args) throws Exception {
        
        System.out.println("Say goodby to YOONES!");
        
        //kill the last opened socket
//        Runtime.getRuntime().exec("fuser -k -n tcp "+portNumber);
        
        Server server = new Server(portNumber);
        /* Setup server (port, etc.) */

        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ServletHolder sh = new ServletHolder(WicketServlet.class);


        sh.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, TestApplication.class.getName());
        sh.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        /* Define a variable DEV_MODE and set to false
         * if wicket should be used in deployment mode
         */
//        if (!DEV_MODE) {
//            sh.setInitParameter("wicket.configuration", "deployment");
//        }
        
        sch.addServlet(sh, "/*");
        server.setHandler(sch);
        server.start();
        server.join();
    }
}
