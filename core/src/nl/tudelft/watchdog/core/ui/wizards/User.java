package nl.tudelft.watchdog.core.ui.wizards;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * The concept of a user comprises all information about the user who runs this
 * WatchDog instance.
 */
public class User extends WatchDogItem {

	public static final String EMAIL_TEXTFIELD_TOOLTIP = "We will use this e-mail address for future communication (if any).";
	public static final String COMPANY_TEXTFIELD_TOOLTIP = "You can include the website or name of your organisation here.";
	public static final String OPERATING_SYSTEM_TOOLTIP = "The operating system you are using.";
	public static final String USER_CREATION_MESSAGE_SUCCESSFUL = "Your WatchDog User has successfully been created.";
	public static final String USER_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog user.";

	/** Constructor. */
	public User() {
		localRegistrationDate = new Date();
	}

	/** eMail. */
	public String email;

	/** Organization. */
	public String organization;

	/** The programming experience. */
	public String programmingExperience;

	/**
	 * Whether the user participates in the lottery, and whether we may contact
	 * him.
	 */
	public boolean mayContactUser = false;

	/** The registration date. */
	public Date localRegistrationDate;

	/** The operating system. */
	public String operatingSystem;
}
