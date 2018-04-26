package nl.tudelft.watchdog.eclipse.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.ui.preferences.PreferencePage;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.util.BrowserOpenerSelection;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * A dialog displaying statistics about the health and status quo of WatchDog.
 */
public class InfoDialog extends Dialog {

	private Color colorRed;
	private Color colorGreen;
	private Composite parentContainer;
	private Preferences preferences;

	/** Constructor. */
	public InfoDialog(Shell parentShell) {
		super(parentShell);
		preferences = Preferences.getInstance();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.parentContainer = parent;
		Composite container = (Composite) super.createDialogArea(parent);
		createGridLayout(container);
		createStatusText(container);
		createStaticLinks(container);

		return container;
	}

	/** Creates a grid layout for the given {@link Composite}. */
	private void createGridLayout(Composite container) {
		final int layoutMargin = 10;
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = layoutMargin;
		layout.marginLeft = layoutMargin;
		layout.marginBottom = layoutMargin;
		layout.marginRight = layoutMargin;
		container.setLayout(layout);
	}

	/** Creates a label with the status of WatchDog plugin. */
	private void createStatusText(Composite parentContainer) {
		Composite logoContainer = UIUtils
				.createFullGridedComposite(parentContainer, 1);
		logoContainer.setData(new GridData(SWT.CENTER, SWT.NONE, true, false));
		Label watchdogLogo = UIUtils.createWatchDogLogo(logoContainer);
		watchdogLogo.setLayoutData(
				new GridData(SWT.CENTER, SWT.BEGINNING, true, false));
		UIUtils.createLabel(" ", logoContainer);

		Composite container = UIUtils
				.createZeroMarginGridedComposite(parentContainer, 2);
		colorRed = new Color(getShell().getDisplay(), 255, 0, 0);
		colorGreen = new Color(getShell().getDisplay(), 0, 150, 0);
		UIUtils.createLabel("WatchDog Status: ", container);
		if (WatchDogGlobals.isActive) {
			UIUtils.createLabel(WatchDogGlobals.ACTIVE_WATCHDOG_TEXT, container,
					colorGreen);
		} else {
			Composite localGrid = UIUtils
					.createZeroMarginGridedComposite(container, 2);
			UIUtils.createLabel(WatchDogGlobals.INACTIVE_WATCHDOG_TEXT,
					localGrid, colorRed);
			createFixThisProblemLink(localGrid, new PreferenceListener());

		}
		createDataInfoLabels(container);
		UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
	}

