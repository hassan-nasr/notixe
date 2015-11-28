/*
package com.noktiz.ui.web.theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.WicketEventJQueryResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.settings.IResourceSettings;


@Deprecated
public final class MyBootstrap
{
    public static JavaScriptResourceReference plain()
    {
        return LocalizationUtils.get().isRTL().booleanValue() ? bootstrapRTLPlain : bootstrapPlain;
    }

    public static JavaScriptResourceReference responsive()
    {
        return LocalizationUtils.get().isRTL().booleanValue() ? bootstrapRTLResponsive : bootstrapResponsive;
    }

    public void renderHeadPlain(IHeaderResponse response)
    {
        response.render(JavaScriptHeaderItem.forReference(plain()));
        HeaderItem jasnyJsHeaderItem = JavaScriptHeaderItem.forReference(jasnyJs);
        HeaderItem jasnyStyleSheet = CssHeaderItem.forReference(jasnyCss);
        response.render(jasnyStyleSheet);
        response.render(jasnyJsHeaderItem);


        installIconsFont(response);
        for (CssResourceReference cssRr : this.cssRefs) {
            response.render(CssHeaderItem.forReference(cssRr));
        }
    }

    public void renderHeadResponsive(IHeaderResponse response)
    {
        response.render(JavaScriptHeaderItem.forReference(
                responsive()));

        HeaderItem jasnyJsHeaderItem = JavaScriptHeaderItem.forReference(jasnyJs);
        HeaderItem jasnyStyleSheet = CssHeaderItem.forReference(jasnyCss);
        HeaderItem jasnyResponsive = CssHeaderItem.forReference(jasnyResponsiveCss);
        response.render(jasnyStyleSheet);
        response.render(jasnyResponsive);
        response.render(jasnyJsHeaderItem);

        installIconsFont(response);
        for (CssResourceReference cssRr : this.cssRefs) {
            response.render(CssHeaderItem.forReference(cssRr));
        }
    }

    private static final BootstrapRTLResourceReference bootstrapRTLPlain = new BootstrapRTLResourceReference();
    private static final BootstrapResourceReference bootstrapPlain = new BootstrapResourceReference();
    private static final JavaScriptResourceReference jasnyJs = new JavaScriptResourceReference(Application.class, "assets/bootstrap/jasnyBootstrap/js/jasny-bootstrap.js");
    private static final CssResourceReference jasnyResponsiveCss = new CssResourceReference(Application.class, "assets/bootstrap/jasnyBootstrap/css/jasny-bootstrap-responsive.css");
    private static final CssResourceReference jasnyCss = new CssResourceReference(Application.class, "assets/bootstrap/jasnyBootstrap/css/jasny-bootstrap.css");
    private static final CssResourceReference fontAwesome = new CssResourceReference(Application.class, "assets/bootstrap/fontawesome/css/font-awesome.css");
    private static final CssResourceReference fontAwesomeIE7 = new CssResourceReference(Application.class, "assets/bootstrap/fontawesome/css/font-awesome-ie7.css");
    private static final BootstrapRTLResponsiveResourceReference bootstrapRTLResponsive = new BootstrapRTLResponsiveResourceReference();
    private static final CssResourceReference bootstrapRTLCss = new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap-rtl.css");
    private static final CssResourceReference bootstrapResponsiveRTLCss = new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap-responsive-rtl.css");
    private static final BootstrapResponsiveResourceReference bootstrapResponsive = new BootstrapResponsiveResourceReference();
    private static final CssResourceReference bootstrapCss = new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap.css");
    private static final CssResourceReference bootstrapResponsiveCss = new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap-responsive.css");
    private List<CssResourceReference> cssRefs = new ArrayList();

    private void allowGetFonts()
    {
        SecurePackageResourceGuard guard = new SecurePackageResourceGuard();

        guard.addPattern("+*iconic_stroke.otf");
        guard.addPattern("+*iconic_fill.otf");
        guard.addPattern("+*iconic_stroke.ttf");
        guard.addPattern("+*iconic_fill.ttf");
        guard.addPattern("+*iconic_stroke.woff");
        guard.addPattern("+*iconic_fill.woff");
        guard.addPattern("+*fontawesome-webfont.ttf");
        guard.addPattern("+*fontawesome-webfont.woff");
        guard.addPattern("+*fontawesome-webfont.eot");
        guard.addPattern("+*fontawesome-webfont.svg");
        guard.addPattern("+*bvahid.ttf");
        guard.addPattern("+*bvahid.woff");
        guard.addPattern("+*bvahid.eot");
        guard.addPattern("+*bvahid.svg");

        guard.addPattern("+*bmitra.ttf");
        guard.addPattern("+*bmitra.woff");
        guard.addPattern("+*bmitra.eot");
        guard.addPattern("+*bmitra.svg");

        guard.addPattern("+*BMitraBold.ttf");
        guard.addPattern("+*BMitraBold.woff");
        guard.addPattern("+*BMitraBold.eot");
        guard.addPattern("+*BMitraBold.svg");

        guard.addPattern("+*FontAwesome.otf");
        guard.addPattern("+*iconic_stroke.eot");
        guard.addPattern("+*iconic_fill.eot");

        guard.addPattern("+*.map");
        Application.get().getResourceSettings().setPackageResourceGuard(guard);
    }

    private void installIconsFont(IHeaderResponse response)
    {
        allowGetFonts();

        response.render(CssHeaderItem.forReference(fontAwesome));





        response.render(CssHeaderItem.forReference(fontAwesomeIE7, null, null, "IE 7"));
    }

    public MyBootstrap()
    {
        if (LocalizationUtils.get().getLang() == Language.fa) {
            addCss(Application.class, "assets/bootstrap/css/defaultBootstrapLessTheme.css");
        }
    }

    public MyBootstrap addCss(Class<?> scope, String cssFileName)
    {
        this.cssRefs.add(new CssResourceReference(scope, cssFileName));
        return this;
    }

    private static class BootstrapRTLResourceReference
            extends JavaScriptResourceReference
    {
        private static final long serialVersionUID = 1L;

        public BootstrapRTLResourceReference()
        {
            super(Application.class, "assets/bootstrap/js/bootstrap-rtl.js");
        }

        public Iterable<? extends HeaderItem> getDependencies()
        {
            HeaderItem jquery = JavaScriptHeaderItem.forReference(WicketEventJQueryResourceReference.get());
            HeaderItem stylesheet = CssHeaderItem.forReference(MyBootstrap.bootstrapRTLCss);









            return Arrays.asList(new HeaderItem[] { jquery, stylesheet });
        }
    }

    private static class BootstrapRTLResponsiveResourceReference
            extends JavaScriptResourceReference
    {
        private static final long serialVersionUID = 1L;

        public BootstrapRTLResponsiveResourceReference()
        {
            super(Application.class, "assets/bootstrap/js/bootstrap-rtl.js");
        }

        public Iterable<? extends HeaderItem> getDependencies()
        {
            HeaderItem jquery = JavaScriptHeaderItem.forReference(WicketEventJQueryResourceReference.get());
            HeaderItem stylesheet = CssHeaderItem.forReference(MyBootstrap.bootstrapRTLCss);

            HeaderItem responsive = CssHeaderItem.forReference(MyBootstrap.bootstrapResponsiveRTLCss);








            return Arrays.asList(new HeaderItem[] { jquery, stylesheet, responsive });
        }
    }

    private static class BootstrapResourceReference
            extends JavaScriptResourceReference
    {
        private static final long serialVersionUID = 1L;

        public BootstrapResourceReference()
        {
            super(Application.class, "assets/bootstrap/js/bootstrap.js");
        }

        public Iterable<? extends HeaderItem> getDependencies()
        {
            HeaderItem jquery = JavaScriptHeaderItem.forReference(WicketEventJQueryResourceReference.get());
            HeaderItem stylesheet = CssHeaderItem.forReference(MyBootstrap.bootstrapCss);









            return Arrays.asList(new HeaderItem[] { jquery, stylesheet });
        }
    }

    private static class BootstrapResponsiveResourceReference
            extends JavaScriptResourceReference
    {
        private static final long serialVersionUID = 1L;

        public BootstrapResponsiveResourceReference()
        {
            super(Application.class, "assets/bootstrap/js/bootstrap.js");
        }

        public Iterable<? extends HeaderItem> getDependencies()
        {
            HeaderItem jquery = JavaScriptHeaderItem.forReference(WicketEventJQueryResourceReference.get());
            HeaderItem stylesheet = CssHeaderItem.forReference(MyBootstrap.bootstrapCss);

            HeaderItem responsive = CssHeaderItem.forReference(MyBootstrap.bootstrapResponsiveCss);








            return Arrays.asList(new HeaderItem[] { jquery, stylesheet, responsive });
        }
    }
}
*/
