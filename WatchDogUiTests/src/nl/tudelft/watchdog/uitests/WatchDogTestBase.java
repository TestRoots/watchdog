package nl.tudelft.watchdog.uitests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.BeforeClass;

/**
 * Base test class for every WatchDog UI test case.
 */
public class WatchDogTestBase {

	/** The {@link SWTWorkbenchBot} bot. */
	protected static SWTWorkbenchBot bot;

	/** Flag denoting whether the workbench has already been initialized. */
	private static boolean watchDogInitialized = false;

	/**
	 * Initializes the eclipse workbench for WatchDog testing, if not already
	 * done.
	 */
	@BeforeClass
	public static void initalizeEclipseWorkbench() {
		if (!watchDogInitialized) {
			bot = new SWTWorkbenchBot();
			bot.viewByTitle("Welcome").close();
			watchDogInitialized = true;
		}
	}
}
