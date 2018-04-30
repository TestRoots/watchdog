package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;

class ProjectRegistrationPage extends RegistrationStep {

	protected ProjectRegistrationPage(WizardDialog dialog) {
		super(PROJECT_REGISTRATION_TITLE, dialog);
	}

	@Override
	void createUserRegistrationIntroduction(Composite container) {
		Label header = new Label(container, SWT.NONE);
		header.setText(BEFORE_PROJECT_REGISTRATION);
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getIdInputPanel() {
		return (container, callback) -> new IdInputPanel(container, callback) {
            @Override
            String getIdTooltipText() {
                return PROJECT_ID_TOOLTIP;
            }
            
            @Override
            String getIdLabelText() {
                return PROJECT_ID_LABEL;
            }

            @Override
            String createUrlForId(String id) {
                return NetworkUtils.buildExistingProjectURL(id);
            }

			@Override
			void storeIdInPreferences(Preferences preferences, String id) {
				preferences.registerProjectId(WatchDogUtils.getWorkspaceName(), id);
				preferences.registerProjectUse(WatchDogUtils.getWorkspaceName(), true);
			}
        };
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getRegistrationPanel() {
		return ProjectRegistrationInputPanel::new;
	}

	@Override
	String getRegistrationType() {
		return PROJECT;
	}

}
