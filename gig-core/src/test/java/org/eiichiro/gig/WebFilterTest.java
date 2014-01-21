package org.eiichiro.gig;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebFilterTest {

	private ServletTester tester = new ServletTester();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitFilterConfig() {}

	@Test
	public void testDoFilterServletRequestServletResponseFilterChain() throws Exception {
		tester.setContextPath("/gig");
		tester.addEventListener(new WebListener());
		tester.addFilter(WebFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
		HttpTester request = new HttpTester();
		request.setURI("/gig/Endpoint3/test");
		request.setMethod("GET");
		request.setHeader("Host", "");
		String req = request.generate();
		HttpTester response = new HttpTester();
		String res = tester.getResponses(req);
		response.parse(res);
		assertThat(response.getStatus(), is(200));
		tester.stop();
	}

	@Test
	public void testConfigurationFilterConfig() throws Exception {
		tester.setContextPath("/gig");
		tester.addFilter(WebFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.start();
		assertTrue(tester.getContext().isFailed());
		tester.stop();
		
		tester.addEventListener(new WebListener());
		tester.start();
		assertFalse(tester.getContext().isFailed());
		tester.stop();
	}

}
