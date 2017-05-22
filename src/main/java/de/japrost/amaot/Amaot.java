package de.japrost.amaot;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingWorker;

/**
 * a microchronometer always on top
 */
public class Amaot implements ActionListener {
	private JWindow mainWindow;
	private JLabel mainLabel;
	private JPopupMenu popup;
	private JFrame aboutFrame;
	private CountdownTask countdownTask = new CountdownTask();
	private long duration = 15 * 60 * 1000;
	private long initialDuration = duration;
	private boolean started = false;
	private boolean autoStart = false;
	private int fontSizeList[] = { 16, 24, 36, 12 };
	private int fontSizeIndex = 1;
	private String timeFormat = "%01d min %02d sec";
	private TrayIcon trayIcon;
	private String imageIconName = "/Sanduhr.png";
	private boolean showImage = true;

	public static void main(String[] args) {
		final Amaot amaot = new Amaot();
		if (args.length > 0) {
			amaot.duration = (long) (Float.parseFloat(args[0]) * 1000 * 60);
			amaot.initialDuration = amaot.duration;
			amaot.autoStart = true;
		}
		if (args.length > 1) {
			amaot.timeFormat = "%01d : %02d ";
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				amaot.createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		mainWindow = new JWindow();
		mainWindow.setAlwaysOnTop(true);
		mainLabel = new JLabel(" AMAOT ");
		mainLabel.setFont(new Font("sansserif", Font.BOLD, fontSizeList[fontSizeIndex]));
		mainWindow.getContentPane().add(mainLabel);
		popup = new JPopupMenu();
		JMenuItem menuItem;
		String[] menuEntries = { "Start", "Stop", "Reset", "+10", "+5", "+1", "-1", "-5", "-10", "Size", "Icon",
				"About", "Close Menu", "Exit" };
		for (String menuEntry : menuEntries) {
			menuItem = new JMenuItem(menuEntry);
			menuItem.addActionListener(this);
			popup.add(menuItem);
		}
		MouseAdapter popupListener = new PopupListener();
		mainWindow.addMouseListener(popupListener);
		mainWindow.addMouseMotionListener(popupListener);
		if (SystemTray.isSupported()) {
			createSystemTray();
		}
		createAboutFrame();
		showTimeOnDisplay(duration);
		changeColor(Color.WHITE);
		mainWindow.pack();
		mainWindow.setVisible(true);
		if (autoStart) {
			started = true;
			(countdownTask = new CountdownTask()).execute();
		}
	}

	private void createSystemTray() {
		SystemTray tray = SystemTray.getSystemTray();
		Dimension trayIconSize = tray.getTrayIconSize();
		Image myImage;
		URL imageURL = Amaot.class.getResource(imageIconName);
		if (showImage && imageURL != null) {
			myImage = new ImageIcon(imageURL).getImage();
		} else {
			myImage = new BufferedImage(trayIconSize.width, trayIconSize.height, BufferedImage.TYPE_BYTE_INDEXED);
		}
		trayIcon = new TrayIcon(myImage,
				" Sinnspruch des Tages:\n Dir bleibt stets immer etwas Zeit\n für Datenschutz und Sicherheit! ");
		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new SystemTrayMouseAdapter());
		try {
			tray.add(trayIcon);
		} catch (final AWTException e) {
			// FIXME: use JFrame if not supported
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

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		String eventText = source.getText();
		boolean keepMenuVisible = false;
		if ("Exit".equals(eventText)) {
			System.exit(0);
		} else if ("About".equals(eventText)) {
			// FIXME: Locate at mouse position
			aboutFrame.setLocation(source.getX(), source.getY());
			aboutFrame.setVisible(true);
		//} else if ("Close Menu".equals(eventText)) {
		//	// nothing to do here
		} else if ("Start".equals(eventText)) {
			startCountdown();
		} else if ("Reset".equals(eventText)) {
			resetCountdown();
		} else if ("Icon".equals(eventText)) {
			showImage = !showImage;
			changeColor(mainWindow.getContentPane().getBackground());
		} else if ("Stop".equals(eventText)) {
			stopCountdown();
		} else if ("Size".equals(eventText)) {
			adjustFontSize();
			keepMenuVisible = true;
		} else if (eventText.startsWith("+") || eventText.startsWith("-")) {
			adjustTime(eventText);
			keepMenuVisible = true;
		}
		popup.setVisible(keepMenuVisible);
	}

	private void startCountdown() {
		if (!started) {
			started = true;
			(countdownTask = new CountdownTask()).execute();
		}
	}

	private void resetCountdown() {
		stopCountdown();
		duration = initialDuration;
		showTimeOnDisplay(duration);
	}

	private void stopCountdown() {
		countdownTask.cancel(true);
		changeColor(Color.WHITE);
		started = false;
	}

	private void adjustFontSize() {
		fontSizeIndex = fontSizeIndex + 1;
		if (fontSizeIndex >= fontSizeList.length) {
			fontSizeIndex = 0;
		}
		mainLabel.setFont(new Font("sansserif", Font.BOLD, fontSizeList[fontSizeIndex]));
		mainWindow.pack();
	}

	private void adjustTime(String minutes) {
		if (minutes.startsWith("+")) {
			minutes = minutes.substring(1);
		}
		int addMinutes = Integer.parseInt(minutes);
		long newDuration = duration + addMinutes * 60 * 1000;
		if (newDuration > 1000) {
			if (addMinutes < 10) {
				duration = newDuration;
			} else {
				duration = zeroSeconds(newDuration);
			}
		}
		countdownTask.cancel(true);
		showTimeOnDisplay(duration);
		if (started) {
			(countdownTask = new CountdownTask()).execute();
		} else {
			changeColor(Color.WHITE);
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

	private void changeColor(Color color) {
		mainWindow.getContentPane().setBackground(color);
		URL imageURL = Amaot.class.getResource(imageIconName);
		if (showImage && imageURL != null) {
			Image myImage = new ImageIcon(Amaot.class.getResource(imageIconName)).getImage();
			trayIcon.setImage(myImage);
		} else {
			Dimension trayIconSize = trayIcon.getSize();
			BufferedImage myImage = new BufferedImage(trayIconSize.width, trayIconSize.height,
					BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D graphics2d = myImage.createGraphics();
			graphics2d.setColor(color);
			graphics2d.fillRect(0, 0, myImage.getWidth(), myImage.getHeight());
			trayIcon.setImage(myImage);
		}
	}

	private void showTimeOnDisplay(long t) {
		long min = t / (60 * 1000);
		long sec = (t - (min * 60 * 1000)) / 1000;
		mainLabel.setText(" " + String.format(timeFormat, min, sec) + " ");
		mainWindow.pack();
	}

	class PopupListener extends MouseAdapter {
		private boolean drag = false;

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
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	class SystemTrayMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.setLocation(e.getX(), e.getY());
				popup.setInvoker(popup);
				popup.setVisible(true);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.setLocation(e.getX(), e.getY());
				popup.setInvoker(popup);
				popup.setVisible(true);
			}
		}
	}

	class CountdownTask extends SwingWorker<Void, String> {
		private long until = System.currentTimeMillis() + duration;
		private long pause = 500;
		private long warningOrange = 5 * 60 * 1000;
		private long warningRed = 1 * 60 * 1000;

		@Override
		protected Void doInBackground() {
			boolean flackeringState = true;
			while (!isCancelled() && duration > 0) {
				duration = until - System.currentTimeMillis();
				if (duration > warningOrange) {
					changeColor(Color.GREEN);
				} else if (duration > warningRed) {
					changeColor(Color.ORANGE);
				} else {
					flackeringState = !(flackeringState);
					if (flackeringState) {
						changeColor(Color.RED);
					} else {
						changeColor(Color.ORANGE);
					}
				}
				showTimeOnDisplay(duration);
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					// nothing to do here
				}
			}
			if (duration <= 0) {
				mainLabel.setText(" You're Done! ");
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// nothing to do here
				}
				mainWindow.pack();
				duration = 0;
				started = false;
			}
			return null;
		}
	}
}