package org.comtel.swing.ui;

import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

public class KeyboardTextAreaUI extends BasicTextAreaUI {

	private static FocusListener fl = null;
	private static MouseListener ml = null;

	public static void setFocusListener(FocusListener l) {
		fl = l;
	}

	public static void setMouseListener(MouseListener l) {
		ml = l;
	}

	public KeyboardTextAreaUI() {
		super();
	}

	public static ComponentUI createUI(JComponent c) {
		return new KeyboardTextAreaUI();
	}

	@Override
	public void installUI(JComponent c) {
		if (c instanceof JTextComponent) {
			if (fl != null) {
				((JTextComponent) c).addFocusListener(fl);
			}
			if (ml != null) {
				((JTextComponent) c).addMouseListener(ml);
			}
		}
		super.installUI(c);
	}
}
