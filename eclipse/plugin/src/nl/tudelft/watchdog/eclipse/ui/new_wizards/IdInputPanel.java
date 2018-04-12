package nl.tudelft.watchdog.eclipse.ui.new_wizards;

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

import static nl.tudelft.watchdog.eclipse.ui.new_wizards.UserRegistrationPage.ID_LENGTH;

import java.util.function.Consumer;

abstract class IdInputPanel extends Composite {
	
	private static final String VERIFICATION_BUTTON_TEXT = "Verify";
    private static final String VERIFICATION_SUCCESSFUL_MESSAGE = "ID verification successful!";
    private static final String VERIFICATION_MESSAGE_FAILURE = "ID verification failed.";
	private Text textfield;
	private Composite statusContainer;

	IdInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, SWT.NONE);
		this.setLayout(new GridLayout(1, false));
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite fieldContainer = new Composite(this, SWT.NONE);
		fieldContainer.setLayout(new GridLayout(2, false));
		fieldContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.textfield = UIUtils.createLinkedFieldInput(this.getIdLabelText(), this.getIdTooltipText(), fieldContainer);
		textfield.setTextLimit(ID_LENGTH);
		
		Button verify = new Button(this, SWT.FLAT);
		verify.setText(VERIFICATION_BUTTON_TEXT);
		verify.setEnabled(false);
		
		textfield.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verify.setEnabled(textfield.getText().length() == ID_LENGTH);
			}
		});
		
		statusContainer = new Composite(this, SWT.NONE);
		statusContainer.setLayout(new GridLayout(1, false));
		statusContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
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
	
	private boolean verifyUserIdRegistration() {
        try {
            NetworkUtils.getURLAndGetResponse(createUrlForId(this.textfield.getText()));
        } catch (ServerCommunicationException exception) {
            new Label(statusContainer, SWT.NONE).setText(VERIFICATION_MESSAGE_FAILURE);
            RegistrationStep.createErrorMessageLabel(statusContainer, exception);

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.setUserId(this.textfield.getText());

        new Label(statusContainer, SWT.NONE).setText(VERIFICATION_SUCCESSFUL_MESSAGE);
        return true;
    }

}
