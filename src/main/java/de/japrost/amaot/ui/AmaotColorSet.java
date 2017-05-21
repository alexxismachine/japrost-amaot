package de.japrost.amaot.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * Colors used for the states.
 * FIXME Move to UI
 *
 */
public class AmaotColorSet {
	private Map<String, Color> colorMap = new HashMap<String, Color>();

	public void putColor(String purpose, Color color) {
		colorMap.put(purpose, color);
	}

	public Color getColor(String pupose) {
		return colorMap.get(pupose);
	}
}