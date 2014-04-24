package nl.tudelft.watchdog.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
		return createLabel(text, SWT.NONE, parent);
	}

	/** Creates and returns a label with a given style and text. */
	public static Label createLabel(String text, int style, Composite parent) {
		Label label = new Label(parent, style);
		label.setText(text);
		return label;
	}

	/** Creates and returns a user text input field. */
	public static Text createTextInput(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(createFullGridUsageData());
		return text;
	}

	/** Creates and returns a radio button with the given text. */
	public static Button createRadioButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		return button;
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

	/** @return A fully horizontally greedy Grid. */
	public static GridData createFullGridUsageData() {
		// has to create new instances because the existing instance are altered
		// once passed into an object.
		return new GridData(SWT.FILL, SWT.NONE, true, false);
	}

	/**
	 * @return The workspace name.
	 */
	public static String getWorkspaceName() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile()
				.toString();
	}
}
