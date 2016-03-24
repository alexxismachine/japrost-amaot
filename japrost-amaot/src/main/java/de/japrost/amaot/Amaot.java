package de.japrost.amaot;

import static de.japrost.amaot.AmaotState.HIGHT_TIME;
import static de.japrost.amaot.AmaotState.STOPPED;

import de.japrost.amaot.ui.swing.SwingUI;

/**
 * a microchronometer always on top
 */
public class Amaot {
	private CountdownTask countdownTask;
	private long remainingDuration = 15 * 60 * 1000;
	private long initialDuration = remainingDuration;
	private long adjustedDuration = remainingDuration;
	boolean started = false;
	private boolean autoStart = false;
	private AmaotState currentAmaotState;
	private SwingUI swingUI = new SwingUI(this);

	public Amaot() {
	}

	public static void main(String[] args) {
		final Amaot amaot = new Amaot();
		if (args.length > 0) {
			amaot.remainingDuration = (long) (Float.parseFloat(args[0]) * 1000 * 60);
			amaot.initialDuration = amaot.remainingDuration;
			amaot.adjustedDuration = amaot.remainingDuration;
			amaot.setAutoStart(true);
		}
		if (args.length > 1) {
			amaot.swingUI.setTimeFormat("%01d : %02d ");
		}
		amaot.countdownTask = new CountdownTask(amaot, amaot.remainingDuration);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				amaot.swingUI.createAndShowGUI(amaot);
			}
		});
	}

	public void startCountdown() {
		if (!started) {
			started = true;
			(countdownTask = new CountdownTask(this, remainingDuration)).execute();
		}
	}

	public void resetCountdown() {
		stopCountdown();
		remainingDuration = initialDuration;
		swingUI.showTimeOnDisplay(remainingDuration);
	}

	public void stopCountdown() {
		countdownTask.cancel(true);
		setState(STOPPED);
		started = false;
	}

	public void adjustTime(String minutes) {
		if (minutes.startsWith("+")) {
			minutes = minutes.substring(1);
		}
		int addMinutes = Integer.parseInt(minutes);
		long newRemainingDuration = remainingDuration + addMinutes * 60 * 1000;
		if (newRemainingDuration > 1000) {
			if (addMinutes < 10) {
				remainingDuration = newRemainingDuration;
			} else {
				remainingDuration = zeroSeconds(newRemainingDuration);
			}
		}
		long newAdjustedDuration = adjustedDuration + addMinutes * 60 * 1000;
		if (newAdjustedDuration > 1000) {
			adjustedDuration = newAdjustedDuration;
		}
		countdownTask.cancel(true);
		swingUI.showTimeOnDisplay(remainingDuration);
		if (started) {
			(countdownTask = new CountdownTask(this, remainingDuration)).execute();
		} else {
			setState(STOPPED);
		}
	}

	private long zeroSeconds(long millis) {
		long min = millis / (60 * 1000);
		long sec = (millis - (min * 60 * 1000)) / 1000;
		if ((sec >= 29)) { // noch nicht zufriedenstellend...
			min = min + 1;
		}
		return (min * 60 * 1000);
	}

	public void setState(AmaotState state) {
		boolean stateChange = currentAmaotState != state;
		currentAmaotState = state;
		swingUI.handleStateChange(this, state, stateChange);
	}

	public AmaotState getState() {
		return currentAmaotState;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	private void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	public long getAdjustedDuration() {
		return adjustedDuration;
	}

	public long getRemainingDuration() {
		return remainingDuration;
	}

	public void setRemainingDuration(long remainingDuration) {
		this.remainingDuration = remainingDuration;
	}

	void redraw() {
		// sollte irgendwie ein neutrales event sein
		swingUI.showTimeOnDisplay(remainingDuration);
	}

	void finished() {
		setState(HIGHT_TIME);
		swingUI.setText(" You're Done! ");
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// nothing to do here
		}
		swingUI.pack();
		remainingDuration = 0;
		started = false;

	}

	public void exit() {
		System.exit(0);
	}
}