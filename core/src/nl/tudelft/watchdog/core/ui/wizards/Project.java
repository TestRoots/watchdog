package nl.tudelft.watchdog.core.ui.wizards;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * The concept of a project comprises all information entered about the project
 * by the user.
 */
public class Project extends WatchDogItem {

    public static final String PROJECT_NAME_TEXTFIELD_TOOLTIP = "The name of the project(s) you work on in this workspace.";
    public static final String PROJECT_WEBSITE_TEXTFIELD_TOOLTIP = "If you have a website, we'd love to see it here.";
    public static final String CI_USAGE_LABEL_TEXT = "Does your project use any Continuous Integration tools (Travis, Jenkins, etc.)?";
    public static final String CODE_STYLE_USAGE_LABEL_TEXT = "  ... enforce a uniform code style (e.g. whitespace)?";
    public static final String BUG_FINDING_USAGE_LABEL_TEXT = "  ... find functional bugs (e.g. NullPointerException)? ";
    public static final String OTHER_AUTOMATION_USAGE_LABEL_TEXT = "  ... other automation forms (e.g. license headers)? ";
    public static final String PROJECT_CREATION_MESSAGE_SUCCESSFUL = "Your WatchDog Project has successfully been created.";
    public static final String PROJECT_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog project.";
    public static final String TOOL_USAGE_LABEL_TEXT = "Please provide the names of the static analysis tools you use in the project: ";
    public static final String TOOL_USAGE_TEXTFIELD_TOOLTIP = "Please provide the names of the tools, for example CheckStyle or PMD";

    /** Constructor. */
	public Project(String userId) {
		localRegistrationDate = new Date();
		this.userId = userId;
	}

	/** eMail. */
	public String name;

	/**
	 * Does the registered WatchDog project belong to a single software project
	 * (<code>true</code> if it does)
	 */
	public boolean belongToASingleSoftware;
	
	/** Do you use ContinuousIntegration? */
	public YesNoDontKnowChoice usesContinuousIntegration;

	/** Do you use Junit? */
	public YesNoDontKnowChoice usesJunit;

	/** Do you use other frameworks than Junit? */
	public YesNoDontKnowChoice usesOtherTestingFrameworks;

	/** Do you use other testing strategies than Unit testing? */
	public YesNoDontKnowChoice usesOtherTestingForms;
	
	public YesNoDontKnowChoice usesCodeStyleSA;

	public YesNoDontKnowChoice usesBugFindingSA;

	public YesNoDontKnowChoice usesOtherAutomationSA;

    public String usesToolsSA;

	/** The percentage of how much production code is done (0% - 100%). */
	public int productionPercentage;

	/** Is Junit used only for pure true-to-the-sense unit testing? */
	public YesNoDontKnowChoice useJunitOnlyForUnitTesting;

	/** Do you follow TDD? */
	public YesNoDontKnowChoice followTestDrivenDesign;

	/** The registration date. */
	public Date localRegistrationDate;

	/** The user who registers this project. */
	public String userId;

	/** The project's website. */
	public String website;

}
