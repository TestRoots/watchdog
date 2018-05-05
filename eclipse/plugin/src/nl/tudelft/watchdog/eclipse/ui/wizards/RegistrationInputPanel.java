package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
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
	private Consumer<Boolean> callback;
	private List<Control> dynamicWidgets;
	/**
	 * This container is not inserted by the constructor, because
	 * the position inside the panel matters. Therefore, subclasses
	 * MUST initialize this container themselves in the appropriate
	 * location.
	 */
	Composite inputContainer;

	public RegistrationInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, SWT.NONE);
		this.callback = callback;
		this.dynamicWidgets = new ArrayList<>();
	}

	void createButtonAndStatusContainer(String buttonText) {
		this.createNewUserButton = new Button(inputContainer, SWT.NONE);
		this.createNewUserButton.setText(buttonText);
		this.createNewUserButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Fill up the second column in the grid, next to the button
		new Label(inputContainer, SWT.NONE);

		this.createNewUserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control child : dynamicWidgets) {
					child.dispose();
				}
				dynamicWidgets.clear();

				callback.accept(registerAction());
			}
		});
	}

	void createFailureMessage(String message, Exception exception) {
		Label errorMessage = new Label(this.inputContainer, SWT.NONE);
		errorMessage.setText(message);
		this.dynamicWidgets.add(errorMessage);

		Composite exceptionContainer = new Composite(this, SWT.NONE);
		exceptionContainer.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));
		this.dynamicWidgets.add(exceptionContainer);

		RegistrationStep.createErrorMessageLabel(exceptionContainer, exception);
	}

	void createSuccessIdOutput(String message, String label, String id) {
		Label successMessage = new Label(this.inputContainer, SWT.NONE);
		successMessage.setText(message);
		this.dynamicWidgets.add(successMessage);

		this.dynamicWidgets.add(new Label(this.inputContainer, SWT.NONE));

		this.createNewUserButton.setEnabled(false);

		Label idLabelComponent = new Label(this.inputContainer, SWT.NONE);
		idLabelComponent.setText(label);
		this.dynamicWidgets.add(idLabelComponent);

		Text userIdField = new Text(this.inputContainer, SWT.NONE);
		userIdField.setText(id);
		userIdField.setEditable(false);
		userIdField.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		this.dynamicWidgets.add(userIdField);
	}

	abstract boolean registerAction();
}
