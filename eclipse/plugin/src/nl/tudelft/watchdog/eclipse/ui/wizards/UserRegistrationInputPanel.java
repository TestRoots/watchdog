package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

import static nl.tudelft.watchdog.core.ui.wizards.User.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.eclipse.ui.util.UIUtils.HEADER_FONT;

class UserRegistrationInputPanel extends RegistrationInputPanel {

	private Text email;
	private Text company;
	private Combo programmingExperience;

	/**
	 * A panel to ask the user questions to create their user profile.
	 * @param container The parent container.
	 * @param callback The callback invoked after the user clicked "Create WatchDog User".
	 */
	UserRegistrationInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, callback);
		this.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		Label header = new Label(this, SWT.NONE);
		header.setText(WATCHDOG_USER_PROFILE);
		header.setFont(HEADER_FONT);

		new Label(this, SWT.NONE).setText(USER_DATA_REQUEST);
		new Label(this, SWT.NONE).setText(INPUT_IS_OPTIONAL);

		this.createInputFields();

		this.createButtonAndStatusContainer(CREATE_USER_BUTTON_TEXT);
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

	@Override
	boolean registerAction() {
		User user = new User();
		user.email = email.getText();
		user.organization = company.getText();
		user.programmingExperience = this.programmingExperience.getItem(this.programmingExperience.getSelectionIndex());
		user.operatingSystem = System.getProperty("os.name");

		String userId;

		try {
			userId = new JsonTransferer().registerNewUser(user);
		} catch (ServerCommunicationException exception) {
			this.createFailureMessage(USER_CREATION_MESSAGE_FAILURE, exception);

			return false;
		}

		Preferences preferences = Preferences.getInstance();
		preferences.setUserId(userId);
		preferences.setProgrammingExperience(user.programmingExperience);

		this.createSuccessIdOutput(USER_CREATION_MESSAGE_SUCCESSFUL, YOUR_USER_ID_LABEL, userId);

		return true;
	}
}
