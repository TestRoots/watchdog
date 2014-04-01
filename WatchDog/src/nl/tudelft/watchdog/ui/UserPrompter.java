package nl.tudelft.watchdog.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import nl.tudelft.watchdog.logic.exceptions.FileSavingFailedException;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UserPrompter {

	private static Display display;
	private static Shell shell;

	static {
		display = Display.getCurrent();
		shell = new Shell(display);
	}

	public static void saveIntervalsToFile(List<IntervalBase> intervals)
			throws FileSavingFailedException {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { "XML files", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); // Windows
																		// wild
																		// cards
		// TODO (MMB) Ouch!!
		dialog.setFilterPath("c:/"); // Windows path
		dialog.setFileName("WatchDogIntervals.xml");
		dialog.setOverwrite(true);

		String path = dialog.open();

		if (path != null) {
			File f = new File(path);
			try {
				FileOutputStream fos = new FileOutputStream(f);
				// TODO (MMB) ouch
			} catch (FileNotFoundException e) {
				WatchDogLogger.logSevere(e);
				throw new FileSavingFailedException(e);
			}
		} else {
			WatchDogLogger.logInfo("File saving canceled");
		}
	}

	public static void showMessageBox(String title, String contents) {
		MessageBox b = new MessageBox(shell);
		b.setText(title);
		b.setMessage(contents);
		b.open();
	}
}
