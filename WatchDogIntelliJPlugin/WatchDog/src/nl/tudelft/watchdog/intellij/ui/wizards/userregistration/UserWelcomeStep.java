package nl.tudelft.watchdog.intellij.ui.wizards.userregistration;

import com.intellij.openapi.util.IconLoader;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.intellij.ui.wizards.WelcomeStepBase;

import javax.swing.*;

/**
 * The first page of the {@link UserProjectRegistrationWizard}. It asks the
 * question: Are you a new WatchDog user, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
public class UserWelcomeStep extends WelcomeStepBase {

	/** Constructor. */
    public UserWelcomeStep(int stepNumber, RegistrationWizardBase wizard) {
		super("Welcome to WatchDog!", stepNumber, wizard);
        myIcon = IconLoader.getIcon("/images/user.png");
		descriptionText = "Please register your project with WatchDog in order to start collecting data.";
		welcomeDisplay = "Welcome!";
		welcomeText = "WatchDog is a free, open-source plugin that tells how you code your software.";
		labelText = "Your WatchDog User-ID: ";
		inputToolTip = "The User-ID we sent you upon your first WatchDog registration.";
		labelQuestion = "Do you want to register a new user? ";
		currentRegistration = "User";
	}

	@Override
	protected String getIconPath() {
		return "/images/user.png";
	}


    private void createWatchDogDescription(JPanel parent) {
		JPanel panel = UIUtils.createGridedJPanel(parent, 1);
		UIUtils.createBoldLabel(panel, "WatchDog is a free, open-source plugin that tells how you code your software.");
        String htmlText = "<html>It measures how you write Java code and tests. We never do anything bad with <a href=\"http://www.testroots.org/testroots_watchdog.html#details\">your purely numerical data</a>.<br>You can win <a href=\"http://www.testroots.org/testroots_watchdog.html#prizes\">amazing prizes</a>, a <a href=\"http://www.testroots.org/reports/sample_watchdog_report.pdf\">detailed report</a> on your development behaviour and our eternal gratitude! :-)<br></html>";
        UIUtils.createHtmlTextWithLinks(panel, htmlText);
    	UIUtils.createLabel(panel,"");
        return;
	}

    @Override
    public void commit(CommitType commitType) {

    }

    @Override
    public void _init() {
        super._init();
        JPanel oneColumn = UIUtils.createVerticalBoxJPanel(topPanel);
        createHeader(oneColumn);
        createWatchDogDescription(oneColumn);
        createRegistrationInfoLabel(oneColumn);
        createLogoRow(oneColumn);
        setComplete(true);
    }

    private void createRegistrationInfoLabel(JPanel parent) {
        JPanel panel = UIUtils.createGridedJPanel(parent, 1);
        UIUtils.createBoldLabel(panel, "By clicking next, an anonymous registration will be performed for you and your project.");
        UIUtils.createLabel(panel, "");
    }
}
