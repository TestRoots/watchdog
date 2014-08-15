package nl.tudelft.watchdog.util;

import java.io.File;
import java.io.FileReader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

/**
 * Globals for the current WatchDog instance.
 */
public class WatchDogGlobals {

	/** A text used in the UI if WatchDog is running. */
	public final static String activeWatchDogUIText = "WatchDog is active and recording ...";

	/** A text used in the UI if WatchDog is not running. */
	public final static String inactiveWatchDogUIText = "WatchDog is inactive!";

	/** The default URI of the WatchDogServer. */
	public final static String DEFAULT_SERVER_URI = "http://watchdog.testroots.org/";

	/** Flag determining whether WatchDog is active. */
	public static boolean isActive = false;

	/** The client's version, as set in pom.xml. */
	public static String CLIENT_VERSION = "1.0-standard";

	static {
		Model model = null;
		FileReader reader = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();

		try {
			File pomFile = new File("pom.xml");
			reader = new FileReader(pomFile); // <-- pomfile is your pom.xml
			model = mavenreader.read(reader);
			model.setPomFile(pomFile);
		} catch (Exception ex) {
			// intentionally left empty;
		}

		MavenProject project = new MavenProject(model);
		CLIENT_VERSION = project.getVersion();
	}

}
