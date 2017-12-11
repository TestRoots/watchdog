package nl.tudelft.watchdog.intellij.ui.wizards.projectregistration;

import com.intellij.openapi.util.IconLoader;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.intellij.ui.wizards.WelcomeStepBase;

import javax.swing.*;

/**
 * The first page of the {@link ProjectRegistrationWizard}. It asks the
 * question: do you have a Project ID, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
public class ProjectWelcomeStep extends WelcomeStepBase {

	/** Constructor. */
	public ProjectWelcomeStep(int pageNumber, RegistrationWizardBase wizard) {
		super("Welcome to WatchDog Project Registration!", pageNumber, wizard);
        myIcon = IconLoader.getIcon("/images/project.png");
		descriptionText ="This wizard guides you through the setup of a WatchDog Project. ";
		labelQuestion = "Do you want to make a new registration of this Project? ";
		welcomeDisplay = "Registering a new project takes just 2 minutes, and you are done. ";
		welcomeText = "";
		labelText = "Your WatchDog Project ID: ";
		inputToolTip = "The Project ID we sent you upon your Project registration.";
		currentRegistration = "Project";
	}

	@Override
	protected String getIconPath() {
		return "/images/project.png";
	}

    @Override
    protected void commit(CommitType commitType) {

    }

    @Override
    public void _init() {
        super._init();
        JPanel oneColumn = UIUtils.createVerticalBoxJPanel(topPanel);
        createHeader(oneColumn);

        createQuestionJPanel(oneColumn);

        setComplete(false);

    }
}
