package nl.tudelft.watchdog.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;

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

	/** Returns <code>true</code> when Eclipse's Debug Perspective is activated. */
	public static boolean isInDebugMode() {
		// TODO (MMB) it appears to me, recognition of debug mode might be
		// buggy. As it stands, as long as the active page of any window
		// (regardless of the window being active or not) is in debug, the debug
		// flag is set. However, if ANY window is in debug perspective, this
		// does not mean we the user is currently debugging at all.
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
			throw new IllegalArgumentException("Editor is null");
		}

		IDocumentProvider documentProvider = editor.getDocumentProvider();
		if (documentProvider == null) {
			throw new ContentReaderException(
					"Editor closed: Document provider is null");
		}

		IDocument document = documentProvider.getDocument(editor
				.getEditorInput());
		if (document == null) {
			throw new ContentReaderException("Document is null");
		}

		return document.get();
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
						fileEditorInput.getFile().getContents()));
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

}
