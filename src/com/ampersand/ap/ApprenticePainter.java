package com.ampersand.ap;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ampersand.ap.models.data.DrawPoint;
import com.ampersand.ap.observers.StateObserver;
import com.ampersand.ap.panels.DrawPanel;
import com.ampersand.lcu.gui.GUIFactory;
import com.ampersand.lcu.gui.component.button.HighlightButton;
import com.ampersand.lcu.gui.font.FontManager;
import com.ampersand.lcu.io.printing.PrintManager;

public class ApprenticePainter extends JFrame implements StateObserver {

	/*
	 * Attributes:
	 */
	private static final long serialVersionUID = 3250199760239593888L;

	private final ApprenticePainter m_instance;

	private boolean m_current_image_is_recorded;
	private String m_current_image_path;

	private FileListener m_file_listener;
	private EditionListener m_edition_listener;
	private FormatListener m_format_listener;
	private HelpListener m_help_listener;

	// GUI

	private JMenuBar m_menu_bar;

	private JMenu m_file_menu;
	private JMenuItem m_clear;
	private JMenuItem m_open;
	private JMenuItem m_save;
	private JMenuItem m_save_as;
	private JMenuItem m_print;
	private JMenuItem m_exit;

	private JMenu m_edition_menu;
	private JMenuItem m_cancel;

	private JMenu m_format_menu;
	private JMenuItem m_point_size;
	private JMenuItem m_point_type;
	private JMenuItem m_point_color;

	private JMenu m_help_menu;
	private JMenuItem m_about;

	private DrawPanel m_draw_pane;

	private JToolBar m_tool_bar;
	private HighlightButton m_hand_button;
	private HighlightButton m_pencil_button;
	private JComboBox<Object> m_point_size_box;
	private JComboBox<Object> m_point_type_box;
	private HighlightButton m_point_color_button;

	private JFileChooser m_file_chooser;

	/*
	 * Methods
	 */

	// CONSTRUCTOR

	public ApprenticePainter() {

		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final ClassNotFoundException e) {

			e.printStackTrace();
		} catch (final InstantiationException e) {

			e.printStackTrace();
		} catch (final IllegalAccessException e) {

			e.printStackTrace();
		} catch (final UnsupportedLookAndFeelException e) {

			e.printStackTrace();
		}

		m_instance = this;

		m_current_image_is_recorded = true;
		m_current_image_path = new String();

