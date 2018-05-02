package nl.tudelft.watchdog.intellij.ui.wizards;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.intellij.ui.wizards.UserRegistrationStep.ID_LENGTH;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

/**
 * Base class for registration panels to create a WatchDog entity
 * and to inform the user of success or failure.
 */
abstract class RegistrationInputPanel extends JPanel {

	private final Consumer<Boolean> callback;
	private JPanel buttonContainer;
	private JPanel statusContainer;
	private JButton createWatchDogUserButton;

	RegistrationInputPanel(Consumer<Boolean> callback) {
		this.callback = callback;
	}

	void createButtonAndStatusContainer(String buttonText) {
		this.buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
		this.add(buttonContainer);

		this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
		this.add(statusContainer);

		this.createWatchDogUserButton = new JButton(buttonText);
		this.buttonContainer.add(createWatchDogUserButton);
		createWatchDogUserButton.addActionListener(actionEvent -> {
			this.statusContainer.removeAll();

			this.buttonContainer.removeAll();
			this.buttonContainer.add(createWatchDogUserButton);

			callback.accept(registerAction());
		});
	}

	void createFailureMessage(String message, Exception exception) {
		buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
		buttonContainer.add(new JLabel(message));

		statusContainer.add(WizardStep.createErrorMessageLabel(exception));
   }

	void createSuccessIdOutput(String message, String label, String id) {
		buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
		buttonContainer.add(new JLabel(message));

		statusContainer.add(new JLabel(label));

		JTextField userIdField = new JTextField(id, ID_LENGTH);
		userIdField.setEditable(false);
		statusContainer.add(userIdField);

		this.createWatchDogUserButton.setEnabled(false);
	}

	abstract boolean registerAction();
}
