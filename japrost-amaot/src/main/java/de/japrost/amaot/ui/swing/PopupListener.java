package de.japrost.amaot.ui.swing;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.japrost.amaot.Amaot;
/**
 * 
 * FIXME merge with {@link SystemTrayMouseAdapter}.
 *
 */
public class PopupListener extends MouseAdapter {
	private final SwingUI swingUI;

	/**
	 * @param amaot the amaot
	 */
	public PopupListener(SwingUI amaot) {
		this.swingUI = amaot;
	}

	private boolean drag = false;

	/**
	 * {@inheritDoc}<br>
	 * <strong>This impelementation</strong>  sets dragging mode and shows popup if popup is triggered.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			drag = true;
		} else {
			drag = false;
		}
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		drag = false;
		maybeShowPopup(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (drag) {
			Component component = (Component) e.getSource();
			component.setLocation(component.getX() + e.getX(), component.getY() + e.getY());
		}
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.swingUI.getPopup().show(e.getComponent(), e.getX(), e.getY());
		}
	}
}