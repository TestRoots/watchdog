package nl.tudelft.watchdog.ui.wizards.userregistration;

import java.net.MalformedURLException;
import java.net.URL;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * A page that contains the WatchDog project description.
 */
public class UserWatchDogDescriptionPage extends FinishableWizardPage {

	private Link linkedText;
	private Label welcomeText;

	/** Constructor. */
	protected UserWatchDogDescriptionPage() {
		super("WatchDog Description");
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("What is WatchDog? (2/3)");
		setDescription("Help Science, and win prizes along the way");

		Composite topComposite = UIUtils.createFullGridedComposite(parent, 1);
		createWatchDogDescription(topComposite);
		createLogoRow(topComposite);

		setControl(topComposite);
		setPageComplete(true);
	}

	private Composite createWatchDogDescription(Composite topContainer) {

		Composite composite = UIUtils
				.createFullGridedComposite(topContainer, 1);

		welcomeText = UIUtils.createBoldLabel("", SWT.WRAP, composite);

		linkedText = new Link(composite, SWT.WRAP);
		linkedText.setText("");
		linkedText.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					// Open default external browser
					PlatformUI.getWorkbench().getBrowserSupport()
							.getExternalBrowser().openURL(new URL(event.text));
				} catch (PartInitException | MalformedURLException exception) {
					// Browser could not be opened. We do nothing about it.
				}
			}
		});

		return composite;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			welcomeText
					.setText("WatchDog is a free, non-commercial Eclipse plugin from TU Delft that assesses how developers make software.");
			welcomeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			welcomeText.getParent().layout();
			welcomeText.getParent().update();

			String welcomeText = "\nIt measures how much production versus test code you write (never what you write!), and when you run tests. Our promise: <a href=\"http://www.testroots.org/data\">Your data</a> is always encrypted, and we never do anything bad with it.\n\nWhat's in it for you? Super-amazing <a href=\"http://www.testroots.org/prizes\">prizes</a> and a truly appreciated contribution to science! :-).\n";

			linkedText.setText(welcomeText);
			linkedText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			linkedText.getParent().layout();
			linkedText.getParent().update();
			linkedText.getParent().getParent().layout();
			linkedText.getParent().getParent().update();
		}
	}

	/** Creates a horizontal separator. */
	@SuppressWarnings("unused")
	public void createSeparator(Composite parent) {
		Label separator = UIUtils.createLabel("", SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.FILL, parent);
		GridData layoutData = UIUtils.createFullGridUsageData();
		layoutData.horizontalSpan = 2;
		separator.setLayoutData(layoutData);
	}

	@Override
	public boolean canFinish() {
		return false;
	}

}
