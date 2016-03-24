package de.japrost.amaot.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import de.japrost.amaot.Amaot;

/**
 * ActionListener for menu actions.
 */
public class MenuActionListener implements ActionListener {
	private final SwingUI swingUI;
	private final Amaot amaot;

	/**
	 * Initailize with needed dependencies.
	 * 
	 * @param amaot
	 *            Amaot
	 * @param swingUI
	 *            Amaot ui
	 */
	public MenuActionListener(Amaot amaot, SwingUI swingUI) {
		super();
		this.swingUI = swingUI;
		this.amaot = amaot;
	}

	/**
	 * {@inheritDoc}<br>
	 * <strong>This implementation</strong> handels all menu entries and delegates them to the according method of amaot or swingUI.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		String eventText = source.getText();
		boolean keepMenuVisible = false;
		if ("Exit".equals(eventText)) {
			amaot.exit();
		} else if ("About".equals(eventText)) {
			// FIXME: Locate at mouse position
			swingUI.getAboutFrame().setLocation(source.getX(), source.getY());
			swingUI.getAboutFrame().setVisible(true);
			// } else if ("Close Menu".equals(eventText)) {
			// // nothing to do here
		} else if ("Start".equals(eventText)) {
			amaot.startCountdown();
		} else if ("Reset".equals(eventText)) {
			amaot.resetCountdown();
		} else if ("Icon".equals(eventText)) {
			swingUI.switchImage();
		} else if ("Stop".equals(eventText)) {
			amaot.stopCountdown();
		} else if ("Size".equals(eventText)) {
			swingUI.adjustFontSize();
			keepMenuVisible = true;
		} else if (eventText.startsWith("+") || eventText.startsWith("-")) {
			amaot.adjustTime(eventText);
			keepMenuVisible = true;
		}
		swingUI.getPopup().setVisible(keepMenuVisible);
	}

}
