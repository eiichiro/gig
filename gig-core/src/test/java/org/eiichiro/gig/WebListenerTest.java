package org.eiichiro.gig;

import static org.junit.Assert.*;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.ServletTester;
import org.eiichiro.jaguar.Jaguar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebListenerTest {

	private ServletTester tester = new ServletTester();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testContextInitializedServletContextEvent() throws Exception {
		tester.setContextPath("/gig");
		tester.addEventListener(new WebListener());
		tester.addFilter(WebFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
		assertTrue(Jaguar.running());
		assertTrue(Jaguar.installed(Configuration.class));
		assertTrue(Jaguar.installed(Namespace.class));
		tester.stop();
	}

	@Test
	public void testContextDestroyedServletContextEvent() throws Exception {
		tester.setContextPath("/gig");
		tester.addEventListener(new WebListener());
		tester.addFilter(WebFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
		tester.stop();
		assertFalse(Jaguar.running());
	}

}
