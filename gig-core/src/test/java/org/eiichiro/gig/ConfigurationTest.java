package org.eiichiro.gig;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.bootleg.GenericRequest;
import org.eiichiro.bootleg.GenericResponse;
import org.eiichiro.bootleg.MediaType;
import org.eiichiro.bootleg.Pipeline;
import org.eiichiro.bootleg.Request;
import org.eiichiro.bootleg.Response;
import org.eiichiro.bootleg.Routing;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.bootleg.json.JSONRequest;
import org.eiichiro.bootleg.json.JSONResponse;
import org.eiichiro.bootleg.xml.XMLRequest;
import org.eiichiro.bootleg.xml.XMLResponse;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.reverb.system.Environment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

	@Inject Configuration configuration;
	
	@Before
	public void setUp() throws Exception {
		Gig.bootstrap();
		Jaguar.assemble(this);
	}

	@After
	public void tearDown() throws Exception {
		Gig.shutdown();
	}

	@Test
	public void testLoad() {
		configuration.load("configuration/META-INF/notexist.js");
		configuration.load("configuration/META-INF/settings-test.js");
		String test = configuration.get("test");
		assertThat(test, is("test"));
	}

	@Test
	public void testGet() throws Exception {
		try {
			configuration.get(null);
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			configuration.get("");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		String value1 = configuration.get("value1");
		assertNull(value1);
		configuration.set("value1", "value1");
		value1 = configuration.get("value1");
		assertThat(value1, is("value1"));
		assertNull(configuration.get(Configuration.ENDPOINTS));
		assertNull(configuration.get(Configuration.PIPELINE));
		assertNull(configuration.get(Configuration.REQUEST_TYPES));
		assertNull(configuration.get(Configuration.RESPONSE_TYPES));
		assertNull(configuration.get(Configuration.ROUTING));
		assertNull(configuration.get(Configuration.DEPLOYMENT));
		Collection<Class<?>> components = configuration.get(Configuration.COMPONENTS);
		assertThat(components.size(), is(7));
		assertTrue(components.contains(Component2.class));
		assertTrue(components.contains(Endpoint3.class));
		assertTrue(components.contains(Endpoint5.class));
		assertTrue(components.contains(Object3.class));
		assertTrue(components.contains(Object5.class));
		assertTrue(components.contains(Object6.class));
		assertTrue(components.contains(Object7.class));
		
		Gig.shutdown();
		ServletTester tester = new ServletTester();
		tester.setContextPath("/gig");
		tester.addEventListener(new WebListener());
		tester.addFilter(WebFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
		Jaguar.assemble(this);
		Collection<Class<?>> endpoints = configuration.get(Configuration.ENDPOINTS);
		assertThat(endpoints.size(), is(2));
		assertTrue(endpoints.contains(Endpoint3.class));
		assertTrue(endpoints.contains(Endpoint5.class));
		Pipeline<WebContext> pipeline = configuration.get(Configuration.PIPELINE);
		assertThat(pipeline.toString(), is("Stage-0 [Route] -> Stage-1 [Receive] -> Stage-2 [JaguarInvoke] -> Stage-3 [Send]"));
		Map<String, Class<? extends Request>> requestTypes = configuration.get(Configuration.REQUEST_TYPES);
		assertThat(requestTypes.get(MediaType.APPLICATION_JSON), is((Object) JSONRequest.class));
		assertThat(requestTypes.get(MediaType.APPLICATION_XML), is((Object) XMLRequest.class));
		assertThat(requestTypes.get(MediaType.TEXT_HTML), is((Object) GenericRequest.class));
		assertThat(requestTypes.get(MediaType.TEXT_PLAIN), is((Object) GenericRequest.class));
		assertThat(requestTypes.get(""), is((Object) GenericRequest.class));
		Map<String, Class<? extends Response>> responseTypes = configuration.get(Configuration.RESPONSE_TYPES);
		assertThat(responseTypes.get(MediaType.APPLICATION_JSON), is((Object) JSONResponse.class));
		assertThat(responseTypes.get(MediaType.APPLICATION_XML), is((Object) XMLResponse.class));
		assertThat(responseTypes.get(MediaType.TEXT_HTML), is((Object) GenericResponse.class));
		assertThat(responseTypes.get(MediaType.TEXT_PLAIN), is((Object) GenericResponse.class));
		assertThat(responseTypes.get(""), is((Object) GenericResponse.class));
		Routing routing = configuration.get(Configuration.ROUTING);
		assertNotNull(routing);
		tester.stop();
		
		List<URL> urls = new ArrayList<>();
		
		for (String path : Environment.getProperty("java.class.path").split(File.pathSeparator)) {
			urls.add(new File(path).toURI().toURL());
		}
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		for (URL url : urls) {
			if (url.getPath().endsWith("gig-core/target/test-classes/")) {
				Thread.currentThread().setContextClassLoader(
						URLClassLoader.newInstance(
								new URL[] {new URL(url + "configuration/")}, 
								loader));
			}
		}
		
		Gig.bootstrap();
		Jaguar.assemble(this);
		endpoints = configuration.get(Configuration.ENDPOINTS);
		assertThat(endpoints.size(), is(1));
		assertTrue(endpoints.contains(Endpoint5.class));
		pipeline = configuration.get(Configuration.PIPELINE);
		assertThat(pipeline.toString(), is("Stage-0 [Pipeline]"));
		requestTypes = configuration.get(Configuration.REQUEST_TYPES);
		assertThat(requestTypes.get("JSONRequest"), is((Object) JSONRequest.class));
		responseTypes = configuration.get(Configuration.RESPONSE_TYPES);
		assertThat(responseTypes.get("JSONResponse"), is((Object) JSONResponse.class));
		Routing routing2 = configuration.get(Configuration.ROUTING);
		assertNotSame(routing, routing2);
		String test1 = configuration.get("test1");
		assertThat(test1, is("test1"));
		double test2 = configuration.get("test2");
		assertThat(test2, is(1d));
		test2 = configuration.get("test2");
		assertThat(test2, is(2d));
		Thread.currentThread().setContextClassLoader(loader);
	}

	@Test
	public void testSet() {
		try {
			configuration.set(null, "value");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			configuration.set("", "value");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		configuration.set("value", "value");
		String value = configuration.get("value");
		assertThat(value, is("value"));
		configuration.set("null", null);
		assertNull(configuration.get("null"));
	}

}
