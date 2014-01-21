package org.eiichiro.gig.appengine;

import static org.junit.Assert.*;

import org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession;
import org.eiichiro.gig.Gig;
import org.eiichiro.jaguar.Jaguar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class AppEngineTransactionalInterceptorTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	
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

	@Test
	public void testTransact() {
		AppEngineTransactionalInterceptorTestObject component = Jaguar.component(AppEngineTransactionalInterceptorTestObject.class);
		component.method1();
		AppEngineDatastoreSession session = new AppEngineDatastoreSession();
		AppEngineTransactionalInterceptorTestEntity entity = session.get(AppEngineTransactionalInterceptorTestEntity.class, "Key1");
		assertNotNull(entity);
		
		try {
			component.method2();
			fail();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		assertNull(session.get(AppEngineGlobalTransactionalInterceptorTestEntity.class, "Key2"));
	}

}
