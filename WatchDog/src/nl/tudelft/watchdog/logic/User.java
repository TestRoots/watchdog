package nl.tudelft.watchdog.logic;

import java.util.Date;

/**
 * The concept of a user comprises all information about the user who runs this
 * WatchDog instance.
 */
public class User {

	/** Constructor. */
	public User() {
		registrationDate = new Date();
	}

	/** eMail. */
	public String email;

	/** Organization. */
	public String organization;

	/** Group. */
	public String group;

	/**
	 * Whether the user participates in the lottery, and whether we may contact
	 * him.
	 */
	public boolean mayBeContacted = false;

	/** The registration date. */
	public Date registrationDate;
}
