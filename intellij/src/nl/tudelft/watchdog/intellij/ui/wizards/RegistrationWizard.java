package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.wizard.AbstractWizard;

/**
 * Marker interface because {@link AbstractWizard#updateButtons()} is protected and we need to call it from outside.
 * This interface should therefore be mixed in when extending {@link AbstractWizard}.
 */
public interface RegistrationWizard {

    /**
     * Marker method. Call {@link AbstractWizard#updateButtons()} with <code>super.updateButtons()</code>.
     */
    void updateButtons();
}
