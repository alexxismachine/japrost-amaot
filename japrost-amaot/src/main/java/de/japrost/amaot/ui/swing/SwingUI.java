package de.japrost.amaot.ui.swing;

import static de.japrost.amaot.AmaotState.GET_READY;
import static de.japrost.amaot.AmaotState.HIGHT_TIME;
import static de.japrost.amaot.AmaotState.ON_TIME;
import static de.japrost.amaot.AmaotState.STOPPED;
import static java.awt.image.BufferedImage.TYPE_BYTE_INDEXED;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JWindow;

import de.japrost.amaot.Amaot;
import de.japrost.amaot.AmaotState;
import de.japrost.amaot.ui.AmaotColorSet;

/**
 * Main component for Swing
 **/
public class SwingUI {
	public static final String NEUTRAL_BACKGROUND_COLOR = "neutralBackground";

	private final MenuActionListener menuActionListener;
	private final Amaot amaot;
	private int fontSizeList[] = { 16, 24, 36, 12 };
	private int fontSizeIndex = 1;
	private String timeFormat = "%01d min %02d sec";
	private JWindow mainWindow;
	private JLabel mainLabel;
	AmaotColorSet amaotColorSet = new AmaotColorSet();

	int colorHeightElapsed = -1;

	public JFrame aboutFrame;
	public TrayIcon trayIcon;

	public JPopupMenu popup;
	public boolean showImage = true;
	private String imageIconName = "/Sanduhr.png";

	public SwingUI(Amaot amaot) {
		super();
		this.amaot = amaot;
		menuActionListener = new MenuActionListener(amaot, this);
		amaotColorSet.putColor(STOPPED.toString(), Color.WHITE);
		amaotColorSet.putColor(ON_TIME.toString(), Color.GREEN);
		amaotColorSet.putColor(GET_READY.toString(), Color.ORANGE);
		amaotColorSet.putColor(HIGHT_TIME.toString(), Color.RED);
		amaotColorSet.putColor(NEUTRAL_BACKGROUND_COLOR, Color.BLACK);

	}
	
