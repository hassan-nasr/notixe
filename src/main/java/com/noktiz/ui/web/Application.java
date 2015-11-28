/*
 * Application.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
package com.noktiz.ui.web;

import com.noktiz.domain.entity.rate.RateInviteBackGroundJob;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.domain.model.jobs.EmailNotifier;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.admin.UrlConsole;
import com.noktiz.ui.web.auth.ForgetPasswordPage;
import com.noktiz.ui.web.auth.SignInPage;
import com.noktiz.ui.web.auth.SingleSignIn;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.behavior.IntroBehavior;
import com.noktiz.ui.web.error.InternalError500;
import com.noktiz.ui.web.error.PageExpired;
import com.noktiz.ui.web.error.PageNotFound404;
import com.noktiz.ui.web.friend.FriendListPage;
import com.noktiz.ui.web.friend.fb.FacebookFriends;
import com.noktiz.ui.web.home.HomePage;
import com.noktiz.ui.web.mail.MailSenderPanel;
import com.noktiz.ui.web.profile.ProfilePage;
import com.noktiz.ui.web.rate.RatingsPage;
import com.noktiz.ui.web.rate.context.RateContextManagePage;
import com.noktiz.ui.web.settings.SettingsPage;
import com.noktiz.ui.web.social.FacebookConnectPage;
import com.noktiz.ui.web.start.Welcome;
import com.noktiz.ui.web.start.Welcome2;
import com.noktiz.ui.web.staticp.legal.PrivacyPolicy;
import com.noktiz.ui.web.staticp.legal.TermsOfUse;
import com.noktiz.ui.web.thread.ThreadPage;
import com.noktiz.ui.web.user.ActivatePage;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.response.NullResponse;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Timer;

//import com.noktiz.domain.model.email.tester;


/**
 *
 * @author hossein
 * @version
 */
public class Application extends AuthenticatedWebApplication {
    //    public static Properties conf= new Properties();
    protected static SessionFactory sf;
    public static MetaDataKey<List<IntroBehavior.Intro>> introKey= new MetaDataKey<List<IntroBehavior.Intro>>() {
    };
    public Application() {
    }

    @Override
    protected void init() {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        configureHibernate();
//        setRootRequestMapper(new CryptoMapper(getRootRequestMapper(), this));
/*
        mount(new MountedMapperWithoutPageComponentInfo("/start", StartConversation.class));
        mount(new MountedMapperWithoutPageComponentInfo("/", HomePage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/signin", SignInPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/friends", FriendListPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/settings", SettingsPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/facebookConnect", FacebookConnectPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/googleConnect", GetGooglePermitionPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/temp", TempPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/FacebookFriends", FacebookFriends.class));
        mount(new MountedMapperWithoutPageComponentInfo("/registerCredential", RegisterConnection.class));
        mount(new MountedMapperWithoutPageComponentInfo("/activate", ActivatePage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/ssi", SingleSignIn.class));
        mount(new MountedMapperWithoutPageComponentInfo("/ratings", RatingsPage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/rateManage", RateContextManagePage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/profile",ProfilePage.class));
        mount(new MountedMapperWithoutPageComponentInfo("/thread",ThreadPage.class));
*/
/*
        mountPage("/start", StartConversation.class);
        mountPage("/", HomePage.class);
        mountPage("/signin", SignInPage.class);
        mountPage("/friends", FriendListPage.class);
        mountPage("/settings", SettingsPage.class);
        mountPage("/facebookConnect", FacebookConnectPage.class);
        mountPage("/googleConnect", GetGooglePermitionPage.class);
        mountPage("/temp", TempPage.class);
        mountPage("/FacebookFriends", FacebookFriends.class);
        mountPage("/registerCredential", RegisterConnection.class);
        mountPage("/activate", ActivatePage.class);
        mountPage("/ssi", SingleSignIn.class);
        mountPage("/ratings", RatingsPage.class);
        mountPage("/rateManage", RateContextManagePage.class);
        mountPage("/profile", ProfilePage.class);
        mountPage("/thread", ThreadPage.class);
        mountResource("/favicon.ico", new PackageResourceReference(Application.class,"favicon.png"));
*/


//        getRootRequestMapperAsCompound().add(new NoVersionMapper("/start", StartConversation.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/home", HomePage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/login", SignInPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/forgetPassword", ForgetPasswordPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/friends", FriendListPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/settings", SettingsPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/facebookConnect", FacebookConnectPage.class));
        //getRootRequestMapperAsCompound().add(new NoVersionMapper("/googleConnect", GetGooglePermitionPage.class));
        //getRootRequestMapperAsCompound().add(new NoVersionMapper("/MailTest", tester.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/temp", TempPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/FacebookFriends", FacebookFriends.class));
//        getRootRequestMapperAsCompound().add(new NoVersionMapper("/registerCredential", RegisterConnection.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/activate", ActivatePage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/ssi", SingleSignIn.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/ratings/${sub}", RatingsPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/ratings", RatingsPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/rateManage", RateContextManagePage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/profile",ProfilePage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/thread",ThreadPage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/welcome",Welcome.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/legal/terms",TermsOfUse.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/legal/privacy",PrivacyPolicy.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/console",UrlConsole.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/publishMail",MailSenderPanel.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/welcome",Welcome2.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/404",PageNotFound404.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/500",InternalError500.class));
        mountResource("/favicon.ico", new PackageResourceReference(Application.class,"favicon.png"));
        mountResource("/logo.png", new PackageResourceReference(Application.class,"Notixe-glow.png"));

        enableResources();
        getDebugSettings().setDevelopmentUtilitiesEnabled(false);
        getApplicationSettings().setInternalErrorPage(InternalError500.class);
        getApplicationSettings().setAccessDeniedPage(PageNotFound404.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpired.class);
        initialJobs();
        loadProperties();
        checkMigration();
        /*
         getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy()
         {
         public boolean isActionAuthorized(Component component, Action action)
         {
         // authorize everything
         return true;
         }

         public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
         Class<T> componentClass)
         {
         // Check if the new Page requires authentication (implements the marker interface)
         if (com.noktiz.ui.web.auth.AuthenticatedWebPage.class.isAssignableFrom(componentClass))
         {
         // Is user signed in?
         if (((UserSession)Session.get()).isSignedIn())
         {
         // okay to proceed
         return true;
         }

         // Intercept the request, but remember the target for later.
         // Invoke Component.continueToOriginalDestination() after successful logon to
         // continue with the target remembered.

         throw new RestartResponseAtInterceptPageException(com.noktiz.ui.web.auth.SignInPage.class);
         }

         // okay to proceed
         return true;
         }
         });
         */
    }

