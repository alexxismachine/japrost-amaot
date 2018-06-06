package de.japrost.amaot;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.japrost.amaot.ui.swing.SwingUI;

/**
 * Test {@link Amaot}
 */
public class AmaotTest {

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
	 * 
	 * @throws Exception
	 */
	@Test
	public void mainWithoutArgs() throws Exception {
		Amaot.main(new String[0]);
		// Let the ui show. Won't work on headless build server.
		Thread.sleep(1000);
	}

	@Test
	public void adjustTime() throws Exception {
		mockFields();
		cut.adjustTime("5");
		Assert.assertEquals(1200000, cut.getRemainingDuration());
	}

	@Test
	public void adjustTimeAllowsPlus() throws Exception {
		mockFields();
		cut.adjustTime("+1");
		Assert.assertEquals(960000, cut.getRemainingDuration());
	}

	@Test
	public void adjustTimeAllowsMinus() throws Exception {
		mockFields();
		cut.setRemainingDuration(cut.getRemainingDuration()-1);
		cut.adjustTime("-1");
		Assert.assertEquals(839999, cut.getRemainingDuration());
	}

	@Test
	public void adjustTimeLargeScaleSetsSecondsToZero() throws Exception {
		mockFields();
		cut.setRemainingDuration(cut.getRemainingDuration()-1);
		cut.adjustTime("15");
		Assert.assertEquals(1800000, cut.getRemainingDuration());
	}

	private void mockFields() throws IllegalAccessException {
		Field[] declaredFields = cut.getClass().getDeclaredFields();
		Field ct = null;
		Field sui = null;
		for (Field field : declaredFields) {
			if (field.getName().equals("countdownTask")) {
				field.setAccessible(true);
				ct = field;
			}
			if (field.getName().equals("swingUI")) {
				field.setAccessible(true);
				sui = field;
			}
		}
		CountdownTask countdownTask = Mockito.mock(CountdownTask.class);
		ct.set(cut, countdownTask);
		sui.set(cut, Mockito.mock(SwingUI.class));
	}
}
