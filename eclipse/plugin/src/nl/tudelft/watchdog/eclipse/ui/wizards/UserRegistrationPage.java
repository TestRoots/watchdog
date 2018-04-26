package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

class UserRegistrationPage extends RegistrationStep {

	/**
	 * The length (in characters) of the WatchDog id.
	 */
	static final int ID_LENGTH = 40;

	protected UserRegistrationPage(WizardDialog dialog) {
		super("User registration", dialog);
	}

	@Override
	void createUserRegistrationIntroduction(Composite container) {
		Label header = new Label(container, SWT.NONE);
		header.setText("Before we start, we first have to have a WatchDog user registration");
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getIdInputPanel() {
		return (container, callback) ->	new IdInputPanel(container, callback) {

			@Override
			String getIdTooltipText() {
				return "The User-ID we sent you upon your first WatchDog registration";
			}

			@Override
			String getIdLabelText() {
				return "Your WatchDog User-ID: ";
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
		return "user";
	}

}
