package de.japrost.amaot.ui.swing;

import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

/**
 * Test the {@link PopupListener}.
 */
public class PopupListenerTest {
	private PopupListener cut;
	private SwingUI swingUI;
	private JPopupMenu popupMenu;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		swingUI = Mockito.mock(SwingUI.class);
		popupMenu = Mockito.mock(JPopupMenu.class);
		BDDMockito.given(swingUI.getPopup()).willReturn(popupMenu);
		cut = new PopupListener(swingUI);

	}

	/**
	 * Test that dragging is on if button is down.
	 */
	@Test
	public void mouseDragged() {
		JFrame jFrame = new JFrame("");
		MouseEvent e = new MouseEvent(jFrame, 0, 0, 16, 10, 10, 0, false);
		cut.mousePressed(e);
		cut.mouseDragged(e);
		Assert.assertEquals(10, jFrame.getY());
	}

	/**
	 * Test that dragging is off if button is not down.
	 */
	@Test
	public void mouseNotDragged() {
		JFrame jFrame = new JFrame("");
		MouseEvent e = new MouseEvent(jFrame, 0, 0, 0, 10, 10, 0, false);
		cut.mousePressed(e);
		cut.mouseDragged(e);
		Assert.assertEquals(0, jFrame.getY());
	}

	/**
	 * Test that the pop up is shown if popup trigger is set.
	 */
	@Test
	public void showPopupOnMousePressed() {
		JFrame jFrame = new JFrame("");
		MouseEvent e = new MouseEvent(jFrame, 0, 0, 0, 0, 0, 0, true);
		cut.mousePressed(e);
		BDDMockito.then(popupMenu).should().show(jFrame, 0, 0);

	}
}
