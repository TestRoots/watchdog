package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.wizard.Step;

import javax.swing.*;
import java.awt.*;

public abstract class WizardStep implements Step {

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

    abstract void _initWithPanel(Container panel);

    abstract boolean isFinishedWithStep();
}
