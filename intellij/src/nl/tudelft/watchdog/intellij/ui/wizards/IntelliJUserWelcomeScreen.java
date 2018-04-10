package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.openapi.util.IconLoader;
import nl.tudelft.watchdog.core.ui.wizards.UserWelcomePanel;

import javax.swing.*;
import java.awt.*;

class IntelliJUserWelcomeScreen extends WizardStep {
    @Override
    void _initWithPanel(Container panel) {
        panel.add(new UserWelcomePanel() {
            @Override
            protected Component createLogo(String iconLocation) {
                JLabel labelLogo = new JLabel();

                labelLogo.setIcon(IconLoader.getIcon(iconLocation));
                labelLogo.setHorizontalAlignment(JLabel.CENTER);

                return labelLogo;
            }
        });
    }

    @Override
    boolean isFinishedWithStep() {
        return true;
    }
}
