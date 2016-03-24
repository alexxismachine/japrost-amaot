package de.japrost.amaot;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

/**
 * Test the {@link CountdownTask}.
 */
public class CountdownTaskTest {
	private CountdownTask cut;
	private Amaot amaot;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		amaot = Mockito.mock(Amaot.class);
		cut = new CountdownTask(amaot, 10);
	}

	/**
	 * If there is no remaining duration, the state is not changed.
	 */
	@Test
	public void doNothingIfNoTimeRemaining() {
		BDDMockito.given(amaot.getRemainingDuration()).willReturn(0l);
		cut.doInBackground();
		then(amaot).should(times(0)).setState(BDDMockito.any());
	}

	/**
	 * If initial remaining time is too short display READY.
	 */
	@Test
	public void showReadyIfTimeToShort() {
		BDDMockito.given(amaot.getRemainingDuration()).willReturn(10L, 0L);
		cut.doInBackground();
		then(amaot).should(times(1)).setState(AmaotState.GET_READY);
	}

	/**
	 * If display flacker if time is to short more than once.
	 */
	@Test
	public void showFlacker() {
		BDDMockito.given(amaot.getRemainingDuration()).willReturn(1L, 1L, 1L, 1L, 0L);
		cut.doInBackground();
		then(amaot).should(times(1)).setState(AmaotState.GET_READY);
		then(amaot).should(times(1)).setState(AmaotState.HIGHT_TIME);
	}

	/**
	 * Display on time if time is far ahead.
	 */
	@Test
	public void showOnTime() {
		BDDMockito.given(amaot.getRemainingDuration()).willReturn(1L, 60 * 5001L, 1L, 1L, 0L);
		cut.doInBackground();
		then(amaot).should(times(1)).setState(AmaotState.GET_READY);
		then(amaot).should(times(1)).setState(AmaotState.ON_TIME);
	}

	/**
	 * Display get ready if time gets short.
	 */
	@Test
	public void showGetReady() {
		BDDMockito.given(amaot.getRemainingDuration()).willReturn(1L, 60 * 1001L, 1L, 1L, 0L);
		cut.doInBackground();
		then(amaot).should(times(2)).setState(AmaotState.GET_READY);
	}
	/**
	 * do not set state if canceled.
	 */
	@Test
	public void canceled() {
		BDDMockito.given(amaot.getRemainingDuration()).willReturn(1L, 60 * 1001L, 1L, 1L, 0L);
		cut.cancel(true);
		cut.doInBackground();
		then(amaot).should(times(0)).setState(AmaotState.GET_READY);
	}
}
