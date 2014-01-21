package org.eiichiro.gig.appengine;

import static org.junit.Assert.*;

import org.eiichiro.acidhouse.Session;
import org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession;
import org.eiichiro.gig.Gig;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.inject.Name;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class AppEngineDatastoreSessionComponentTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
	
	@Before
	public void setUp() throws Exception {
		helper.setUp();
		Gig.bootstrap();
	}

	@After
	public void tearDown() throws Exception {
		Gig.shutdown();
		helper.tearDown();
	}
	
	@Inject private Session session;
	
	@Inject @Name("org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession")
	private Session session2;
	
	@Inject private AppEngineDatastoreSession session3;
	
	@Inject @Name("org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession")
	private AppEngineDatastoreSession session4;
	
	@Test
	public void testInstance() {
		Jaguar.assemble(this);
		assertNotNull(session);
		assertNotNull(session2);
		assertNotNull(session3);
		assertNotNull(session4);
		assertSame(session, session2);
		assertSame(session, session3);
		assertSame(session, session4);
	}

}
