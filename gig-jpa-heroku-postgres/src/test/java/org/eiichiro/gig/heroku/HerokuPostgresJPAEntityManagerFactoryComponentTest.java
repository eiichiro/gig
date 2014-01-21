package org.eiichiro.gig.heroku;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eiichiro.gig.Configuration;
import org.eiichiro.gig.Gig;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.reverb.system.Environment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.Driver;

public class HerokuPostgresJPAEntityManagerFactoryComponentTest {

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
		assertNull(factory);
		
		Gig.shutdown();
		Gig.bootstrap();
		Environment.setenv("DATABASE_URL", "invalid.url");
		
		try {
			factory = Jaguar.component(EntityManagerFactory.class);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Gig.shutdown();
		Gig.bootstrap();
		Environment.setenv("DATABASE_URL", "postgres://user:password@hostname/path");
		factory = Jaguar.component(EntityManagerFactory.class);
		Map<String, Object> properties = factory.getProperties();
		assertThat(properties.size(), is(4));
		assertThat(properties.get("javax.persistence.jdbc.driver").toString(), is(Driver.class.getName()));
		assertThat(properties.get("javax.persistence.jdbc.url").toString(), is("jdbc:postgresql://hostname/path"));
		assertThat(properties.get("javax.persistence.jdbc.user").toString(), is("user"));
		assertThat(properties.get("javax.persistence.jdbc.password").toString(), is("password"));
		
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
