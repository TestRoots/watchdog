package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.Consumer;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

public class UserRegistrationInputPanel extends Composite {

	private static final String EMAIL_TEXTFIELD_TOOLTIP = "We will use this e-mail address for future communication (if any).";
    private static final String COMPANY_TEXTFIELD_TOOLTIP = "You can include the website or name of your organisation here.";
    private static final String USER_CREATION_MESSAGE_SUCCESSFUL = "Your WatchDog User has successfully been created.";
    private static final String USER_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog user.";

    private final Text email;
    private final Text company;
    private final Combo programmingExperience;
    private final Text operatingSystem;
    private final Composite buttonContainer;
    private Composite statusContainer;

    UserRegistrationInputPanel(Composite parent, Consumer<Boolean> callback) {
		super(parent, SWT.NONE);
		this.setLayout(new RowLayout(SWT.VERTICAL));
		
		Label header = new Label(this, SWT.NONE);
		header.setText("WatchDog user registration");
		header.setFont(JFaceResources.getFontRegistry().getBold(""));
		
		new Label(this, SWT.NONE).setText("Please fill in the following data to create a WatchDog user account for you.");
		new Label(this, SWT.NONE).setText("The input is optional, but greatly appreciated to improve the quality of our research data.");
		
		Composite inputContainer = new Composite(this, SWT.NONE);
		inputContainer.setLayout(new GridLayout(2, true));
		
		this.email = RegistrationStep.createLinkedLabelTextField("Your e-mail: ", EMAIL_TEXTFIELD_TOOLTIP, inputContainer);
		this.company = RegistrationStep.createLinkedLabelTextField("Your Organisation/Company: ", COMPANY_TEXTFIELD_TOOLTIP, inputContainer);
		
		new Label(inputContainer, SWT.NONE).setText("Your programming experience: ");
		
		this.programmingExperience = new Combo(inputContainer, SWT.NONE);
		this.programmingExperience.setItems("N/A", "< 1 year", "1-2 years", "3-6 years", "7-10 years", "> 10 years");
		this.programmingExperience.select(0);
		this.programmingExperience.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        this.operatingSystem = RegistrationStep.createLinkedLabelTextField("Your operating system: ", COMPANY_TEXTFIELD_TOOLTIP, inputContainer);
        this.operatingSystem.setText(System.getProperty("os.name"));
        this.operatingSystem.setEditable(false);
        this.operatingSystem.setEnabled(false);
        
        this.buttonContainer = new Composite(this, SWT.NONE);
        this.buttonContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
        
        Button createNewUserButton = new Button(this.buttonContainer, SWT.NONE);
        createNewUserButton.setText("Create new WatchDog user");
        
        this.statusContainer = new Composite(this, SWT.NONE);
        this.statusContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
        
        createNewUserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control child : statusContainer.getChildren()) {
					child.dispose();
				}
				
				for (Control child : buttonContainer.getChildren()) {
					if (child != createNewUserButton) {
						child.dispose();
					}
				}
				
				callback.accept(registerUser());
			}
		});
    }

    private boolean registerUser() {
        User user = new User();
        user.email = email.getText();
        user.organization = company.getText();
        user.programmingExperience = this.programmingExperience.getItem(this.programmingExperience.getSelectionIndex());
        user.operatingSystem = this.operatingSystem.getText();

        String userId;

        try {
            userId = new JsonTransferer().registerNewUser(user);
        } catch (ServerCommunicationException exception) {
        	new Label(this.buttonContainer, SWT.NONE).setText(USER_CREATION_MESSAGE_FAILURE);

            RegistrationStep.createErrorMessageLabel(this.statusContainer, exception);

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.setUserId(userId);
        preferences.setProgrammingExperience(user.programmingExperience);
        
        new Label(this.buttonContainer, SWT.NONE).setText(USER_CREATION_MESSAGE_SUCCESSFUL);
        
        new Label(this.statusContainer, SWT.NONE).setText("Your User ID is: ");
        Text userIdField = new Text(this.statusContainer, SWT.NONE);
        userIdField.setText(userId);
        userIdField.setEditable(false);
        
        return true;
    }
}
