package nl.tudelft.watchdog.plugin.infoDialog;

import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog displaying statistics about the WatchDog recordings. This dialog is
 * needed for the user to benefit from WatchDog and gain insights into how much
 * he's developing, and how much he is testing.
 */
public class InfoStatisticsDialog extends Dialog {

	/** Constructor. */
	public InfoStatisticsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);

		final int layoutMargin = 10;
		layout.marginTop = layoutMargin;
		layout.marginLeft = layoutMargin;
		layout.marginBottom = layoutMargin;
		layout.marginRight = layoutMargin;
		container.setLayout(layout);

		Color colorRed = new Color(getShell().getDisplay(), 255, 0, 0);
		Color colorGreen = new Color(getShell().getDisplay(), 0, 150, 0);
		Color colorBlack = new Color(getShell().getDisplay(), 0, 0, 0);
		createLabel("WatchDog Status: ", container, colorBlack);
		if (WatchDogGlobals.isActive) {
			createLabel(WatchDogGlobals.activeWatchDogUIText, container,
					colorGreen);
		} else {
			createLabel(WatchDogGlobals.inactiveWatchDogUIText, container,
					colorRed);
		}

		return container;
	}

	private Label createLabel(String text, Composite parent, Color color) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setForeground(color);
		return label;
	}

	/** Disables the creation of a cancle button in the dialog */
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		if (id == IDialogConstants.CANCEL_ID) {
			return null;
		}
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("WatchDog Statistics");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}