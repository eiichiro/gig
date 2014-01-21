package org.eiichiro.gig.persistence;

import static org.junit.Assert.*;

import javax.persistence.EntityManagerFactory;

import org.eiichiro.gig.Configuration;
import org.eiichiro.gig.Gig;
import org.eiichiro.jaguar.Jaguar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JPAEntityManagerFactoryComponentTest {

	@Before
	public void setUp() throws Exception {
		Gig.bootstrap();
	}

	@After
	public void tearDown() throws Exception {
		Gig.shutdown();
	}

	@Test
	public void testConfigure() {
		EntityManagerFactory factory = Jaguar.component(EntityManagerFactory.class);
		assertNotNull(factory);
		Gig.shutdown();
		Gig.bootstrap();
		Configuration configuration = Jaguar.component(Configuration.class);
		configuration.load("META-INF/persistence-unit.js");
		factory = Jaguar.component(EntityManagerFactory.class);
		assertNotNull(factory);
		Gig.shutdown();
		Gig.bootstrap();
		configuration = Jaguar.component(Configuration.class);
		configuration.load("META-INF/persistence-unit2.js");
		
		try {
			factory = Jaguar.component(EntityManagerFactory.class);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInstance() {}

}
