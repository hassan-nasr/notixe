package com.noktiz.ui.rest.core;

import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.rest.core.auth.TokenManager;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.response.NullResponse;
import org.hibernate.cfg.Configuration;

import javax.servlet.*;
import java.io.IOException;

/**
 * a filter to extract token information form request and put it in request parameters
 * if token is valid this parameters are also available:
 * String userId, Set of String userRoles , Set of String permissions;
 * Created by hassan on 03/11/2015.
 */
public class HibernateFilter implements Filter {


    TokenManager tokenManager = new TokenManager();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        configureSessionFactory();
        rc = new RequestCycle(new RequestCycleContext(new MockWebRequest(null), NullResponse.getInstance(), new BookmarkableMapper(), new DefaultExceptionMapper()));
        HSF.get().onBeginRequest(rc);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
    private static boolean configured=false;
    private static void configureSessionFactory(){
        if(!configured){
            HSF.configure(new Configuration().configure().buildSessionFactory());
            configured=true;
        }
    }
    RequestCycle rc;
    public void onEndRequest(){
        HSF.get().onEndRequest(rc);
    }

    @Override
    public void destroy() {
        onEndRequest();
    }
}
