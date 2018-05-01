package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

import static nl.tudelft.watchdog.core.ui.wizards.User.*;

class UserRegistrationPage extends RegistrationStep {

	/**
	 * The length (in characters) of the WatchDog id.
	 */
	static final int ID_LENGTH = 40;

	protected UserRegistrationPage(WizardDialog dialog) {
		super(USER_REGISTRATION_TITLE, dialog);
	}

	@Override
	void createUserRegistrationIntroduction(Composite container) {
		Label header = new Label(container, SWT.NONE);
		header.setText(BEFORE_USER_REGISTRATION);
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getIdInputPanel() {
		return (container, callback) ->	new IdInputPanel(container, callback) {

			@Override
			String getIdTooltipText() {
				return USER_ID_TOOLTIP;
			}

			@Override
			String getIdLabelText() {
				return USER_ID_LABEL;
			}

			@Override
			String createUrlForId(String id) {
				return NetworkUtils.buildExistingUserURL(id);
			}

			@Override
			void storeIdInPreferences(Preferences preferences, String id) {
				preferences.setUserId(id);
			}

		};
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getRegistrationPanel() {
		return UserRegistrationInputPanel::new;
	}

	@Override
	String getRegistrationType() {
		return USER;
	}

	@Override
	int getShellLayoutHeight() {
		return 500;
	}

}
