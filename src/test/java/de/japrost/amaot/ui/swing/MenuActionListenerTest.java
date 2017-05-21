package de.japrost.amaot.ui.swing;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import de.japrost.amaot.Amaot;

/**
 * Test the {@link MenuActionListener}.
 */
public class MenuActionListenerTest {
	private MenuActionListener cut;
	private Amaot amaot;
	private SwingUI swingUI;
	private JPopupMenu popupMenu;
	private JFrame aboutFrame;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		amaot = Mockito.mock(Amaot.class);
		swingUI = Mockito.mock(SwingUI.class);
		popupMenu = Mockito.mock(JPopupMenu.class);
		aboutFrame = Mockito.mock(JFrame.class);
		BDDMockito.given(swingUI.getPopup()).willReturn(popupMenu);
		BDDMockito.given(swingUI.getAboutFrame()).willReturn(aboutFrame);
		cut = new MenuActionListener(amaot, swingUI);
	}

	/**
	 * If action is not found, the menu is closed.
	 */
	@Test
	public void fallThruClosesMenu() {
		// given
		JMenuItem menuItem = new JMenuItem("Peter");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * close menu on close the menu is closed.
	 */
	@Test
	public void closeMenu() {
		// given
		JMenuItem menuItem = new JMenuItem("Close");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * Start amaot on start and close menu.
	 */
	@Test
	public void startStarts() {
		// given
		JMenuItem menuItem = new JMenuItem("Start");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(amaot).should().startCountdown();
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * Reset amaot on Reset and close menu.
	 */
	@Test
	public void resetResets() {
		// given
		JMenuItem menuItem = new JMenuItem("Reset");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(amaot).should().resetCountdown();
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * Stop amaot on Stop and close menu.
	 */
	@Test
	public void stopStops() {
		// given
		JMenuItem menuItem = new JMenuItem("Stop");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(amaot).should().stopCountdown();
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * adjustTime up and keep menu open.
	 */
	@Test
	public void adjustTimeUp() {
		// given
		JMenuItem menuItem = new JMenuItem("+4");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(amaot).should().adjustTime("+4");
		BDDMockito.then(popupMenu).should().setVisible(true);
	}

	/**
	 * adjustTime down and keep menu open.
	 */
	@Test
	public void adjustTimeDown() {
		// given
		JMenuItem menuItem = new JMenuItem("-7");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(amaot).should().adjustTime("-7");
		BDDMockito.then(popupMenu).should().setVisible(true);
	}

	/**
	 * toggle icon type and close menu.
	 */
	@Test
	public void toggleItem() {
		// given
		JMenuItem menuItem = new JMenuItem("Icon");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(swingUI).should().switchImage();
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * switch font size and close menu.
	 */
	@Test
	public void switchFontSize() {
		// given
		JMenuItem menuItem = new JMenuItem("Size");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(swingUI).should().adjustFontSize();
		BDDMockito.then(popupMenu).should().setVisible(true);
	}

	/**
	 * show about frame and close menu.
	 */
	@Test
	public void showAboutFrame() {
		// given
		JMenuItem menuItem = new JMenuItem("About");
		menuItem.setLocation(0, 0);
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(aboutFrame).should().setVisible(true);
		BDDMockito.then(popupMenu).should().setVisible(false);
	}

	/**
	 * show about frame and close menu.
	 */
	@Test
	public void exit() {
		// given
		JMenuItem menuItem = new JMenuItem("Exit");
		// when
		cut.actionPerformed(new ActionEvent(menuItem, 0, "otto"));
		// then
		BDDMockito.then(amaot).should().exit();
		BDDMockito.then(popupMenu).should().setVisible(false);
	}
}