		// Window properties

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent event) {

				exit();
			}
		});

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("res/icons/paint_brush-48.png")).getImage());
		setSize(1024, 600);
		setTitle("ApprenticePainter");
		setLocationRelativeTo(null);

		inisialize();
	}

	// INITIALIZATIONS:

	public void inisialize() {

		initMenu();
		initDrawPane();
		initToolBar();
	}

	// INISIALIZATIONS

	public void initDrawPane() {

		m_draw_pane = new DrawPanel();
		m_draw_pane.addObserver(this);

		getContentPane().add(m_draw_pane, BorderLayout.CENTER);
	}

	public void initMenu() {

		m_file_listener = new FileListener();
		m_edition_listener = new EditionListener();
		m_format_listener = new FormatListener();
		m_help_listener = new HelpListener();

		m_file_chooser = new JFileChooser();
		m_file_chooser.setAcceptAllFileFilterUsed(false);
		m_file_chooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichier .JPG", "jpg"));

		// File

		m_clear = new JMenuItem("Effacer tout", new ImageIcon(getClass().getResource("res/icons/frame-32.png")));
		m_clear.addActionListener(m_file_listener);
		m_clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		m_clear.setMnemonic('c');

		m_open = new JMenuItem("Ouvrir", new ImageIcon(getClass().getResource("res/icons/folder-32.png")));
		m_open.addActionListener(m_file_listener);
		m_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		m_open.setMnemonic('o');

		m_save = new JMenuItem("Enregistrer", new ImageIcon(getClass().getResource("res/icons/save-32.png")));
		m_save.addActionListener(m_file_listener);
		m_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		m_save.setMnemonic('e');

		m_save_as = new JMenuItem("Enregistrer sous",
				new ImageIcon(getClass().getResource("res/icons/save_as-32.png")));
		m_save_as.addActionListener(m_file_listener);
		m_save_as.setMnemonic('s');

		m_print = new JMenuItem("Imprimer", new ImageIcon(getClass().getResource("res/icons/print-32.png")));
		m_print.addActionListener(m_file_listener);
		m_print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		m_print.setMnemonic('i');

		m_exit = new JMenuItem("Quitter", new ImageIcon(getClass().getResource("res/icons/switch_off-32.png")));
		m_exit.addActionListener(m_file_listener);
		m_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		m_exit.setMnemonic('q');

		m_file_menu = new JMenu("Fichier");
		m_file_menu.add(m_clear);
		m_file_menu.add(m_open);
		m_file_menu.add(m_save);
		m_file_menu.add(m_save_as);
		m_file_menu.addSeparator();
		m_file_menu.add(m_print);
		m_file_menu.addSeparator();
		m_file_menu.add(m_exit);
		m_file_menu.setMnemonic('f');

		// Edition

		m_cancel = new JMenuItem("Annuler", new ImageIcon(getClass().getResource("res/icons/back-32.png")));
		m_cancel.addActionListener(m_edition_listener);
		m_cancel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		m_cancel.setMnemonic('a');

		m_edition_menu = new JMenu("Edition");
		m_edition_menu.add(m_cancel);
		m_edition_menu.setMnemonic('e');

		// Format

		// POINT SIZE

		m_point_size = new JMenuItem("Choisir la taille du point",
				new ImageIcon(getClass().getResource("res/icons/font_size-32.png")));
		m_point_size.addActionListener(m_format_listener);
		m_point_size.setMnemonic('t');

		// POINT STYLE

		m_point_type = new JMenuItem("Choisir la forme du point",
				new ImageIcon(getClass().getResource("res/icons/shape-32.png")));
		m_point_type.addActionListener(m_format_listener);
		m_point_type.setMnemonic('f');

		// POINT COLOR

		m_point_color = new JMenuItem("Choisir la couleur du point",
				new ImageIcon(getClass().getResource("res/icons/color-32.png")));
		m_point_color.addActionListener(m_format_listener);
		m_point_color.setMnemonic('c');

		m_format_menu = new JMenu("Format");
		m_format_menu.add(m_point_size);
		m_format_menu.add(m_point_type);
		m_format_menu.add(m_point_color);
		m_format_menu.setMnemonic('o');

		// Help

		m_about = new JMenuItem("À propos", new ImageIcon(getClass().getResource("res/icons/info-32.png")));
		m_about.addActionListener(m_help_listener);
		m_about.setAccelerator(KeyStroke.getKeyStroke("F1"));
		m_about.setMnemonic('p');

		m_help_menu = new JMenu("?");
		m_help_menu.add(m_about);
		m_help_menu.setMnemonic('?');

		// Menu

		m_menu_bar = new JMenuBar();
		m_menu_bar.add(m_file_menu);
		m_menu_bar.add(m_edition_menu);
		m_menu_bar.add(m_format_menu);
		m_menu_bar.add(m_help_menu);

		setJMenuBar(m_menu_bar);
	}

	public void initToolBar() {

		// HAND

		m_hand_button = new HighlightButton(new ImageIcon(getClass().getResource("res/icons/hand_cursor-32.png")));
		m_hand_button.addActionListener(m_format_listener);

		// PENCIL

		m_pencil_button = new HighlightButton(new ImageIcon(getClass().getResource("res/icons/pencil-32.png")));
		m_pencil_button.addActionListener(m_format_listener);
		m_pencil_button.setEnabled(false);

		// POINT TYPE

		final Object[] shapes = { new ImageIcon(getClass().getResource("res/icons/filled_circle-32.png")),
				new ImageIcon(getClass().getResource("res/icons/filled_square-32.png")),
				new ImageIcon(getClass().getResource("res/icons/filled_triangle-32.png")),
				new ImageIcon(getClass().getResource("res/icons/circle-32.png")),
				new ImageIcon(getClass().getResource("res/icons/square-32.png")),
				new ImageIcon(getClass().getResource("res/icons/triangle-32.png")) };

		m_point_type_box = new JComboBox<Object>(shapes);
		m_point_type_box.addItemListener(e -> {

			int point_type = 0;

			if (m_point_type_box.getSelectedItem().toString().endsWith("filled_circle-32.png")) {

				point_type = DrawPoint.FILLED_CIRCLE;
			} else if (m_point_type_box.getSelectedItem().toString().endsWith("filled_square-32.png")) {

				point_type = DrawPoint.FILLED_SQUARE;
			} else if (m_point_type_box.getSelectedItem().toString().endsWith("filled_triangle-32.png")) {

				point_type = DrawPoint.FILLED_TRIANGLE;
			} else if (m_point_type_box.getSelectedItem().toString().endsWith("circle-32.png")) {

				point_type = DrawPoint.CIRCLE;
			} else if (m_point_type_box.getSelectedItem().toString().endsWith("square-32.png")) {

				point_type = DrawPoint.SQUARE;
			} else if (m_point_type_box.getSelectedItem().toString().endsWith("triangle-32.png")) {

				point_type = DrawPoint.TRIANGLE;
			}

			m_draw_pane.setPointType(point_type);
		});

		m_point_type_box.setFocusable(false);
		((JLabel) m_point_type_box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		// -----------------------------------------------------------------------------------------------

		m_point_size_box = new JComboBox<Object>();

		for (int i = 0; i < 100; i++) {

			m_point_size_box.addItem(i + 1);
		}

		m_point_size_box
				.addItemListener(event -> m_draw_pane.setPointSize(Integer.parseInt(event.getItem().toString())));

		m_point_size_box.setFocusable(false);
		m_point_size_box.setFont(FontManager.CENTURY_GOTHIC_BOLD_20);
		m_point_size_box.setSelectedItem(m_draw_pane.getPointSize());
		((JLabel) m_point_size_box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		// -----------------------------------------------------------------------------------------------

		m_point_color_button = new HighlightButton(new ImageIcon(getClass().getResource("res/icons/color-32.png")));
		m_point_color_button.addActionListener(m_format_listener);

		m_tool_bar = new JToolBar();
		m_tool_bar.add(m_hand_button);
		m_tool_bar.add(m_pencil_button);

		m_tool_bar.add(m_point_type_box);
		m_tool_bar.addSeparator();

		m_tool_bar.add(m_point_size_box);
		m_tool_bar.add(m_point_color_button);

		getContentPane().add(m_tool_bar, BorderLayout.SOUTH);
	}

	// LISTENERS

	public class FileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			if (event.getSource().equals(m_clear)) {

				m_draw_pane.clear();
			} else if (event.getSource().equals(m_open)) {

				if (m_draw_pane.getBackgroundImage() == null) {

					if (m_file_chooser.showOpenDialog(m_instance) == JFileChooser.APPROVE_OPTION) {

						final File file = m_file_chooser.getSelectedFile();
						m_current_image_path = file.getAbsolutePath();

						if (m_file_chooser.getFileFilter().accept(file)) {

							BufferedImage img = null;

							try {

								img = ImageIO.read(file);

							} catch (final IOException e1) {

								e1.printStackTrace();
							}

							m_draw_pane.setBackgroundImage(img);
							m_draw_pane.repaint();
						} else {

							JOptionPane.showMessageDialog(m_instance, "L'extension du fichier selectionné est invalide !",
									"Erreur", JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("res/icons/error-48.png")));
						}
					}
				} else {

					final int answer = JOptionPane.showConfirmDialog(m_instance,
							"Cette action causera la perte de l'image en cours, voulez vous continuer quand même ?",
							"Avertissement", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
							new ImageIcon(getClass().getResource("res/icons/help-48.png")));

					switch (answer) {

					case JOptionPane.YES_OPTION:

						if (m_file_chooser.showOpenDialog(m_instance) == JFileChooser.APPROVE_OPTION) {

							final File file = m_file_chooser.getSelectedFile();
							m_current_image_path = file.getAbsolutePath();

							if (m_file_chooser.getFileFilter().accept(file)) {

								BufferedImage img = null;

								try {

									img = ImageIO.read(file);

								} catch (final IOException e1) {

									e1.printStackTrace();
								}

								m_draw_pane.setBackgroundImage(img);
								m_draw_pane.repaint();
							} else {

								JOptionPane.showMessageDialog(m_instance,
										"L'extension du fichier selectionné est invalide !", "Erreur",
										JOptionPane.ERROR_MESSAGE,
										new ImageIcon(getClass().getResource("res/icons/error-48.png")));
							}
						}

						break;
					}
				}

				m_current_image_is_recorded = true;
			} else if (event.getSource().equals(m_save)) {

				if (!m_current_image_path.isEmpty()) {

					write();
				} else {

					if (m_file_chooser.showSaveDialog(m_instance) == JFileChooser.APPROVE_OPTION) {

						final File file = m_file_chooser.getSelectedFile();
						m_current_image_path = file.getAbsolutePath() + ".jpg";

						if (m_file_chooser.getFileFilter().accept(file)) {

							write();
						} else {

							JOptionPane.showMessageDialog(m_instance, "L'extension fournie est invalide !", "Erreur",
									JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("res/icons/error-48.png")));
						}
					}
				}
			} else if (event.getSource().equals(m_save_as)) {

				if (m_file_chooser.showSaveDialog(m_instance) == JFileChooser.APPROVE_OPTION) {

					final File file = m_file_chooser.getSelectedFile();
					m_current_image_path = file.getAbsolutePath() + "jpg";

					if (m_file_chooser.getFileFilter().accept(file)) {

						write();
					} else {

						JOptionPane.showMessageDialog(m_instance, "L'extension fournie est invalide !", "Erreur",
								JOptionPane.ERROR_MESSAGE,
								new ImageIcon(getClass().getResource("res/icons/error-48.png")));
					}
				}
			} else if (event.getSource().equals(m_print)) {

				PrintManager.print(m_draw_pane);
			} else if (event.getSource().equals(m_exit)) {

				exit();
			}
		}
	}

	public class EditionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			if (event.getSource().equals(m_cancel)) {

				m_draw_pane.getPointsList().remove(m_draw_pane.getPointsList().size() - 1);

				repaint();

				if (m_draw_pane.getPointsList().isEmpty()) {

					// m_cancel.setEnabled(false);
				}
			}
		}
	}

	public class FormatListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			if (event.getSource().equals(m_hand_button)) {

				m_hand_button.setEnabled(false);
				m_pencil_button.setEnabled(true);

				m_draw_pane.setToolType(DrawPanel.HAND_TOOL);
				m_draw_pane.setCursor(new Cursor(Cursor.HAND_CURSOR));
			} else if (event.getSource().equals(m_pencil_button)) {

				m_hand_button.setEnabled(true);
				m_pencil_button.setEnabled(false);

				m_draw_pane.setToolType(DrawPanel.PENCIL_TOOL);
				m_draw_pane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} else if (event.getSource().equals(m_point_size)) {

				final JDialog point_size_dialog = GUIFactory.createDialog("Taille du point", 200, 100,
						new ImageIcon(getClass().getResource("res/icons/square-32.png")), true);

				final JComboBox<Object> point_size_box = new JComboBox<Object>();

				for (int i = 0; i < 100; i++) {

					point_size_box.addItem(i + 1);
				}

				point_size_box.addItemListener(e -> {

					m_point_size_box.setSelectedItem(e.getItem());
					m_draw_pane.setPointSize(Integer.parseInt(e.getItem().toString()));

					point_size_dialog.dispose();
				});

				point_size_box.setFont(FontManager.CENTURY_GOTHIC_BOLD_20);
				point_size_box.setSelectedItem(m_draw_pane.getPointSize());
				((JLabel) point_size_box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

				point_size_dialog.add(point_size_box);

				point_size_dialog.setVisible(true);
			} else if (event.getSource().equals(m_point_type)) {

				final JDialog point_type_dialog = GUIFactory.createDialog("Forme du point", 200, 100,
						new ImageIcon(getClass().getResource("res/icons/shape-32.png")), true);

				final Object[] shapes = {

						new ImageIcon(getClass().getResource("res/icons/filled_circle-32.png")),
						new ImageIcon(getClass().getResource("res/icons/filled_square-32.png")),
						new ImageIcon(getClass().getResource("res/icons/filled_triangle-32.png")),
						new ImageIcon(getClass().getResource("res/icons/circle-32.png")),
						new ImageIcon(getClass().getResource("res/icons/square-32.png")),
						new ImageIcon(getClass().getResource("res/icons/triangle-32.png")) };

				final JComboBox<Object> point_type_box = new JComboBox<Object>(shapes);
				point_type_box.addItemListener(e -> {

					int point_type = 0;

					if (point_type_box.getSelectedItem().toString().endsWith("filled_circle-32.png")) {

						point_type = DrawPoint.FILLED_CIRCLE;
					} else if (point_type_box.getSelectedItem().toString().endsWith("filled_square-32.png")) {

						point_type = DrawPoint.FILLED_SQUARE;
					} else if (point_type_box.getSelectedItem().toString().endsWith("filled_triangle-32.png")) {

						point_type = DrawPoint.FILLED_TRIANGLE;
					} else if (point_type_box.getSelectedItem().toString().endsWith("circle-32.png")) {

						point_type = DrawPoint.CIRCLE;
					} else if (point_type_box.getSelectedItem().toString().endsWith("square-32.png")) {

						point_type = DrawPoint.SQUARE;
					} else if (point_type_box.getSelectedItem().toString().endsWith("triangle-32.png")) {

						point_type = DrawPoint.TRIANGLE;
					}

					m_point_type_box.setSelectedIndex(point_type_box.getSelectedIndex());
					;
					m_draw_pane.setPointType(point_type);

					point_type_dialog.dispose();
				});

				point_type_box.setSelectedIndex(m_point_type_box.getSelectedIndex());
				((JLabel) point_type_box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

				point_type_dialog.add(point_type_box);

				point_type_dialog.setVisible(true);
			} else if (event.getSource().equals(m_point_color) || event.getSource().equals(m_point_color_button)) {

				m_draw_pane.setPointColor(
						JColorChooser.showDialog(m_instance, "Choix de la couleur", m_draw_pane.getPointColor()));

				m_point_color_button.setBackground(m_draw_pane.getPointColor());
			}
		}
	}

	public class HelpListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			JOptionPane.showMessageDialog(m_instance, "ApprenticePainter est un projet réalisé pour le fun!", "À propos",
					JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getClass().getResource("res/icons/paint_brush-48.png")));
		}
	}

	// OBSERVERS

	@Override
	public void update(boolean saved) {

		m_current_image_is_recorded = saved;
	}

	// IMPLEMENTED METHODS

	// ----------------------------------------------------[ E
	// ]----------------------------------------------------//

	public void exit() {

		if (!m_current_image_is_recorded) {

			final int answer = JOptionPane.showConfirmDialog(m_instance,
					"Si vous continuez toutes les données non sauvegardées seront perdues, voulez vous sauvegarder avant ?",
					"Avertissement!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
					new ImageIcon(getClass().getResource("res/icons/help-48.png")));

			switch (answer) {

			case JOptionPane.YES_OPTION:

				if (!m_current_image_path.isEmpty()) {

					write();
				} else {

					if (m_file_chooser.showSaveDialog(m_instance) == JFileChooser.APPROVE_OPTION) {

						final File file = m_file_chooser.getSelectedFile();

						if (m_file_chooser.getFileFilter().accept(file)) {

							write();
						} else {

							JOptionPane.showMessageDialog(m_instance, "L'extension fournie est invalide !", "Erreur",
									JOptionPane.ERROR_MESSAGE,
									new ImageIcon(getClass().getResource("res/icons/error-48.png")));
						}
					}
				}

				break;

			case JOptionPane.NO_OPTION:

				dispose();

				break;
			}
		}

		dispose();
	}

	// ----------------------------------------------------[ W
	// ]----------------------------------------------------//

	public void write() {

		final BufferedImage img = (BufferedImage) m_draw_pane.createImage(m_draw_pane.getWidth(),
				m_draw_pane.getHeight());
		final Graphics g = img.getGraphics();

		m_draw_pane.paint(g);

		g.dispose();

		try {

			ImageIO.write(img, "jpg", new File(m_current_image_path));
		} catch (final IOException e) {

			e.printStackTrace();
		}

		m_current_image_is_recorded = true;
	}
}
