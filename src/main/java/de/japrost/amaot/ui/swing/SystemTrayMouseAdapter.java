package de.japrost.amaot.ui.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 * {@link MouseListener} for bringing up the menu for SystemTray.
 *
 */
// TODO rename and merge with PopupListener
class SystemTrayMouseAdapter implements  MouseListener {
	private final SwingUI swingUI;

	/**
	 * Initialize
	 * 
	 * @param swingUI the UI the listener is for.
	 */
	public SystemTrayMouseAdapter(SwingUI swingUI) {
		this.swingUI = swingUI;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		popUpIfPopupTrigger(e);
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		popUpIfPopupTrigger(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// nothing to do	
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// nothing to do
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// nothing to do		
	}
	
	private void popUpIfPopupTrigger(MouseEvent e) {
		if (e.isPopupTrigger()) {
			swingUI.popUpMenu(e.getX(), e.getY());
		}
	}

}