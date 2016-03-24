package de.japrost.amaot.ui.swing;

import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

/**
 * Test the {@link SystemTrayMouseAdapter}.
 */
public class SystemTrayMouseAdapterTest {
	private SystemTrayMouseAdapter cut;
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
		cut = new SystemTrayMouseAdapter(swingUI);

	}

	/**
	 * Test that the pop up is shown if popup trigger is set.
	 */
	@Test
	public void showPopupOnMousePressed() {
		JFrame jFrame = new JFrame("");
		MouseEvent e = new MouseEvent(jFrame, 0, 0, 0, 0, 0, 0, true);
		cut.mousePressed(e);
		BDDMockito.then(swingUI).should().popUpMenu(0, 0);

	}

	/**
	 * Test that the pop up is not shown if popup trigger is not set.
	 */
	@Test
	public void noPopupOnMousePressed() {
		JFrame jFrame = new JFrame("");
		MouseEvent e = new MouseEvent(jFrame, 0, 0, 0, 0, 0, 0, false);
		cut.mousePressed(e);
		BDDMockito.then(swingUI).should(BDDMockito.never()).popUpMenu(0, 0);

	}
}
