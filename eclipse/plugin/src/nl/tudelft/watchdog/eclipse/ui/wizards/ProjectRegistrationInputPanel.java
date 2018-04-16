package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationStep.YesNoDontknowButtonGroup;

public class ProjectRegistrationInputPanel extends Composite {

	private static final String PROJECT_NAME_TEXTFIELD_TOOLTIP = "The name of the project(s) you work on in this workspace.";
    private static final String PROJECT_WEBSITE_TEXTFIELD_TOOLTIP = "If you have a website, we'd love to see it here.";
    private static final String CI_USAGE_LABEL_TEXT = "Does your project use any Continuous Integration tools (Travis, Jenkins, etc.)?";
    private static final String CODE_STYLE_USAGE_LABEL_TEXT = "  ... enforce a uniform code style (e.g. whitespace)?";
    private static final String BUG_FINDING_USAGE_LABEL_TEXT = "  ... find functional bugs (e.g. NullPointerException)? ";
    private static final String OTHER_AUTOMATION_USAGE_LABEL_TEXT = "  ... other automation forms (e.g. license headers)? ";
    private static final String PROJECT_CREATION_MESSAGE_SUCCESSFUL = "Your WatchDog Project has successfully been created.";
    private static final String PROJECT_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog project.";

    private final Text projectName;
    private final Text projectWebsite;
    private final YesNoDontknowButtonGroup ciUsage;
    private final YesNoDontknowButtonGroup codeStyleUsage;
    private final YesNoDontknowButtonGroup bugFindingUsage;
    private final YesNoDontknowButtonGroup automationUsage;
    private final Composite buttonContainer;
    private Composite statusContainer;

    ProjectRegistrationInputPanel(Composite container, Consumer<Boolean> callback) {
		super(container, SWT.NONE);
		this.setLayout(new RowLayout(SWT.VERTICAL));
		
		Composite inputContainer = new Composite(this, SWT.NONE);
		inputContainer.setLayout(new GridLayout(2, false));

        this.projectName = RegistrationStep.createLinkedLabelTextField("Project name: ", PROJECT_NAME_TEXTFIELD_TOOLTIP, inputContainer);
        this.projectWebsite = RegistrationStep.createLinkedLabelTextField("Project website: ", PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, inputContainer);
        this.ciUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

        new Label(inputContainer, SWT.NONE).setText("Does your project use static analysis tools to...");
        // Filler to complete this grid row
        new Composite(inputContainer, SWT.NONE);

        this.codeStyleUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(CODE_STYLE_USAGE_LABEL_TEXT, inputContainer);
        this.bugFindingUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(BUG_FINDING_USAGE_LABEL_TEXT, inputContainer);
        this.automationUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(OTHER_AUTOMATION_USAGE_LABEL_TEXT, inputContainer);

        this.buttonContainer = new Composite(this, SWT.NONE);
        this.buttonContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
        
        Button createNewUserButton = new Button(this.buttonContainer, SWT.NONE);
        createNewUserButton.setText("Create new WatchDog project");
        
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
				
				callback.accept(registerProject());
			}
		});
    }

    private boolean registerProject() {
        Project project = new Project(Preferences.getInstance().getUserId());

        project.name = projectName.getText();
        project.website = projectWebsite.getText();
        project.usesContinuousIntegration = this.ciUsage.selected;
        project.usesCodeStyleSA = this.codeStyleUsage.selected;
        project.usesBugFindingSA = this.bugFindingUsage.selected;
        project.usesOtherAutomationSA = this.automationUsage.selected;

        String projectId;

        try {
            projectId = new JsonTransferer().registerNewProject(project);
        } catch (ServerCommunicationException exception) {
        	new Label(this.buttonContainer, SWT.NONE).setText(PROJECT_CREATION_MESSAGE_FAILURE);

            RegistrationStep.createErrorMessageLabel(this.statusContainer, exception);
        	
            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.registerProjectId(project.name, projectId);
        preferences.registerProjectUse(project.name, true);
        
        new Label(this.buttonContainer, SWT.NONE).setText(PROJECT_CREATION_MESSAGE_SUCCESSFUL);
        
        new Label(this.statusContainer, SWT.NONE).setText("Your Project ID is: ");
        Text projectIdField = new Text(this.statusContainer, SWT.NONE);
        projectIdField.setText(projectId);
        projectIdField.setEditable(false);

        return true;
    }

}
