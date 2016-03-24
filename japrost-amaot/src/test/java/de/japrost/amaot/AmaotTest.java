package de.japrost.amaot;

import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link Amaot}
 */
public class AmaotTest {
	@SuppressWarnings("unused")
	private Amaot cut;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		cut = new Amaot();
	}

	/**
	 * Just start.
	 */
	@Test
	public void mainWithoutArgs()  {
		Amaot.main(new String[0]);
	}
}
