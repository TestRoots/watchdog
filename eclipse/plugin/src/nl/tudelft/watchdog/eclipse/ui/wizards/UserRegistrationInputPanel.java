package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

import static nl.tudelft.watchdog.core.ui.wizards.User.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.eclipse.ui.util.UIUtils.HEADER_FONT;

class UserRegistrationInputPanel extends Composite {

	private final Composite buttonContainer;
	private Text email;
	private Text company;
	private Combo programmingExperience;
	private Text operatingSystem;
	private Composite statusContainer;

	/**
	 * A panel to ask the user questions to create their user profile.
	 * @param container The parent container.
	 * @param callback The callback invoked after the user clicked "Create WatchDog User".
	 */
	UserRegistrationInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, SWT.NONE);
		this.setLayout(new RowLayout(SWT.VERTICAL));

		Label header = new Label(this, SWT.NONE);
		header.setText(WATCHDOG_USER_PROFILE);
		header.setFont(HEADER_FONT);

		new Label(this, SWT.NONE).setText(USER_DATA_REQUEST);
		new Label(this, SWT.NONE).setText(INPUT_IS_OPTIONAL);

		this.createInputFields();

		this.buttonContainer = new Composite(this, SWT.NONE);
		this.buttonContainer.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button createNewUserButton = new Button(this.buttonContainer, SWT.NONE);
		createNewUserButton.setText(CREATE_USER_BUTTON_TEXT);

		this.statusContainer = new Composite(this, SWT.NONE);
		this.statusContainer.setLayout(new RowLayout(SWT.HORIZONTAL));

		createNewUserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control child : statusContainer.getChildren()) {
					child.dispose();
				}

				for (Control child : buttonContainer.getChildren()) {
					if (child != createNewUserButton) {
						child.dispose();
					}
				}

				callback.accept(registerUser());
			}
		});
	}
	
	private void createInputFields() {
		Composite inputContainer = new Composite(this, SWT.NONE);
		inputContainer.setLayout(new GridLayout(2, true));

		this.email = RegistrationStep.createLinkedLabelTextField(EMAIL_LABEL, EMAIL_TEXTFIELD_TOOLTIP, inputContainer);
		this.company = RegistrationStep.createLinkedLabelTextField(COMPANY_LABEL, COMPANY_TEXTFIELD_TOOLTIP, inputContainer);

		new Label(inputContainer, SWT.NONE).setText(PROGRAMMING_EXPERIENCE_LABEL);

		this.programmingExperience = new Combo(inputContainer, SWT.NONE);
		this.programmingExperience.setItems(PROGRAMMING_EXPERIENCE_YEARS);
		this.programmingExperience.select(0);
		this.programmingExperience.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		new Label(inputContainer, SWT.NONE).setText(OPERATING_SYSTEM_LABEL);
		new Label(inputContainer, SWT.NONE).setText(System.getProperty("os.name"));
	}

	private boolean registerUser() {
		User user = new User();
		user.email = email.getText();
		user.organization = company.getText();
		user.programmingExperience = this.programmingExperience.getItem(this.programmingExperience.getSelectionIndex());
		user.operatingSystem = this.operatingSystem.getText();

		String userId;

		try {
			userId = new JsonTransferer().registerNewUser(user);
		} catch (ServerCommunicationException exception) {
			new Label(this.buttonContainer, SWT.NONE).setText(USER_CREATION_MESSAGE_FAILURE);

			RegistrationStep.createErrorMessageLabel(this.statusContainer, exception);

			return false;
		}

		Preferences preferences = Preferences.getInstance();
		preferences.setUserId(userId);
		preferences.setProgrammingExperience(user.programmingExperience);

		new Label(this.buttonContainer, SWT.NONE).setText(USER_CREATION_MESSAGE_SUCCESSFUL);

		new Label(this.statusContainer, SWT.NONE).setText(YOUR_USER_ID_LABEL);
		Text userIdField = new Text(this.statusContainer, SWT.NONE);
		userIdField.setText(userId);
		userIdField.setEditable(false);

		return true;
	}
}