	private void createDataInfoLabels(Composite container) {
		createCheckIdsOnServer(container, preferences);

		UIUtils.createLabel(" ", container);
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel("Transfered intervals: ", container);
		UIUtils.createLabel(Long.toString(preferences.getIntervals()),
				container);
		UIUtils.createLabel("Last Transfered: ", container);
		UIUtils.createLabel(preferences.getLastIntervalTransferDate(),
				container);
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel(" ", container);

		UIUtils.createLabel("Transfered events: ", container);
		UIUtils.createLabel(Long.toString(preferences.getEvents()), container);
		UIUtils.createLabel("Last Transfered: ", container);
		UIUtils.createLabel(preferences.getLastEventTransferDate(), container);
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel(" ", container);

		UIUtils.createLabel("WatchDog Version:", container);
		UIUtils.createLabel(WatchDogGlobals.CLIENT_VERSION, container);
		UIUtils.createLabel(" ", container);
		if (preferences.isOldVersion()) {
			Composite localGrid = UIUtils
					.createZeroMarginGridedComposite(container, 2);
			UIUtils.createLabel("Outdated!", localGrid, colorRed);
			createFixThisProblemLink(localGrid, new DefaultSelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					UIUtils.updateWatchDog();
				}
			});
		} else {
			UIUtils.createLabel(" ", container);
		}
	}

	private void createCheckIdsOnServer(Composite container,
			Preferences preferences) {
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel(" ", container);
		UIUtils.createLabel("User ID: ", container);
		Connection userConnection = NetworkUtils.urlExistsAndReturnsStatus200(
				NetworkUtils.buildExistingUserURL(preferences.getUserId()));
		reactOnConnectionStatus(container, userConnection,
				new UserButtonListener());

		Connection projectConnection = Connection.UNSUCCESSFUL;
		ProjectPreferenceSetting workspaceSettings = preferences
				.getOrCreateProjectSetting(WatchDogUtils.getWorkspaceName());
		boolean projectIdHasProblem = false;
		if (userConnection == Connection.SUCCESSFUL
				&& workspaceSettings.enableWatchdog) {
			UIUtils.createLabel("Project ID: ", container);
			projectConnection = NetworkUtils.urlExistsAndReturnsStatus200(
					NetworkUtils.buildExistingProjectURL(
							workspaceSettings.projectId));
			if (projectConnection != Connection.SUCCESSFUL) {
				projectIdHasProblem = true;
			}
			reactOnConnectionStatus(container, projectConnection,
					new ProjectButtonListener());
		}

		if (userConnection != Connection.SUCCESSFUL || projectIdHasProblem) {
			UIUtils.createLabel(" ", container);
			UIUtils.createLabel("WatchDog cannot transfer its data.", container,
					colorRed);
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
			Composite localGrid = UIUtils
					.createZeroMarginGridedComposite(container, 2);
			UIUtils.createLabel("Does not exist!", localGrid, colorRed);
			WatchDogGlobals.lastTransactionFailed = true;
			createFixThisProblemLink(localGrid, listener);
			break;
		case NETWORK_ERROR:
			localGrid = UIUtils.createZeroMarginGridedComposite(container, 2);
			UIUtils.createLabel("(Temporary) Network Error.", localGrid);
			WatchDogGlobals.lastTransactionFailed = true;
			createFixThisProblemLink(localGrid, new PreferenceListener());
		}
	}

	private void createStaticLinks(Composite parentContainer) {
		UIUtils.createLabel("", parentContainer);
		Composite container = UIUtils.createFullGridedComposite(parentContainer,
				3);
		container.setData(new GridData(SWT.CENTER, SWT.NONE, true, false));

		UIUtils.createLinkedLabel(container, new WatchDogViewListener(),
				"Open View.", "")
				.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createOpenReportLink(container);
		UIUtils.createLabel("", container);
		UIUtils.createLinkedLabel(container, new BrowserOpenerSelection(),
				"Open logs.",
				"file://" + WatchDogLogger.getInstance().getLogDirectoryPath())
				.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createLinkedLabel(container, new PreferenceListener(),
				"Open Prefs.", "")
				.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createLinkedLabel(container, new BrowserOpenerSelection(),
				"Report bug.", "https://github.com/TestRoots/watchdog/issues")
				.setLayoutData(UIUtils.createFullGridUsageData());
	}

	private void createFixThisProblemLink(Composite localGrid,
			SelectionListener listener) {
		UIUtils.createLinkedLabel(localGrid, listener, "Fix this.", "");
	}

	/** {@inheritDoc} Disables the creation of a cancel button in the dialog */
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		if (id == IDialogConstants.CANCEL_ID) {
			return null;
		}
		Button createdButton = super.createButton(parent, id, label, true);
		createdButton.setFocus();
		return createdButton;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("WatchDog Statistics");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 570);
	}

	private class WatchDogViewListener extends DefaultSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(WatchDogView.ID);
			} catch (PartInitException exception) {
				// intentionally empty
			}
			super.widgetSelected(e);
		}
	}

	private class PreferenceListener extends DefaultSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			PreferencesUtil.createPreferenceDialogOn(null, PreferencePage.ID,
					null, null).open();
			super.widgetSelected(e);
		}
	}

	private class UserButtonListener extends DefaultSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			UIUtils.invokeCommand(
					"nl.tudelft.watchdog.commands.UserWizardDialog");
			super.widgetSelected(e);
		}
	}

	private class ProjectButtonListener extends DefaultSelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			UIUtils.invokeCommand(
					"nl.tudelft.watchdog.commands.ProjectWizardDialog");
			super.widgetSelected(e);
		}
	}

	private abstract class DefaultSelectionListener
			implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Composite uberParentContainer = parentContainer.getParent();
			parentContainer.dispose();
			createContents(uberParentContainer);
			uberParentContainer.layout(true);
			uberParentContainer.redraw();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// Intentionally empty
		}
	}

}
