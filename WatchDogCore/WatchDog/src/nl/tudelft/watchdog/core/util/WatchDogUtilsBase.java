package nl.tudelft.watchdog.core.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;

import java.util.regex.Pattern;

/**
 * Base class for WatchDog Utilities.
 */
public abstract class WatchDogUtilsBase {
	/**
	 * @return A hash code for the given String, so that it is completely
	 *         anonymous.
	 */
	public static String createHash(String name) {
		return DigestUtils.sha1Hex(name);
	}

	/**
	 * Generates an intelligent hash code of the supplied fileName, removing
	 * .java file endings and shortening fully-qualified filenames.
	 *
	 * <br>
	 * Example 1: Generates a hash code <code>a</code> for input "AClass.java"
	 * and <code>aTest</code> for "AClassTest.java". <br>
	 * <br>
	 * Example 2: Takes <code>package.for.a</code> and returns the same file
	 * hash as <code>a</code>.
	 *
	 * @return A hash for the given filename.
	 */
	public static String createFileNameHash(String fileName) {
		String hashedName = "";
		if (isEmpty(fileName)) {
			return hashedName;
		}
		String lowerCaseFileName = fileName.toLowerCase().replaceFirst(Pattern.quote(".") + "java$", "");

		// Strip-away fully-qualified path from filename (necessary when project
		// has Maven nature)
		String[] fileNameParts = lowerCaseFileName.split(Pattern.quote("."));
		lowerCaseFileName = fileNameParts[fileNameParts.length - 1];

		if (lowerCaseFileName.startsWith("test") || lowerCaseFileName.endsWith("test")) {
			lowerCaseFileName = lowerCaseFileName.replaceFirst("^test", "");
			lowerCaseFileName = lowerCaseFileName.replaceFirst("test$", "");
			hashedName = createHash(lowerCaseFileName) + "Test";
		} else {
			hashedName = createHash(lowerCaseFileName);
		}
		return hashedName;
	}

	/**
	 * @return the number of source lines of code in the given string.
	 */
	public static long countSLOC(String text) {
		String[] lines = text.split("\r\n|\r|\n");
		long sloc = 0;
		for (String line : lines) {
			if (!isEmptyOrHasOnlyWhitespaces(line)) {
				sloc++;
			}
		}
		return sloc;
	}

	/**
	 * @return Whether the string with white spaces trimmed is empty.
	 */
	public static boolean isEmptyOrHasOnlyWhitespaces(String string) {
		return isEmpty(string) ? true : string.trim().isEmpty();
	}

	/**
	 * @return <code>true</code> when the given string is either
	 *         <code>null</code> or empty. <code>false</code> otherwise.
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	/** Converts given object to Json format. */
	public static String convertToJson(Object object) {
		return new Gson().toJson(object);
	}

}
