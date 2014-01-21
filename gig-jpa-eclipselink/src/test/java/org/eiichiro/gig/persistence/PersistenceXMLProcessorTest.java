package org.eiichiro.gig.persistence;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.eiichiro.gig.persistence.PersistenceXMLProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersistenceXMLProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		PersistenceXMLProcessor processor = new PersistenceXMLProcessor();
		processor.process();
		assertThat(processor.persistenceUnits().size(), is(2));
		assertThat(processor.persistenceUnits().get(0), is("unit-0"));
		assertThat(processor.persistenceUnits().get(1), is("unit-1"));
	}

	@Test
	public void testPersistenceUnits() {}

}
