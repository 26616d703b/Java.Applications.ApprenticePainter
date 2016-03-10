package com.ampersand.ap.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.ampersand.ap.models.data.DrawPoint;
import com.ampersand.ap.observers.StateObservable;
import com.ampersand.ap.observers.StateObserver;
import com.ampersand.lcu.gui.color.ColorPalette;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, Printable, StateObservable {

	/*
	 * Attributes:
	 */
	private static final long serialVersionUID = 2230189071814985334L;

	public static final int HAND_TOOL = 0;
	public static final int PENCIL_TOOL = 1;

	private StateObserver m_observer;

	private int m_background_image_x;
	private int m_background_image_y;
	private BufferedImage m_background_image;

	private int m_point_size;
	private int m_point_type;
	private Color m_point_color;
	private int m_tool_type;

	private ArrayList<DrawPoint> m_points_list;

	/*
	 * Methods:
	 */

	// CONSTRUCTOR

	public DrawPanel() {

		m_point_size = 3;
		m_point_color = ColorPalette.BLACK;
		m_point_type = DrawPoint.FILLED_CIRCLE;

		m_tool_type = DrawPanel.PENCIL_TOOL;

		m_background_image = null;
		m_points_list = new ArrayList<DrawPoint>();

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public DrawPanel(int point_size, Color point_color, int point_type) {

		m_point_size = point_size;
		m_point_color = point_color;
		m_point_type = point_type;

		m_background_image = null;
		m_points_list = new ArrayList<DrawPoint>();

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// ACCESSORS and MUTATORS

	// POINT SIZE

	public int getPointSize() {

		return m_point_size;
	}

	public void setPointSize(int size) {

		m_point_size = size;
	}

	// POINT TYPE

	public int getPointType() {

		return m_point_type;
	}

	public void setPointType(int type) {

		m_point_type = type;
	}

	// POINT COLOR

	public Color getPointColor() {

		return m_point_color;
	}

	public void setPointColor(Color color) {

		m_point_color = color;
	}

	// TOOL TYPE

	public int getToolType() {

		return m_tool_type;
	}

	public void setToolType(int type) {

		m_tool_type = type;
	}

	// POINTS LIST

	public ArrayList<DrawPoint> getPointsList() {

		return m_points_list;
	}

	public void setPointsList(ArrayList<DrawPoint> points_list) {

		m_points_list = points_list;

		repaint();
	}

	// BACKGROUND IMAGE

	public BufferedImage getBackgroundImage() {

		return m_background_image;
	}

	public void setBackgroundImage(BufferedImage image) {

		m_background_image = image;
	}

	// RE-IMPLEMENTED METHODS

	@Override
	public void paintComponent(Graphics g) {

		g.setColor(ColorPalette.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(m_background_image, m_background_image_x, m_background_image_y, null);

		for (final DrawPoint point : m_points_list) {

			g.setColor(point.getColor());

			switch (point.getType()) {

			case DrawPoint.CIRCLE:

				g.drawOval(point.getX() - m_point_size / 2, point.getY() - m_point_size / 2, point.getSize(),
						point.getSize());

				break;

			case DrawPoint.SQUARE:

				g.drawRect(point.getX() - m_point_size / 2, point.getY() - m_point_size / 2, point.getSize(),
						point.getSize());

				break;

			case DrawPoint.TRIANGLE:

				final int x[] = { point.getX() - m_point_size / 2, point.getX(), point.getX() + m_point_size / 2 };
				final int y[] = { point.getY() + m_point_size / 2, point.getY() - m_point_size / 2,
						point.getY() + m_point_size / 2 };

				g.drawPolygon(x, y, 3);

				break;

			case DrawPoint.FILLED_CIRCLE:

				g.fillOval(point.getX() - m_point_size / 2, point.getY() - m_point_size / 2, point.getSize(),
						point.getSize());

				break;

			case DrawPoint.FILLED_SQUARE:

				g.fillRect(point.getX() - m_point_size / 2, point.getY() - m_point_size / 2, point.getSize(),
						point.getSize());

				break;

			case DrawPoint.FILLED_TRIANGLE:

				final int _x[] = { point.getX() - m_point_size / 2, point.getX(), point.getX() + m_point_size / 2 };
				final int _y[] = { point.getY() + m_point_size / 2, point.getY() - m_point_size / 2,
						point.getY() + m_point_size / 2 };

				g.fillPolygon(_x, _y, 3);

				break;

			default:
				break;
			}
		}
	}

	// MOUSE EVENT

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {

		switch (m_tool_type) {

		case HAND_TOOL:

			break;

		case PENCIL_TOOL:

			m_points_list.add(new DrawPoint(event.getX(), event.getY(), m_point_size, m_point_color, m_point_type));

			notifyObserver(false);
			repaint();

			break;

		default:
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {

		switch (m_tool_type) {

		case HAND_TOOL:

			if (m_background_image != null) {

				if (getCursor().getType() == Cursor.N_RESIZE_CURSOR) {

					/*
					 * if (event.getY() < m_background_image_y) {
					 *
					 * m_background_image =
					 * ImageProcess.resize(m_background_image,
					 * m_background_image.getWidth(),
					 * m_background_image.getHeight() + m_background_image_y -
					 * event.getY(), m_background_image.getType());
					 *
					 * if (event.getY() < 0) {
					 *
					 * m_background_image_y = 0; } else {
					 *
					 * m_background_image_y = event.getY(); } } else if
					 * (event.getY() > m_background_image_y) {
					 *
					 * m_background_image =
					 * ImageProcess.resize(m_background_image,
					 * m_background_image.getWidth(),
					 * m_background_image.getHeight() - event.getY() -
					 * m_background_image_y, m_background_image.getType());
					 *
					 * if (event.getY() < m_background_image_y +
					 * m_background_image.getHeight() - 10) {
					 *
					 * m_background_image_y = event.getY(); } else {
					 *
					 *
					 * } }
					 */
				} else {

					if (event.getX() < m_background_image_x + m_background_image.getWidth()
							&& event.getX() > m_background_image_x
							&& event.getY() < m_background_image_y + m_background_image.getHeight()
							&& event.getY() > m_background_image_y) {

						m_background_image_x = event.getX() - m_background_image.getWidth() / 2;
						m_background_image_y = event.getY() - m_background_image.getHeight() / 2;
					}
				}
			}

			break;

		case PENCIL_TOOL:

			m_points_list.add(new DrawPoint(event.getX(), event.getY(), m_point_size, m_point_color, m_point_type));

			break;

		default:
			break;
		}

		notifyObserver(false);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent event) {

		switch (m_tool_type) {

		case HAND_TOOL:

			if (m_background_image != null) {

				if (event.getY() <= m_background_image_y + 10 && event.getY() >= m_background_image_y - 10) {

					setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
				} else if (event.getY() <= m_background_image_y + m_background_image.getHeight() + 10
						&& event.getY() >= m_background_image_y + m_background_image.getHeight() - 10) {

					setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
				} else if (event.getX() <= m_background_image_x + m_background_image.getWidth() + 10
						&& event.getX() >= m_background_image_x + m_background_image.getWidth() - 10) {

					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				} else if (event.getX() <= m_background_image_x + 10 && event.getX() >= m_background_image_x - 10) {

					setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
				} else {

					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}

			break;

		case PENCIL_TOOL:

			break;

		default:
			break;
		}

		/*
		 * notifyObserver(false); repaint();
		 */
	}

	// PRINTABLE

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

		graphics = getGraphics();

		return Printable.PAGE_EXISTS;
	}

	// OBSERVABLE

	@Override
	public void addObserver(StateObserver observer) {

		m_observer = observer;
	}

	@Override
	public void notifyObserver(boolean saved) {

		m_observer.update(saved);
	}

	@Override
	public void removeObserver() {

		m_observer = null;
	}

	// IMPLEMENTED METHODS

	// ----------------------------------------------------[ C
	// ]----------------------------------------------------//

	public void clear() {

		m_background_image = null;
		m_points_list = new ArrayList<DrawPoint>();

		notifyObserver(false);
		repaint();
	}
}
