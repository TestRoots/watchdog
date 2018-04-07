package nl.tudelft.watchdog.core.ui.wizards;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * The concept of a project comprises all information entered about the project
 * by the user.
 */
public class Project extends WatchDogItem {

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
