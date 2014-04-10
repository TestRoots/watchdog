package nl.tudelft.watchdog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/** Utility methods for the UI. */
public class UIUtils {

	/** Creates and returns a label with the given text. */
	public static Label createLabel(String text, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}
}
