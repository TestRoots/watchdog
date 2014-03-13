package nl.tudelft.watchdog.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.exceptions.ContentReaderException;
import nl.tudelft.watchdog.interval.IIntervalManager;
import nl.tudelft.watchdog.interval.IntervalManager;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.interval.recorded.RecordedIntervalSerializationManager;
import nl.tudelft.watchdog.plugin.logging.WDLogger;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Utilities for watchDog.
 */
public class WatchDogUtils {

	/** Formatter for a {@link Period}. */
	private static PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
			.printZeroRarelyLast().appendDays().appendSuffix(" day", " days")
			.appendSeparator(", ").appendHours()
			.appendSuffix(" hour", " hours").appendSeparator(", ")
			.appendMinutes().appendSuffix(" minute", " minutes")
			.appendSeparator(" and ").appendSeconds()
			.appendSuffix(" second", " seconds").toFormatter();

	/**
	 * Returns <code>true</code> when Eclipse's Debug Perspective is activated.
	 */
	public static boolean isInDebugMode() {
		boolean isDebugMode = false;
		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			isDebugMode = window.getActivePage().getPerspective().getId()
					.equals("org.eclipse.debug.ui.DebugPerspective");
			if (isDebugMode) {
				return isDebugMode;
			}
		}
		return isDebugMode;
	}

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
		if (editor == null) {
			throw new IllegalArgumentException("editor is null");
		}
		if (editor.getDocumentProvider() == null) {
			throw new ContentReaderException("doc provider is null");
		}
		IDocumentProvider dp = editor.getDocumentProvider();
		if (dp.getDocument(editor.getEditorInput()) == null) {
			throw new IllegalArgumentException("doc is null");
		}
		IDocument doc = dp.getDocument(editor.getEditorInput());

		return doc.get();
	}

	/**
	 * Reads and returns the contents of a file from the supplied editor.
	 */
	public static String getFileContentsFromEditor(ITextEditor editor) {
		if (editor.getEditorInput() instanceof FileEditorInput) {
			IFileEditorInput editorInput = (IFileEditorInput) editor
					.getEditorInput();

			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(editorInput
						.getFile().getContents()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				String res = sb.toString();
				System.out.println(res);
				return res;

			} catch (Exception e) {
				throw new IllegalArgumentException("can't read resource file");
			}
		} else {
			throw new IllegalArgumentException("can't read resource file");
		}
	}

	/**
	 * @return A list of all stored recorded intervals.
	 */
	public static List<IInterval> getAllRecordedIntervals() {
		RecordedIntervalSerializationManager serializationManager = new RecordedIntervalSerializationManager();

		IIntervalManager intervalKeeper = IntervalManager.getInstance();
		List<IInterval> completeList = new ArrayList<IInterval>();
		try {
			completeList.addAll(serializationManager
					.retrieveRecordedIntervals());
		} catch (IOException exception) {
			WDLogger.logSevere(exception);
		} catch (ClassNotFoundException exception) {
			WDLogger.logSevere(exception);
		}
		completeList.addAll(intervalKeeper.getRecordedIntervals());
		return completeList;
	}

	/**
	 * @return the converted Joda Duration in a human-readable String,
	 *         discarding milliseconds.
	 */
	public static String makeDurationHumanReadable(Duration duration) {
		return periodFormatter.print(duration.toPeriod().withMillis(0)
				.normalizedStandard());
	}
}
