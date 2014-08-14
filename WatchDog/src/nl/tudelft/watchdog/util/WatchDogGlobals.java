package nl.tudelft.watchdog.util;

import nl.tudelft.watchdog.Activator;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Globals for the current WatchDog instance.
 */
public class WatchDogGlobals {

	/** A text used in the UI if WatchDog is running. */
	public final static String activeWatchDogUIText = "WatchDog is active and recording ...";

	/** A text used in the UI if WatchDog is not running. */
	public final static String inactiveWatchDogUIText = "WatchDog is inactive!";

	/** The default URI of the WatchDogServer. */
	public final static String DEFAULT_SERVER_URI = "http://www.testroots.org/watchdog/";

	/** Flag determining whether WatchDog is active. */
	public static boolean isActive = false;

	/** The reading timeout in milliseconds. */
	public static int READING_TIMEOUT = 4 * 1000;

	/** The typing timeout in milliseconds. */
	public static int TYPING_TIMEOUT = 4 * 1000;

	/** The TU Logo. */
	public static ImageDescriptor tuLogoImageDescriptor = Activator
			.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"resources/images/tudelft_with_frame.png");
}
