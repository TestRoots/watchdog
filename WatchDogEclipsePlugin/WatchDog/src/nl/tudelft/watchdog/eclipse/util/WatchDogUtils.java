package nl.tudelft.watchdog.eclipse.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.ContentReaderException;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

/** Utilities for WatchDog. */
public class WatchDogUtils extends WatchDogUtilsBase {

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

		IDocument document = documentProvider
				.getDocument(editor.getEditorInput());
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
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getFile(editor).getContents(), "UTF-8"));
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

	}

	/**
	 * @return The underlying file of the given editor.
	 */
	public static IFile getFile(ITextEditor editor)
			throws IllegalArgumentException {
		if (editor.getEditorInput() instanceof FileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editor
					.getEditorInput();
			return fileEditorInput.getFile();
		} else {
			throw new IllegalArgumentException("can't read resource file");
		}
	}

	/** Sleeps for the specified amount of milliseconds. */
	public static void sleep(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Returns the workspace name. */
	public static String getWorkspaceName() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile()
				.toString();
	}

	/**
	 * Returns the {@link ProjectPreferenceSetting} of the currently active
	 * workspace.
	 */
	public static ProjectPreferenceSetting getProjectSetting() {
		return Preferences.getInstance()
				.getOrCreateProjectSetting(getWorkspaceName());
	}

}
