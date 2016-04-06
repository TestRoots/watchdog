package nl.tudelft.watchdog.eclipse.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.ui.WatchDogView;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.util.CommandExecuterBase.CommandExecuter;
import nl.tudelft.watchdog.eclipse.ui.util.CommandExecuterBase.CommandRefresher;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/** Utility methods for the UI. */
public class UIUtils {

	/** The command to show the WatchDog info. */
	public static final String COMMAND_SHOW_INFO = "nl.tudelft.watchdog.commands.showWatchDogInfo";

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

	/** Creates uneditable text field. */
	public static Text createTextField(Composite parent, String content) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(
				new GridData(SWT.BOTTOM, SWT.BEGINNING, true, false));
		text.setText(content);
		text.setEditable(false);
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
	public static Composite createZeroMarginGridedComposite(Composite parent,
			int columns) {
		Composite composite = createGridedComposite(parent, columns);
		GridLayout layout = (GridLayout) composite.getLayout();
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		return composite;
	}

	/**
	 * @return A {@link GridLayout}ed composite with the given number of
	 *         columns.
	 */
	public static Composite createGridedComposite(Composite parent,
			int columns) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(columns, false));
		return composite;
	}

	/** @return A fully horizontally greedy Grid. */
	public static GridData createFullGridUsageData() {
		// Has to create new instances because the existing instance are altered
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

	/** The TU Logo. */
	public static final ImageDescriptor TU_DELFT_LOGO = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"resources/images/tudelft_with_frame.png");

	/** The WatchDog Icon. */
	public static final ImageDescriptor WATCHDOG_ICON = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"resources/images/watchdog_icon.png");

	/** The WatchDog Icon Disabled. */
	public static final ImageDescriptor WATCHDOG_ICON_DISABLED = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"resources/images/watchdog_icon_disabled.png");

	/** The WatchDog Icon Warning. */
	public static final ImageDescriptor WATCHDOG_ICON_WARNING = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"resources/images/watchdog_icon_warning.png");

	/** Creates and returns a label with the given text and color. */
	public static Label createLabel(String text, Composite parent,
			Color color) {
		Label label = createLabel(text, parent);
		label.setForeground(color);
		return label;
	}

	/** Creates a centered label containing the WatchDogLogo. */
	public static Label createWatchDogLogo(Composite logoContainer) {
		return createLogo(logoContainer, "resources/images/watchdog_small.png");
	}

	/** Creates a logo from the given image url. */
	public static Label createLogo(Composite logoContainer,
			String imageLocation) {
		Label watchdogLogo = new Label(logoContainer, SWT.NONE);
		ImageDescriptor watchdogLogoImageDescriptor = Activator
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID, imageLocation);
		Image watchdogLogoImage = watchdogLogoImageDescriptor.createImage();
		watchdogLogo.setImage(watchdogLogoImage);
		watchdogLogo.setLayoutData(
				new GridData(SWT.CENTER, SWT.BEGINNING, true, false));
		return watchdogLogo;
	}

	/** Invokes the supplied command. */
	public static void invokeCommand(final String command) {
		new CommandExecuter(command).execute();
	}

	/** Refreshes the supplied command's ui elements. */
	public static void refreshCommand(final String command) {
		new CommandRefresher(command).execute();
	}

	/** Updates WatchDog. */
	public static void updateWatchDog() {
		invokeCommand("org.eclipse.equinox.p2.ui.sdk.update");
	}

	/**
	 * Returns the WatchDog view, or <code>null</code> if it cannot launch or
	 * find it.
	 */
	public static WatchDogView getWatchDogView() {
		try {
			return (WatchDogView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.findViewReference(WatchDogView.ID).getView(false);
		} catch (NullPointerException npe) {
			WatchDogLogger.getInstance().logSevere(npe);
			return null;
		}
	}

	/** Creates a clickable link with the given description text. */
	public static Link createLinkedLabel(Composite parent,
			SelectionListener listener, String description, String url) {
		Link link = new Link(parent, SWT.WRAP);
		link.setText("<a href=\"" + url + "\">" + description + "</a>");
		link.addSelectionListener(listener);
		return link;
	}

	/** Creates a Combo List of String items. */
	public static Combo createComboList(Composite parent,
			SelectionListener listener, String[] items, int defaultSelection) {
		Combo comboList = new Combo(parent,
				SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		comboList.setItems(items);
		comboList.select(defaultSelection);
		comboList.addSelectionListener(listener);
		return comboList;
	}

	/** Creates a linked label that opens the project report in a browser. */
	public static void createOpenReportLink(Composite container) {
		String projectReport = "http://www.testroots.org/reports/project/"
				+ WatchDogUtils.getProjectSetting().projectId + ".html";
		UIUtils.createLinkedLabel(container, new BrowserOpenerSelection(),
				"Open Report.", projectReport);
	}

	/**
	 * Creates a linked label that opens the debug survey in the browser with
	 * the correct User ID.
	 */
	public static void createStartDebugSurveyLink(Composite container) {
		String surveyLink = "https://docs.google.com/forms/d/1ybD1jC-iICXNlmQpyPEFngtmOtodicDr18E1ZbfBtx4/viewform?entry.1872114938="
				+ Preferences.getInstance().getUserId()
				+ "&entry.87074017=Java&entry.1002919343=Eclipse&entry.2010347695&entry.2084367812";
		UIUtils.createLinkedLabel(container, new BrowserOpenerSelection(),
				"Share your thoughts on debugging and win an additional Amazon voucher!\n",
				surveyLink);
	}
}
