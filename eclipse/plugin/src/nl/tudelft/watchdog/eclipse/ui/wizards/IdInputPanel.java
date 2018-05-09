package nl.tudelft.watchdog.eclipse.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.eclipse.ui.wizards.UserRegistrationPage.ID_LENGTH;

import java.util.function.Consumer;

/**
 * Input panel that has an input field for a WatchDog ID and can
 * verify that the id exists after filling in.
 */
abstract class IdInputPanel extends Composite {

	private Text textfield;
	private Composite statusContainer;

	/**
	 * Create the input panel.
	 * @param parent The parent container.
	 * @param callback The callback invoked after the user clicked "Verify".
	 */
	IdInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, SWT.NONE);
		this.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		Composite fieldContainer = new Composite(this, SWT.NONE);
		fieldContainer.setLayout(new GridLayout(2, false));

		this.textfield = UIUtils.createLinkedFieldInput(this.getIdLabelText(), this.getIdTooltipText(), fieldContainer);
		textfield.setSize(250, SWT.DEFAULT);
		textfield.setTextLimit(ID_LENGTH);
		textfield.setLayoutData(new GridData(350, SWT.DEFAULT));

		Button verify = new Button(fieldContainer, SWT.FLAT);
		verify.setText(VERIFICATION_BUTTON_TEXT);
		verify.setEnabled(false);
		verify.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		textfield.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verify.setEnabled(textfield.getText().length() == ID_LENGTH);
			}
		});

		statusContainer = new Composite(this, SWT.NONE);
		statusContainer.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		verify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control control : statusContainer.getChildren()) {
					control.dispose();
				}
				callback.accept(verifyUserIdRegistration());

				statusContainer.layout(true, true);
				parent.layout(true, true);

				parent.redraw();
				parent.update();
			}
		});
	}

	abstract String getIdTooltipText();

	abstract String getIdLabelText();

	abstract String createUrlForId(String text);

	abstract void storeIdInPreferences(Preferences preferences, String id);

	private boolean verifyUserIdRegistration() {
		try {
			NetworkUtils.getURLAndGetResponse(createUrlForId(this.textfield.getText()));
		} catch (ServerCommunicationException exception) {
			new Label(statusContainer, SWT.NONE).setText(VERIFICATION_MESSAGE_FAILURE);
			RegistrationStep.createErrorMessageLabel(statusContainer, exception);

			return false;
		}

		this.storeIdInPreferences(Preferences.getInstance(), this.textfield.getText());

		new Label(statusContainer, SWT.NONE).setText(VERIFICATION_SUCCESSFUL_MESSAGE);
		return true;
	}

}