	public void createSystemTray() {
		SystemTray tray = SystemTray.getSystemTray();
		Dimension trayIconSize = tray.getTrayIconSize();
		Image myImage;
		URL imageURL = Amaot.class.getResource(imageIconName);
		if (showImage && imageURL != null) {
			myImage = new ImageIcon(imageURL).getImage();
		} else {
			showImage = false;
			myImage = new BufferedImage(trayIconSize.width, trayIconSize.height, BufferedImage.TYPE_BYTE_INDEXED);
		}
		trayIcon = new TrayIcon(myImage,
				" Sinnspruch des Tages:\n Dir bleibt stets immer etwas Zeit\n für Datenschutz und Sicherheit! ");
		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new SystemTrayMouseAdapter(this));
		try {
			tray.add(trayIcon);
		} catch (final AWTException e) {
			// FIXME: use JFrame if not supported
		}
	}

	
	public void switchImage() {
		showImage = !showImage;
		URL imageURL = Amaot.class.getResource(imageIconName);
		if (showImage && imageURL != null) {
			Image myImage = new ImageIcon(Amaot.class.getResource(imageIconName)).getImage();
			trayIcon.setImage(myImage);
		} else {
			amaot.setState(amaot.getState());
		}
	}

	private void createAboutFrame() {
		aboutFrame = new JFrame("About: AMAOT");
		JTextArea aboutText = new JTextArea(4, 30);
		aboutText.append("AMAOT: A Michrochronometer Always On Top \n\nThis is just a small countdown timer. ");
		aboutText.append("Use it if you like.\n But be aware of ");
		aboutText.append("the licensing conditions:\n If you use ");
		aboutText.append("it more than once a day, you have to ");
		aboutText.append("consider \n data protection and ");
		aboutText.append("IT-security for some seconds!");
		aboutText.setEditable(false);
		aboutFrame.add(aboutText);
		aboutFrame.pack();
	}


	public void createAndShowGUI(Amaot amaot) {
		mainWindow = new JWindow();
		// FIXME assure more often that AMAOT is on top
		mainWindow.setAlwaysOnTop(true);
		mainLabel = new JLabel(" AMAOT ");
		mainLabel.setFont(new Font("sansserif", Font.BOLD,
				fontSizeList[fontSizeIndex]));
		mainWindow.getContentPane().add(mainLabel);
		popup = new JPopupMenu();
		JMenuItem menuItem;
		String[] menuEntries = { "Start", "Stop", "Reset", "+10", "+5", "+1",
				"-1", "-5", "-10", "Size", "Icon", "About", "Close Menu",
				"Exit" };
		for (String menuEntry : menuEntries) {
			menuItem = new JMenuItem(menuEntry);
			menuItem.addActionListener(menuActionListener);
			popup.add(menuItem);
		}
		MouseAdapter popupListener = new PopupListener(this);
		mainWindow.addMouseListener(popupListener);
		mainWindow.addMouseMotionListener(popupListener);
		if (SystemTray.isSupported()) {
			createSystemTray();
		}
		createAboutFrame();
		showTimeOnDisplay(amaot.getRemainingDuration());
		amaot.setState(STOPPED);
		mainWindow.pack();
		mainWindow.setVisible(true);
		if (amaot.isAutoStart()) {
			amaot.startCountdown();
		}
	}

	void adjustFontSize() {
		fontSizeIndex = fontSizeIndex + 1;
		if (fontSizeIndex >= fontSizeList.length) {
			fontSizeIndex = 0;
		}
		mainLabel.setFont(new Font("sansserif", Font.BOLD,
				fontSizeList[fontSizeIndex]));
		mainWindow.pack();
	}

	public void showTimeOnDisplay(long t) {
		long min = t / (60 * 1000);
		long sec = (t - (min * 60 * 1000)) / 1000;
		mainLabel.setText(" " + String.format(timeFormat, min, sec) + " ");
		mainWindow.pack();
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public void handleStateChange(Amaot amaot, AmaotState state,
			boolean stateChange) {
		if (stateChange) {
			mainWindow.getContentPane().setBackground(
					amaotColorSet.getColor(state.toString()));
		}
		if (!showImage) {
			Dimension trayIconSize = trayIcon.getSize();
			long remainingPercent = amaot.getRemainingDuration() * 100
					/ amaot.getAdjustedDuration();
			int newColorHeightElapsed = (int) (trayIconSize.height
					* (100 - remainingPercent) / 100);
			if (stateChange
					|| colorHeightElapsed != newColorHeightElapsed) {
				colorHeightElapsed = newColorHeightElapsed;
				BufferedImage myImage = new BufferedImage(trayIconSize.width,
						trayIconSize.height, TYPE_BYTE_INDEXED);
				Graphics2D graphics2d = myImage.createGraphics();
				// upper part is neutral while still on time
				if (amaot.getState() == ON_TIME) {
					graphics2d.setColor(amaotColorSet
							.getColor(NEUTRAL_BACKGROUND_COLOR));
				} else {
					graphics2d.setColor(amaotColorSet.getColor(state
							.toString()));
				}
				graphics2d.fillRect(0, 0, myImage.getWidth(),
						colorHeightElapsed);
				graphics2d.setColor(amaotColorSet.getColor(ON_TIME
						.toString()));
				graphics2d.fillRect(0, colorHeightElapsed,
						myImage.getWidth(), myImage.getHeight());
				trayIcon.setImage(myImage);
			}
		} else {
			colorHeightElapsed = -1;
		}
	}

	public void pack() {
		mainWindow.pack();

	}

	public void setText(String string) {
		mainLabel.setText(string);
	}

	void popUpMenu(int x, int y) {
		popup.setLocation(x, y);
		popup.setInvoker(popup);
		popup.setVisible(true);
	}
}
