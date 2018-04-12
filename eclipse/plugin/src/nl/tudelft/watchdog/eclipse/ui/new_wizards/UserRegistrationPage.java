package nl.tudelft.watchdog.eclipse.ui.new_wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

public class UserRegistrationPage extends RegistrationStep {

	/**
     * The length (in characters) of the WatchDog id.
     */
    static final int ID_LENGTH = 40;
	
	protected UserRegistrationPage() {
		super("User registration");
	}

	@Override
	void createUserRegistrationIntroduction(Composite container) {
		Label header = new Label(container, SWT.NONE);
		header.setText("Before we start, we first have to have a WatchDog user registration");
	}
	
	@Override
	BiConsumer<Composite, Consumer<Boolean>> getIdInputPanel() {
		return (container, callback) ->	new IdInputPanel(container ,callback) {

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
			
		};
	}

	@Override
	BiConsumer<Composite, Consumer<Boolean>> getRegistrationPanel() {
		return (container, callback) -> {
			Text text = new Text(container, SWT.NONE);
			text.setText("user input");
		};
	}

	

}
