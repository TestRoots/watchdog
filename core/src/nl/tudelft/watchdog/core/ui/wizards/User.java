package nl.tudelft.watchdog.core.ui.wizards;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * The concept of a user comprises all information about the user who runs this
 * WatchDog instance.
 */
public class User extends WatchDogItem {

	public static final String USER = "user";
	public static final String USER_REGISTRATION_TITLE = "User registration";
	public static final String EMAIL_TEXTFIELD_TOOLTIP = "We will use this e-mail address for future communication (if any).";
	public static final String EMAIL_LABEL = "Your e-mail: ";
	public static final String COMPANY_TEXTFIELD_TOOLTIP = "You can include the website or name of your organisation here.";
	public static final String COMPANY_LABEL = "Your Organisation/Company: ";
	public static final String PROGRAMMING_EXPERIENCE_LABEL = "Your programming experience: ";
	public static final String[] PROGRAMMING_EXPERIENCE_YEARS = new String[] {"N/A", "< 1 year", "1-2 years", "3-6 years", "7-10 years", "> 10 years"};
	public static final String OPERATING_SYSTEM_LABEL = "Your operating system: ";
	public static final String USER_CREATION_MESSAGE_SUCCESSFUL = "Your WatchDog User has successfully been created.";
	public static final String USER_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog user.";
	public static final String BEFORE_USER_REGISTRATION = "Before we start, we first have to have a WatchDog user registration.";
	public static final String USER_ID_TOOLTIP = "The User-ID we sent you upon your first WatchDog registration.";
	public static final String USER_ID_LABEL = "Your WatchDog User-ID: ";
	
	public static final String WATCHDOG_USER_PROFILE = "WatchDog user profile";
	public static final String USER_DATA_REQUEST = "Please fill in the following data to create a WatchDog user account for you.";
	public static final String CREATE_USER_BUTTON_TEXT = "Create new WatchDog user";
	public static final String YOUR_USER_ID_LABEL = "Your User ID is: ";

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
