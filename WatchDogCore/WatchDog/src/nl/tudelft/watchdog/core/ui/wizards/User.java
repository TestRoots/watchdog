package nl.tudelft.watchdog.core.ui.wizards;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.network.WatchDogTransferable;

/**
 * The concept of a user comprises all information about the user who runs this
 * WatchDog instance.
 */
public class User extends WatchDogTransferable {

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
