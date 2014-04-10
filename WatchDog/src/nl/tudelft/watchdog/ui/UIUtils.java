package nl.tudelft.watchdog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** Utility methods for the UI. */
public class UIUtils {

	/** Constant for full horizontal usage of Grid data. */
	public final static GridData fullGirdUsageData = new GridData(SWT.FILL,
			SWT.NONE, true, false);

	/** Creates and returns a label with the given text. */
	public static Label createLabel(String text, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	/** Creates and returns a user text input field. */
	public static Text createTextInput(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(fullGirdUsageData);
		return text;
	}

	/**
	 * @return A {@link GridLayout}ed composite with the given number of
	 *         columns.
	 */
	public static Composite createGridedComposite(Composite parent, int columns) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(columns, false));
		return composite;
	}
}
