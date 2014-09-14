package nl.tudelft.watchdog.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/** Utilities for watchDog. */
public class WatchDogUtils {

	/** A random (generator) object. */
	public static final Random randomObject = new Random();

	/** Formatter for a {@link Period}. */
	private static PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
			.printZeroRarelyLast().appendDays().appendSuffix(" day", " days")
			.appendSeparator(", ").appendHours()
			.appendSuffix(" hour", " hours").appendSeparator(", ")
			.appendMinutes().appendSuffix(" minute", " minutes")
			.appendSeparator(" and ").appendSeconds()
			.appendSuffix(" second", " seconds").toFormatter();

	/**
	 * Returns the contents of the supplied {@link ITextEditor}.
	 * 
	 * @param editor
	 *            the editor you want the contents of
	 * @return The content of the editor
	 * @throws ContentReaderException
	 *             Can throw this exception when a file is moved. When moving a
	 *             file within the workspace, the document provider pointer is
	 *             set to null to make room for a new document provider later in
	 *             the moving phase
	 * @throws IllegalArgumentException
	 *             Unexpected eclipse API behavior when Editor is null or the
	 *             document in the document provider is null
	 */
	public static String getEditorContent(final ITextEditor editor)
			throws ContentReaderException, IllegalArgumentException {
		IDocument document = extractDocument(editor);
		if (document == null) {
			throw new ContentReaderException("Document is null");
		}

		return document.get();
	}

	private static IDocument extractDocument(final ITextEditor editor)
			throws ContentReaderException {
		if (editor == null) {
			throw new IllegalArgumentException("Editor is null");
		}

		IDocumentProvider documentProvider = editor.getDocumentProvider();
		if (documentProvider == null) {
			throw new ContentReaderException(
					"Editor closed: Document provider is null");
		}

		IDocument document = documentProvider.getDocument(editor
				.getEditorInput());
		return document;
	}

	/**
	 * Determines whether the both given {@link ITextEditor}s have the same
	 * underlying document (<code>true</code>) or not (<code>false</code>).
	 */
	public static boolean hasSameUnderlyingDocument(ITextEditor editor1,
			ITextEditor editor2) {
		try {
			return extractDocument(editor1) == extractDocument(editor2);
		} catch (ContentReaderException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return false;
	}

	/**
	 * Reads and returns the contents of a file from the supplied editor, by
	 * trying to access the file from the disk.
	 */
	public static String getContentForEditorFromDisk(ITextEditor editor) {
		if (editor.getEditorInput() instanceof FileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editor
					.getEditorInput();

			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(
						fileEditorInput.getFile().getContents(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
				return sb.toString();
			} catch (Exception e) {
				throw new IllegalArgumentException("can't read resource file");
			}
		} else {
			throw new IllegalArgumentException("can't read resource file");
		}
	}

	/**
	 * @return the converted {@link Duration} in a human-readable String,
	 *         discarding milliseconds.
	 */
	public static String makeDurationHumanReadable(Duration duration) {
		return periodFormatter.print(duration.toPeriod().withMillis(0)
				.normalizedStandard());
	}

	/**
	 * @return <code>true</code> when the given string is either
	 *         <code>null</code> or empty. <code>false</code> otherwise.
	 */
	public static boolean isEmpty(String string) {
		if (string == null || string.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return Whether the string with whitespaces trimmed is empty.
	 */
	public static boolean isEmptyOrHasOnlyWhitespaces(String string) {
		return isEmpty(string) ? true : string.trim().isEmpty();
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
	 * @return A hash code for the given String, so that it is completely
	 *         anonymous.
	 */
	public static String createHash(String name) {
		return DigestUtils.shaHex(name);
	}

	/**
	 * Generates an intelligent hash code the supplied fileName.
	 * 
	 * <br>
	 * <br>
	 * Example: Generates a hash code <br>
	 * <code>a6bb3545c5d1424e8bb6e95aceb1c734535e7ca3</code> for input
	 * "AClass.java" and <br>
	 * <code>a6bb3545c5d1424e8bb6e95aceb1c734535e7ca3Test</code> for
	 * "AClassTest.java". This means it is possible to relate a test file to its
	 * related Production class hash, by removing the trailing "Test" string
	 * from the string returned by this method.
	 * 
	 * @return A hash for the given filename.
	 */
	public static String createFileNameHash(String fileName) {
		String shortenedName = fileName.toLowerCase().replace(".java", "");
		if (shortenedName.startsWith("test") || shortenedName.endsWith("test")) {
			shortenedName = shortenedName.replace("test", "");
			return createHash(shortenedName) + "Test";
		} else {
			return createHash(shortenedName);
		}
	}

}
