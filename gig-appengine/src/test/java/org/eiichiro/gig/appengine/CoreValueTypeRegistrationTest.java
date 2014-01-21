package org.eiichiro.gig.appengine;

import static org.junit.Assert.*;

import org.eiichiro.gig.Gig;
import org.eiichiro.bootleg.Types;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.Text;

public class CoreValueTypeRegistrationTest {

	@Before
	public void setUp() throws Exception {
		Gig.bootstrap();
	}

	@After
	public void tearDown() throws Exception {
		Gig.shutdown();
	}

	@Test
	public void testRegister() {
		assertTrue(Types.isCoreValueType(Text.class));
		assertTrue(Types.isCoreValueType(Key.class));
		assertTrue(Types.isCoreValueType(Category.class));
		assertTrue(Types.isCoreValueType(Email.class));
		assertTrue(Types.isCoreValueType(GeoPt.class));
		assertTrue(Types.isCoreValueType(Link.class));
		assertTrue(Types.isCoreValueType(PhoneNumber.class));
		assertTrue(Types.isCoreValueType(PostalAddress.class));
		assertTrue(Types.isCoreValueType(Rating.class));
	}

}
