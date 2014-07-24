package nl.tudelft.watchdog.logic;

import java.util.Date;

/**
 * The concept of a project comprises all information entered about the project
 * by the user.
 */
public class Project {

	/** Constructor. */
	public Project() {
		registrationDate = new Date();
	}

	/** eMail. */
	public String name;

	/** Organization. */
	public String role;

	/** User who registered the projects. */
	public String user;

	/**
	 * Does the registered WatchDog project belong to a single software project
	 * (<code>true</code> if it does)
	 */
	public boolean belongToASingleSofware;

	/** Do you use Junit? */
	public YesNoDontKnowChoice usesJunit;

	/** Do you use other frameworks than Junit? */
	public YesNoDontKnowChoice usesOtherFrameworks;

	/** The percentage of how much production code is done (0% - 100%). */
	public int productionPercentage;

	/** Is Junit used only for pure true-to-the-sense unit testing? */
	public YesNoDontKnowChoice useJunitOnlyForUnitTesting;

	/** Do you follow TDD? */
	public YesNoDontKnowChoice followTestDrivenDesign;

	/** Group. */
	public String group;

	/** The registration date. */
	public Date registrationDate;

}
