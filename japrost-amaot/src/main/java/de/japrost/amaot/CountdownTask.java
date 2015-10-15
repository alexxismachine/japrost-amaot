package de.japrost.amaot;

import static de.japrost.amaot.AmaotState.GET_READY;
import static de.japrost.amaot.AmaotState.HIGHT_TIME;
import static de.japrost.amaot.AmaotState.ON_TIME;

import javax.swing.SwingWorker;

public class CountdownTask extends SwingWorker<Void, String> {
	/**
	 * FIXME use interface
	 */
	private final Amaot amaot;
	private long until ; 
	private long pause = 500;
	private long warningOrange = 5 * 60 * 1000;
	private long warningRed = 1 * 60 * 1000;

	/**
	 * @param amaot the amaot
	 * @param remainingDuration the remaining duration 
	 */
	public CountdownTask(Amaot amaot, long remainingDuration) {
		this.amaot = amaot;
		until =System.currentTimeMillis() + this.amaot.remainingDuration;
	}


	@Override
	protected Void doInBackground() {
		boolean flackeringState = true;
		while (!isCancelled() && this.amaot.remainingDuration > 0) {
			this.amaot.remainingDuration = until - System.currentTimeMillis();
			if (this.amaot.remainingDuration > warningOrange) {
				this.amaot.setState(ON_TIME);
			} else if (this.amaot.remainingDuration > warningRed) {
				this.amaot.setState(GET_READY);
			} else {
				flackeringState = !(flackeringState);
				if (flackeringState) {
					this.amaot.setState(HIGHT_TIME);
				} else {
					this.amaot.setState(GET_READY);
				}
			}
			this.amaot.redraw();
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				// nothing to do here
			}
		}
		if (this.amaot.remainingDuration <= 0) {
			amaot.finished();
		}
		return null;
	}
}