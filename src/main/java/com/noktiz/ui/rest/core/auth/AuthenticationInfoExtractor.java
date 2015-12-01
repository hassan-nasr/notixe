package com.noktiz.ui.rest.core.auth;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.SimpleResponse;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

/**
 * a filter to extract token information form request and put it in request parameters
 * if token is valid this parameters are also available:
 * String userId, Set of String userRoles , Set of String permissions;
 * Created by hassan on 03/11/2015.
 */
public class AuthenticationInfoExtractor implements Filter {


    TokenManager tokenManager = new TokenManager();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getParameter("accessToken");
        if (token != null && !token.isEmpty()) {
            try {
                TokenData tokenData = tokenManager.extractTokenFromString(token);
                request.setAttribute("token", tokenData);
                if (tokenData.getExpireDate().getTime() >= new Date().getTime()) {
                    request.setAttribute("userInSite", new UserFacade(User.load(Long.valueOf(tokenData.userId))));
                    request.setAttribute("userId", tokenData.getUserId());
                    request.setAttribute("userRoles", tokenData.getRoles());
                    request.setAttribute("userPermissions", tokenData.getPermissions());
                }
            } catch (Exception e) {
                response.getWriter().append(BaseWS.createSimpleResponse(SimpleResponse.Status.Failed,"Invalid Token"));
                return;
            }
        }
        try {
            chain.doFilter(request, response);
        }catch (Exception e){
            throw e;
//            response.reset();
//            new PrintStream(response.getOutputStream()).print(BaseWS.createSimpleResponse(SimpleResponse.Status.Failed,"Error Processing Request"));
        }
    }

    @Override
    public void destroy() {

    }
}
