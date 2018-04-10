package nl.tudelft.watchdog.core.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.WizardUIElements.DEFAULT_SPACING;
import static nl.tudelft.watchdog.core.ui.wizards.WizardUIElements.ID_LENGTH;

public abstract class IdInputPanel extends JPanel {

	private static final long serialVersionUID = -6605470210561694539L;
	private static final String VERIFICATION_BUTTON_TEXT = "Verify";
    private static final String VERIFICATION_SUCCESSFUL_MESSAGE = "ID verification successful!";
    private static final String VERIFICATION_MESSAGE_FAILURE = "ID verification failed.";

    private final JTextField textfield;
    private final JPanel statusContainer;

    protected IdInputPanel(Consumer<Boolean> callback) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel fieldContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(fieldContainer);

        this.textfield = WizardUIElements.createLinkedLabelTextField(this.getIdLabelText(), getIdTooltipText(), ID_LENGTH, fieldContainer);

        JButton verify = new JButton(VERIFICATION_BUTTON_TEXT);
        verify.setEnabled(false);
        verify.setContentAreaFilled(true);
        verify.setOpaque(true);
        fieldContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
        fieldContainer.add(verify);

        textfield.setDocument(new JTextFieldLimit(ID_LENGTH));
        textfield.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                textChanged(documentEvent);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                textChanged(documentEvent);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                textChanged(documentEvent);
            }
            private void textChanged(DocumentEvent e) {
                verify.setEnabled(e.getDocument().getLength() == ID_LENGTH);
                callback.accept(false);
            }
        });

        this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(statusContainer);

        verify.addActionListener(actionEvent -> {
            this.statusContainer.removeAll();
            callback.accept(verifyUserIdRegistration());
        });
    }

    protected abstract String getIdLabelText();

    protected abstract String getIdTooltipText();

    protected abstract String createUrlForId(String id);

    private boolean verifyUserIdRegistration() {
        try {
            NetworkUtils.getURLAndGetResponse(createUrlForId(this.textfield.getText()));
        } catch (ServerCommunicationException exception) {
            this.statusContainer.add(new JLabel(VERIFICATION_MESSAGE_FAILURE));
            this.statusContainer.add(WizardUIElements.createErrorMessageLabel(exception));

            return false;
        }

        PreferencesBase preferences = WatchDogGlobals.getPreferences();
        preferences.setUserId(this.textfield.getText());

        this.statusContainer.add(new JLabel(VERIFICATION_SUCCESSFUL_MESSAGE));
        return true;
    }
}
