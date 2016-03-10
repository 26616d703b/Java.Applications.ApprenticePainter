package com.ampersand.ap;

import javax.swing.SwingUtilities;

public final class Main {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {

			final ApprenticePainter painter = new ApprenticePainter();
			painter.setVisible(true);
		});
	}
}
