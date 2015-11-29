package rest.JerseyTest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.noktiz.ui.rest.core.auth.AuthenticationInfoExtractor;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.ServerSocket;

//import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 * Test class which will wire itelf into your the Spring context which
 * is configured on the WebAppDecriptor built for your tests.
 * Ensure you configure annotation-aware support into your contexts,
 * and annotate any auto-wire properties on your test class
 * @author George McIntosh
 *
 */


public abstract class AbstractJerseyTest extends JerseyTest {

	public AbstractJerseyTest() {
		super();
	}

	public AbstractJerseyTest(WebAppDescriptor wad) {
		super(wad);
	}

	@BeforeClass
	public static void setUp3() throws Exception {

//        MakhsanInitDataFiller.main(new String[]{"true", "drop"});
//        MakhsanTestDataFiller.main(new String[]{"true"});

	}

//	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
//		return new SpringAwareGrizzlyTestContainerFactory(this);
//	}

	@Override
	protected int getPort(int defaultPort) {

		ServerSocket server = null;
		int port = -1;
		try {
			server = new ServerSocket(defaultPort);
			port = server.getLocalPort();
		} catch (IOException e) {
			// ignore
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		if ((port != -1) || (defaultPort == 0)) {
			return port;
		}
		return getPort(0);
	}

	@Override
	public AppDescriptor configure() {

		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		WebAppDescriptor wa = new WebAppDescriptor.Builder()
				.contextPath("/")
				.servletClass(com.sun.jersey.spi.container.servlet.ServletContainer.class)
				.initParam(ResourceConfig.FEATURE_TRACE, "true")
				.initParam(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, LoggingFilter.class.getCanonicalName())
				.initParam(ResourceConfig.PROPERTY_CONTAINER_NOTIFIER, LoggingFilter.class.getCanonicalName())
				.initParam(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, LoggingFilter.class.getCanonicalName())
//				.initParam(JSONConfiguration.FEATURE_POJO_MAPPING, "true")
				.addFilter(AuthenticationInfoExtractor.class, "authenticationInfoExtractor")
				.build();
		return wa;
	}

	@Before
	public void setUp2() throws Exception {

//        MakhsanInitDataFiller.main(new String[]{"true", "drop"});
//        MakhsanTestDataFiller.main(new String[]{"true"});

	}


}
