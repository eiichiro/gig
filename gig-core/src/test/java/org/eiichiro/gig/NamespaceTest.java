package org.eiichiro.gig;

import static org.junit.Assert.*;

import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.scope.Application;
import org.eiichiro.jaguar.scope.Prototype;
import org.eiichiro.jaguar.scope.Request;
import org.eiichiro.jaguar.scope.Session;
import org.eiichiro.jaguar.scope.Singleton;
import org.eiichiro.jaguar.scope.Thread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NamespaceTest {

	@Inject Namespace namespace;
	
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
	public void testGet() {
		try {
			namespace.get(null, "key");
			fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			namespace.get(Singleton.class, null);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			namespace.get(Inject.class, "key");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		assertNull(namespace.get(Singleton.class, "key"));
	}

	@Test
	public void testSet() {
		try {
			namespace.set(null, "key", "value");
			fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			namespace.set(Singleton.class, null, "value");
			fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			namespace.set(Singleton.class, "key", null);
			fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			namespace.set(Inject.class, "key", "value");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		String string = "string";
		namespace.set(Singleton.class, "key", string);
		assertSame(string, namespace.get(Singleton.class, "key"));
		
		String string2 = "string2";
		namespace.set(Application.class, "key", string2);
		assertSame(string2, namespace.get(Application.class, "key"));
		
		String string3 = "string3";
		namespace.set(Session.class, "key", string3);
		assertSame(string3, namespace.get(Session.class, "key"));
		
		String string4 = "string4";
		namespace.set(Request.class, "key", string4);
		assertSame(string4, namespace.get(Request.class, "key"));
		
		String string5 = "string5";
		namespace.set(Thread.class, "key", string5);
		assertSame(string5, namespace.get(Thread.class, "key"));
		
		String string6 = "string6";
		namespace.set(Prototype.class, "key", string6);
		assertNotSame(string6, namespace.get(Prototype.class, "key"));
	}

}
