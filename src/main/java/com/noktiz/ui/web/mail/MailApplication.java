/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.mail;

import org.apache.wicket.Application;
import org.apache.wicket.IPageRendererProvider;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.core.request.ClientInfo;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.render.PageRenderer;
import org.apache.wicket.request.handler.render.WebPageRenderer;
import org.apache.wicket.session.HttpSessionStore;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.IProvider;

/**
 *
 * @author Hossein
 */
public class MailApplication extends Application {
    static MailApplication instance= new MailApplication();
    public static MailApplication get(){
        return instance;
    }
    private MailApplication() {
        super();
    }
    
    @Override
    public String getApplicationKey() {
        return "mailer";
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEPLOYMENT;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return null;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new Session(request) {
            @Override
            public ClientInfo getClientInfo() {
                return new ClientInfo() {
                };
            }
        };
    }

    @Override
    protected void init() {
        setPageRendererProvider(new WebPageRendererProvider());
        setSessionStoreProvider(new WebSessionStoreProvider());
    }

    class WebPageRendererProvider implements IPageRendererProvider {

        @Override
        public PageRenderer get(RenderPageRequestHandler handler) {
            return new WebPageRenderer(handler);
        }
    }

    class WebSessionStoreProvider implements IProvider<ISessionStore> {

        @Override
        public ISessionStore get() {
            return new HttpSessionStore();
        }
    }
}
