package nl.tudelft.watchdog.eclipse.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
	 *             Can throw this exception when a file is moved. When moving a file
	 *             within the workspace, the document provider pointer is set to
	 *             null to make room for a new document provider later in the moving
	 *             phase
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

	private static IDocument extractDocument(final ITextEditor editor) throws ContentReaderException {
		if (editor == null) {
			throw new IllegalArgumentException("Editor is null");
		}

		IDocumentProvider documentProvider = editor.getDocumentProvider();
		if (documentProvider == null) {
			throw new ContentReaderException("Editor closed: Document provider is null");
		}

		IDocument document = documentProvider.getDocument(editor.getEditorInput());
		return document;
	}

	/**
	 * Determines whether the both given {@link ITextEditor}s have the same
	 * underlying document (<code>true</code>) or not (<code>false</code>).
	 */
	public static boolean hasSameUnderlyingDocument(ITextEditor editor1, ITextEditor editor2) {
		try {
			return extractDocument(editor1) == extractDocument(editor2);
		} catch (ContentReaderException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return false;
	}

	/**
	 * Reads and returns the contents of a file from the supplied editor, by trying
	 * to access the file from the disk.
	 */
	public static String getContentForEditorFromDisk(ITextEditor editor) {
		return getContentForFileFromDisk(getFile(editor));
	}

	/**
	 * Reads and returns the contents of a file, by trying to access the file from
	 * the disk. Can return the empty string if it cannot read the file's content.
	 */
	public static String getContentForFileFromDisk(IFile file) {
		return getContentForFileFromDisk(file, 0);
	}

	private static String getContentForFileFromDisk(IFile file, int count) {
		if (count > 5) {
			WatchDogLogger.getInstance().logSevere("Couldn't get contents for file 5 times. Aborting.");
			return "";
		}

		try {
			return readContentForFileFromDisk(file);
		} catch (Exception e) {
			try {
				file.refreshLocal(IFile.DEPTH_ZERO, null);
			} catch (CoreException ex) {
				count = 5;
			}
			return getContentForFileFromDisk(file, count + 1);
		}
	}

	/**
	 * A performance-optimized method for reading files from disk. Does not read
	 * source code files bigger than {@link WatchDogUtilsBase#MAX_FILE_SIZE} and
	 * instead returns the empty string for performance optimization reasons.
	 */
	private static String readContentForFileFromDisk(IFile file)
			throws UnsupportedEncodingException, CoreException, IOException {
		File realfile = file.getRawLocation().makeAbsolute().toFile();
		if (realfile.length() > WatchDogUtilsBase.MAX_FILE_SIZE) {
			WatchDogLogger.getInstance().logSevere("File too big to be read!");
			return "";
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		return sb.toString();
	}

	/**
	 * @return The underlying file of the given editor.
	 */
	public static IFile getFile(ITextEditor editor) throws IllegalArgumentException {
		if (editor.getEditorInput() instanceof FileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editor.getEditorInput();
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
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toString();
	}

	/**
	 * Returns the {@link ProjectPreferenceSetting} of the currently active
	 * workspace.
	 */
	public static ProjectPreferenceSetting getProjectSetting() {
		return Preferences.getInstance().getOrCreateProjectSetting(getWorkspaceName());
	}

}
