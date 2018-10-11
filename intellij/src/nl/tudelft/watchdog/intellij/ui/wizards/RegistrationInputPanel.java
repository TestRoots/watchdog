package nl.tudelft.watchdog.intellij.ui.wizards;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.intellij.ui.wizards.UserRegistrationStep.ID_LENGTH;

/**
 * Base class for registration panels to create a WatchDog entity
 * and to inform the user of success or failure.
 */
abstract class RegistrationInputPanel extends JPanel {

	private final Consumer<Boolean> callback;
	private JButton createWatchDogUserButton;
	JPanel inputContainer;
	List<JComponent> dynamicWidgets;

	RegistrationInputPanel(Consumer<Boolean> callback) {
		this.callback = callback;
		this.dynamicWidgets = new ArrayList<>();
	}

	void createButtonAndStatusContainer(String buttonText) {
		this.createWatchDogUserButton = new JButton(buttonText);
		this.inputContainer.add(createWatchDogUserButton);
		this.inputContainer.add(Box.createHorizontalBox());

		createWatchDogUserButton.addActionListener(actionEvent -> {
			for (JComponent widget : this.dynamicWidgets) {
				widget.remove(this.inputContainer);
			}

			callback.accept(registerAction());
		});
	}

	void createFailureMessage(String message, Exception exception) {
		inputContainer.add(addToDynamic(new JLabel(message)));

		JPanel exceptionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
		this.add(addToDynamic(exceptionContainer));
		exceptionContainer.add(WizardStep.createErrorMessageLabel(exception));
   }

	void createSuccessIdOutput(String message, String label, String id) {
		inputContainer.add(addToDynamic(new JLabel(message)));
		inputContainer.add(addToDynamic(Box.createHorizontalBox()));

		inputContainer.add(addToDynamic(new JLabel(label)));

		JTextField userIdField = new JTextField(id, ID_LENGTH);
		userIdField.setEditable(false);
		inputContainer.add(addToDynamic(userIdField));

		this.createWatchDogUserButton.setEnabled(false);
	}

	private Component addToDynamic(JComponent component) {
		this.dynamicWidgets.add(component);
		return component;
	}

	abstract boolean registerAction();
}