    private void checkMigration() {
//        EmailIdMigration.Run();
    }


    @Override
    public Class<? extends Page> getHomePage() {
        return Welcome.class;
    }

    public static SessionFactory getSf() {
        return sf;
    }

    public void configureHibernate() {
        sf = new Configuration().configure().buildSessionFactory();
//        com.noktiz.domain.persistance.SessionFactory.sf=sf;
        HSF.configure(sf);
        IRequestCycleListener listener = HSF.get();
        getRequestCycleListeners().add(listener);
    }

    private void initialJobs(){
        //Email
        Timer time= new Timer();
        EmailNotifier emailNotifier= new EmailNotifier();
        Long emailNotificationPeriod = SystemConfigManager.getCurrentConfig().getPropertyAsLong("emailNotificationPeriod",7 * 24 * 60 * 60 * 1000l);
        time.scheduleAtFixedRate(emailNotifier, 0, emailNotificationPeriod);

        //RateInvite
        RateInviteBackGroundJob rateInviteBackGroundJob = new RateInviteBackGroundJob();
        time.schedule(rateInviteBackGroundJob,0,60*1000);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return UserSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    private void enableResources() {
        ImageManagement.loadResourece(this);
//        mountResource("/src/*",new SharedResourceReference(Application.class,"*"));

    }

    private void loadProperties() {
//        try {
//            conf.load(Application.class.getResourceAsStream("conf.properties"));
//            SystemConfigManager.getCurrentConfig().getAppName();
//
//        } catch (IOException ex) {
//            Logger.getLogger(Application.class).fatal("can not load properties",ex);
//        }
    }

    private static class NoVersionMapper extends MountedMapper {
        public NoVersionMapper(final Class<? extends IRequestablePage> pageClass) {
            this("/", pageClass);
        }

        public NoVersionMapper(String mountPath, final Class<? extends IRequestablePage> pageClass) {
            super(mountPath, pageClass, new PageParametersEncoder());
//            super(mountPath, pageClass, new UrlPathPageParametersEncoder());

        }

        @Override
        protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
            //Does nothing
        }

        @Override
        public Url mapHandler(IRequestHandler requestHandler) {
            if (requestHandler instanceof ListenerInterfaceRequestHandler || requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
                return null;
            } else {
                return super.mapHandler(requestHandler);
            }
        }
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
//        return RuntimeConfigurationType.DEPLOYMENT;
        return RuntimeConfigurationType.DEVELOPMENT;
    }
    public static JobHandler beginInnerJob(){
        return new JobHandler(new RequestCycle(new RequestCycleContext(new MockWebRequest(null), NullResponse.getInstance(), new BookmarkableMapper(), new DefaultExceptionMapper())));
    }
    public static class JobHandler{
        RequestCycle rc;

        public JobHandler(RequestCycle rc) {
            this.rc = rc;
        }

        public void endJob(){
            HSF.get().onEndRequest(rc);
        }
    }

}
