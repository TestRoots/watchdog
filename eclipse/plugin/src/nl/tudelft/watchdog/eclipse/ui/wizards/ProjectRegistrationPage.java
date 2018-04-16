package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

public class ProjectRegistrationPage extends RegistrationStep {

	protected ProjectRegistrationPage(WizardDialog dialog) {
		super("Project registration", dialog);
	}

	@Override
	void createUserRegistrationIntroduction(Composite container) {
		Label header = new Label(container, SWT.NONE);
		header.setText("Now we have to create a new WatchDog project for this workspace");
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getIdInputPanel() {
		return (container, callback) -> new IdInputPanel(container, callback) {
            @Override
            String getIdLabelText() {
                return "The WatchDog project ID: ";
            }

            @Override
            String getIdTooltipText() {
                return "The WatchDog project ID associated with this workspace";
            }

            @Override
            String createUrlForId(String id) {
                return NetworkUtils.buildExistingProjectURL(id);
            }
        };
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getRegistrationPanel() {
		return ProjectRegistrationInputPanel::new;
	}

}
