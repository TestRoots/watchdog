package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NewUserWizard extends Wizard {

	public NewUserWizard() {
		super();
	}

	@Override
	public void addPages() {
		addPage(new NewUserWizardPage());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	class NewUserWizardPage extends WizardPage {

		/**
		 * The Composite which holds the text field for the existing user
		 * registration.
		 */
		private Composite existingUserRegistration;

		/** Constructor. */
		NewUserWizardPage() {
			super("Welcome to WatchDog!");
			setTitle("Welcome to WatchDog!");
			setDescription("This wizard will guide you through the setup of WatchDog. May we ask for one minute of your time?");
		}

		@Override
		public void createControl(Composite parent) {
			Composite topContainer = createGridedComposite(parent, 1);

			// Sets up the basis layout (2 column grid)
			Composite questionContainer = createQuestionComposite(topContainer);
			existingUserRegistration = createWatchDogIdComposite(topContainer);

			// Required to avoid an error in the system
			setControl(questionContainer);
			setPageComplete(false);

		}

		/**
		 * Creates and returns the question whether WatchDog Id is already
		 * known.
		 */
		private Composite createQuestionComposite(Composite parent) {
			Composite questionContainer = createGridedComposite(parent, 2);

			UIUtils.createLabel("Is this the first time you install WatchDog?",
					questionContainer);

			final Composite radioButtons = createGridedComposite(
					questionContainer, 1);
			radioButtons.setLayout(new FillLayout());
			final Button radioButtonYes = new Button(radioButtons, SWT.RADIO);
			radioButtonYes.setText("Yes");
			radioButtonYes.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			final Button radioButtonNo = new Button(radioButtons, SWT.RADIO);
			radioButtonNo.setText("No");
			radioButtonNo.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					existingUserRegistration.setVisible(radioButtonNo
							.getSelection());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			return questionContainer;
		}

		/**
		 * Creates and returns an input field, in which user can enter their
		 * existing WatchDog ID.
		 */
		private Composite createWatchDogIdComposite(Composite parent) {
			Composite existingUserRegistration = new Composite(parent, SWT.NONE);

			existingUserRegistration.setLayout(new GridLayout(2, false));
			GridData fullGirdUsageData = new GridData(SWT.FILL, SWT.NONE, true,
					false);
			existingUserRegistration.setLayoutData(fullGirdUsageData);
			existingUserRegistration.setVisible(false);

			UIUtils.createLabel("Your WatchDog Id:", existingUserRegistration);

			Text text = new Text(existingUserRegistration, SWT.SINGLE
					| SWT.BORDER);
			text.setTextLimit(40);
			text.setToolTipText("The SHA-1 hash that was returned upon your last WatchDog registration. You may (and should!) reuse your registration when you install a new Eclipse version for the same purpose.");
			text.setLayoutData(fullGirdUsageData);
			return existingUserRegistration;
		}

		/**
		 * @return A {@link GridLayout}ed composite with the given number of
		 *         columns.
		 */
		private Composite createGridedComposite(Composite parent, int columns) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(columns, false));
			return composite;
		}

	}

}
