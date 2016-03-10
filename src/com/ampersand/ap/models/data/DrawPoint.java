package com.ampersand.ap.models.data;

import java.awt.Color;
import java.io.Serializable;

import com.ampersand.lcu.gui.color.ColorPalette;

public class DrawPoint implements Serializable {

	/*
	 * Attributes:
	 */
	private static final long serialVersionUID = -2430331361281663163L;

	private int m_x;
	private int m_y;
	private int m_size;
	private int m_type;
	private Color m_color;

	public static final int ERASER = 0;

	public static final int FILLED_CIRCLE = 11;
	public static final int FILLED_SQUARE = 12;
	public static final int FILLED_TRIANGLE = 13;

	public static final int CIRCLE = 21;
	public static final int SQUARE = 22;
	public static final int TRIANGLE = 23;

	/*
	 * Methods:
	 */

	// CONSTRUCTOR

	public DrawPoint() {

		m_x = -10;
		m_y = -10;
		m_size = 3;
		m_color = ColorPalette.BLACK;
		m_type = FILLED_CIRCLE;
	}

	public DrawPoint(int x, int y, int size, Color color, int type) {

		m_x = x;
		m_y = y;
		m_size = size;
		m_color = color;
		m_type = type;
	}

	// ACCESSORS and MUTATORS

	public int getX() {

		return m_x;
	}

	public void setX(int x) {

		m_x = x;
	}

	public int getY() {

		return m_y;
	}

	public void setY(int y) {

		m_y = y;
	}

	public int getSize() {

		return m_size;
	}

	public void setSize(int size) {

		m_size = size;
	}

	public Color getColor() {

		return m_color;
	}

	public void setColor(Color color) {

		m_color = color;
	}

	public int getType() {

		return m_type;
	}

	public void setType(int type) {

		m_type = type;
	}
}
