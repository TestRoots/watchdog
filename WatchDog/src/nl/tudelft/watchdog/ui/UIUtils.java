package nl.tudelft.watchdog.ui;

import nl.tudelft.watchdog.Activator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** Utility methods for the UI. */
public class UIUtils {

	/**
	 * Creates and returns a label whose text is wrapped inside the supplied
	 * {@link Composite}. Be careful: The width calculation on the parent
	 * composite only works when called after the parent has already been
	 * created on the screen, i.e. its client area is known.
	 */
	public static Label createWrappingLabel(String text, Composite parent) {
		Label label = createLabel(text, SWT.WRAP, parent);
		GridData labelData = new GridData();
		labelData.widthHint = parent.getParent().getClientArea().width - 30;
		label.setLayoutData(labelData);
		return label;
	}

	/** Creates and returns a bold text label. */
	public static Label createBoldLabel(String text, Composite parent) {
		Label label = createLabel(text, parent);
		label.setFont(JFaceResources.getFontRegistry().getBold(""));
		return label;
	}

	/** Creates and returns a bold text label with associated SWT-Style. */
	public static Label createBoldLabel(String text, int swtStyle,
			Composite parent) {
		Label label = createLabel(text, swtStyle, parent);
		label.setFont(JFaceResources.getFontRegistry().getBold(""));
		return label;
	}

	/** Creates and returns an italic text label. */
	public static Label createItalicLabel(String text, Composite parent) {
		Label label = createLabel(text, parent);
		label.setFont(JFaceResources.getFontRegistry().getItalic(""));
		return label;
	}

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
	 * Creates and returns a grided composite, that fills out its parent to the
	 * fullest extent.
	 */
	public static Composite createFullGridedComposite(Composite parent,
			int columns) {
		Composite composite = UIUtils.createGridedComposite(parent, columns);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		return composite;
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
	 * Creates a pair of a linked Label and text input field.
	 * 
	 * @param labelText
	 *            The text of the label associated with the input.
	 * @param toolTip
	 *            The tooltip displayed on both the label and the input.
	 * @param composite
	 *            The composite on which both should be put.
	 * @return
	 * 
	 * @return input The linked input.
	 */
	public static Text createLinkedFieldInput(String labelText, String toolTip,
			Composite composite) {
		Label label = UIUtils.createLabel(labelText, composite);
		label.setToolTipText(toolTip);
		Text input = UIUtils.createTextInput(composite);
		input.setLayoutData(UIUtils.createFullGridUsageData());
		input.setToolTipText(toolTip);
		UIUtils.attachListenerOnLabelClickFocusTextElement(label, input);
		return input;
	}

	/**
	 * Attaches a listener to the specified label that directs the focus to the
	 * supplied text (resulting in an HTML form-like connection of the label and
	 * its input field).
	 */
	private static void attachListenerOnLabelClickFocusTextElement(Label label,
			final Text text) {
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// intentionally left empty
			}

			@Override
			public void mouseDown(MouseEvent e) {
				focusAccompanyingInput();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				focusAccompanyingInput();
			}

			private void focusAccompanyingInput() {
				text.forceFocus();
			}
		});

	}

	/**
	 * @return The workspace name.
	 */
	public static String getWorkspaceName() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile()
				.toString();
	}

	/** The TU Logo. */
	public static final ImageDescriptor TU_DELFT_LOGO = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"resources/images/tudelft.png");

	/**
	 * The warning displayed when WatchDog is not active.
	 */
	public static final String WATCHDOG_WARNING = "WatchDog only works when you register a (possibly anonymous) user.\n\nTakes less than one minute,  and you can win prices. As a registered user, you decide where WatchDog is active.";
}
