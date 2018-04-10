package nl.tudelft.watchdog.core.ui.wizards;

/**
 * Marker interface because {@link com.intellij.ide.wizard.AbstractWizard#updateButtons()} is protected and we need to call it from outside.
 * This interface should therefore be mixed in when extending {@link com.intellij.ide.wizard.AbstractWizard}.
 */
public interface RegistrationWizard {

    /**
     * Marker method. Call {@link com.intellij.ide.wizard.AbstractWizard#updateButtons()} with <code>super.updateButtons()</code>.
     */
    void updateButtons();
}
