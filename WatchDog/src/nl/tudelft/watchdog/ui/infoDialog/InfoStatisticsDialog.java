package nl.tudelft.watchdog.ui.infoDialog;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.preferences.WorkspacePreferenceSetting;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog displaying statistics about the health and status quo of WatchDog.
 */
public class InfoStatisticsDialog extends Dialog {

	private Color colorRed;
	private Color colorGreen;

	/** Constructor. */
	public InfoStatisticsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		createGridLayout(container);
		createStatusText(container);

		return container;
	}

	/** Creates a grid layout for the given {@link Composite}. */
	private void createGridLayout(Composite container) {
		final int layoutMargin = 10;
		GridLayout layout = new GridLayout(2, false);
		layout.marginTop = layoutMargin;
		layout.marginLeft = layoutMargin;
		layout.marginBottom = layoutMargin;
		layout.marginRight = layoutMargin;
		container.setLayout(layout);
	}

	/** Creates a label with the status of WatchDog plugin. */
	private void createStatusText(Composite container) {
		colorRed = new Color(getShell().getDisplay(), 255, 0, 0);
		colorGreen = new Color(getShell().getDisplay(), 0, 150, 0);
		UIUtils.createLabel("WatchDog Status: ", container);
		if (WatchDogGlobals.isActive) {
			UIUtils.createLabel(WatchDogGlobals.activeWatchDogUIText,
					container, colorGreen);
		} else {
			UIUtils.createLabel(WatchDogGlobals.inactiveWatchDogUIText,
					container, colorRed);
		}
		Preferences preferences = Preferences.getInstance();

		createCheckIdsOnServer(container, preferences);

		UIUtils.createLabel(" ", container);
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel("Transfered intervals: ", container);
		UIUtils.createLabel(Long.toString(preferences.getIntervals()),
				container);
		UIUtils.createLabel("Last Transfered: ", container);
		UIUtils.createLabel(preferences.getLastIntervalTransferDate(),
				container);
		UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
	}

	private void createCheckIdsOnServer(Composite container,
			Preferences preferences) {
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel("User ID: ", container);
		Connection userConnection = NetworkUtils
				.urlExistsAndReturnsStatus200(NetworkUtils
						.buildExistingUserURL(preferences.getUserid()));
		reactOnConnectionStatus(container, userConnection,
				new UserButtonListener());

		Connection projectConnection = Connection.UNSUCCESSFUL;
		WorkspacePreferenceSetting workspaceSettings = preferences
				.getOrCreateWorkspaceSetting(UIUtils.getWorkspaceName());
		boolean projectIdHasProblem = false;
		if (userConnection == Connection.SUCCESSFUL
				&& workspaceSettings.enableWatchdog) {
			UIUtils.createLabel("Project ID: ", container);
			projectConnection = NetworkUtils
					.urlExistsAndReturnsStatus200(NetworkUtils
							.buildExistingProjectURL(workspaceSettings.projectId));
			if (projectConnection != Connection.SUCCESSFUL) {
				projectIdHasProblem = true;
			}
			reactOnConnectionStatus(container, projectConnection,
					new ProjectButtonListener());
		}

		if (userConnection != Connection.SUCCESSFUL || projectIdHasProblem) {
			UIUtils.createLabel(" ", container);
			UIUtils.createLabel("WatchDog cannot transfer its data.",
					container, colorRed);
		}
	}

	private void reactOnConnectionStatus(Composite container,
			Connection userConnection, SelectionListener listener) {
		switch (userConnection) {
		case SUCCESSFUL:
			UIUtils.createLabel("OK", container, colorGreen);
			WatchDogGlobals.lastTransactionFailed = false;
			break;
		case UNSUCCESSFUL:
			Composite localGrid = UIUtils.createFullGridedComposite(container,
					2);
			UIUtils.createLabel("Does not exist!", localGrid, colorRed);
			Button userRegistration = new Button(localGrid, SWT.PUSH);
			WatchDogGlobals.lastTransactionFailed = true;

			userRegistration.addSelectionListener(listener);
			userRegistration.setText("Fix this problem.");
			break;
		case NETWORK_ERROR:
			UIUtils.createLabel("(Temporary) Network Error.", container);
			WatchDogGlobals.lastTransactionFailed = true;
		}
	}

	/** {@inheritDoc} Disables the creation of a cancel button in the dialog */
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		if (id == IDialogConstants.CANCEL_ID) {
			return null;
		}
		return super.createButton(parent, id, label, true);
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

	private class UserButtonListener implements SelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			UIUtils.invokeCommand("nl.tudelft.watchdog.commands.UserWizardDialog");
			okPressed();
			UIUtils.invokeCommand(UIUtils.COMMAND_SHOW_INFO);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// Intentionally empty
		}
	}

	private class ProjectButtonListener implements SelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			UIUtils.invokeCommand("nl.tudelft.watchdog.commands.ProjectWizardDialog");
			okPressed();
			UIUtils.invokeCommand(UIUtils.COMMAND_SHOW_INFO);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// Intentionally empty
		}
	}

}