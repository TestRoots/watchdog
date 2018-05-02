package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Base class for registration panels to create a WatchDog entity
 * and to inform the user of success or failure.
 */
abstract class RegistrationInputPanel extends Composite {

	private Button createNewUserButton;
	private Composite statusContainer;
	private Composite buttonContainer;
	private Consumer<Boolean> callback;

	public RegistrationInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, SWT.NONE);
		this.callback = callback;
	}

	void createButtonAndStatusContainer(String buttonText) {
		this.buttonContainer = new Composite(this, SWT.NONE);
		this.buttonContainer.setLayout(RegistrationStep.createRowLayout(SWT.HORIZONTAL));

		this.createNewUserButton = new Button(buttonContainer, SWT.NONE);
		this.createNewUserButton.setText(buttonText);

		this.statusContainer = new Composite(this, SWT.NONE);
		this.statusContainer.setLayout(RegistrationStep.createRowLayout(SWT.HORIZONTAL));

		this.createNewUserButton.addSelectionListener(new SelectionAdapter() {
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

				callback.accept(registerAction());
			}
		});
	}

	void createFailureMessage(String message, Exception exception) {
		new Label(this.buttonContainer, SWT.NONE).setText(message);

		RegistrationStep.createErrorMessageLabel(this.statusContainer, exception);
	}

	void createSuccessIdOutput(String message, String label, String id) {
		new Label(this.buttonContainer, SWT.NONE).setText(message);
		this.createNewUserButton.setEnabled(false);

		new Label(this.statusContainer, SWT.NONE).setText(label);
		Text userIdField = new Text(this.statusContainer, SWT.NONE);
		userIdField.setText(id);
		userIdField.setEditable(false);
	}

	abstract boolean registerAction();
}
