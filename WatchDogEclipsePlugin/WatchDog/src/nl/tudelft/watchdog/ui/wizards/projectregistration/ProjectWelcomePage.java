package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.wizards.WelcomePage;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * The first page of the {@link ProjectRegistrationWizard}. It asks the
 * question: do you have a Project ID, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
public class ProjectWelcomePage extends WelcomePage {

	/** Constructor. */
	public ProjectWelcomePage() {
		super("Welcome to WatchDog Project Registration!");
		setDescription("This wizard guides you through the setup of a WatchDog Project. ");
		labelQuestion = "Do you want to make a new registration of this Project? ";
		welcomeTitle = "Registering a new project takes just 2 minutes, and you are done. ";
		welcomeText = "";
		labelText = "Your WatchDog Project ID: ";
		inputToolTip = "The Project ID we sent you upon your Project registration.";
		currentRegistration = "Project";
		pageNumber = getWizard() instanceof ProjectRegistrationWizard ? 1 : 3;
	}

	@Override
	protected String getIconPath() {
		return "resources/images/project.png";
	}

	@Override
	public IWizardPage getPreviousPage() {
		return null;
	}
}