package nl.tudelft.watchdog.intellij.ui.new_wizards;

import com.intellij.ide.wizard.Step;
import com.intellij.openapi.util.IconLoader;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class WizardStep implements Step {

    static final int DEFAULT_SPACING = 5;

    private JComponent panel;

    @Override
    public void _init() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this._initWithPanel(panel);
    }

    @Override
    public void _commit(boolean b) {

    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @NotNull
    JLabel createLogo(String iconPath) {
        JLabel tudelftLogo = new JLabel();
        tudelftLogo.setIcon(IconLoader.getIcon(iconPath));
        tudelftLogo.setHorizontalAlignment(JLabel.CENTER);
        return tudelftLogo;
    }

    abstract void _initWithPanel(Container panel);

    abstract boolean isFinishedWithStep();

    static JTextField createLinkedLabelTextField(String labelText, String tooltip, int textFieldLength, Container container) {
        JLabel label = new JLabel(labelText);
        label.setToolTipText(tooltip);
        container.add(label);

        JTextField textfield = new JTextField(textFieldLength);
        textfield.setToolTipText(tooltip);
        container.add(textfield);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                textfield.grabFocus();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                textfield.grabFocus();
            }
        });
        return textfield;
    }

    static Component createErrorMessageLabel(Exception exception) {
        return new JLabel("<html>" +
                exception.getMessage() +
                "<br>Are you connected to the internet, and is port 80 open?" +
                "<br>Please contact us via <a href=\"https://www.testroots.org\">our website</a>. We can help troubleshoot the issue!");
    }
}
