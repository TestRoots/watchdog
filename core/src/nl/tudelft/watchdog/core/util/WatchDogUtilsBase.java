package nl.tudelft.watchdog.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;

import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;

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
		return isEmpty(string) || string.trim().isEmpty();
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

	/** @return a pre-filled link to the survey on debugging. */
	public static String getDebugSurveyLink() {
		StringBuilder builder = new StringBuilder(
				"https://docs.google.com/forms/d/1ybD1jC-iICXNlmQpyPEFngtmOtodicDr18E1ZbfBtx4/viewform?");
		PreferencesBase preferences = WatchDogGlobals.getPreferences();

		// Add user id.
		builder.append("entry.1872114938=");
		builder.append(preferences.getUserId());

		// Add programming experience (if available).
		String programmingExperienceParam = "&entry.962486075=";
		try {
			programmingExperienceParam += URLEncoder.encode(preferences.getProgrammingExperience(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			programmingExperienceParam = "";
		}

		if (programmingExperienceParam.length() > 0) {
			builder.append(programmingExperienceParam);
		}

		// Add programming language.
		builder.append("&entry.87074017=Java");

		// Add IDE parameter.
		builder.append("&entry.1002919343=");
		switch (WatchDogGlobals.hostIDE) {
		case ECLIPSE:
			builder.append("Eclipse");
			break;
		case INTELLIJ:
		case ANDROIDSTUDIO:
			builder.append("IntelliJ");
			break;
		}

		// Add final parameters and build the string
		builder.append("&entry.2010347695&entry.2084367812");
		return builder.toString();
	}

	// The lambda in this function is in its expanded form, because of a very obscure Java compiler bug on Travis.
    // For some reason, when using a lambda notation foo -> {} in a static function with type parameters,
    // the compiler complains about "undeclared type variables" in a subclass (in this case the intellij module)
    @SuppressWarnings("Convert2Lambda")
    public static <A, R, E extends Exception> Function<A, R> unchecked(FunctionWithException<A, R, E> function) {
        return new Function<A, R>() {
            @Override
            public R apply(A argument) {
                try {
                    return function.apply(argument);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    @FunctionalInterface
    public interface FunctionWithException<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

}
