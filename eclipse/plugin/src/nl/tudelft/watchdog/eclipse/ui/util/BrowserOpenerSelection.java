package nl.tudelft.watchdog.eclipse.ui.util;

import java.net.URL;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/** Opens the system browser if selected. */
public class BrowserOpenerSelection extends SelectionAdapter {

	private static final String DIALOG_TITLE = "Browser not supported";
	private static final String DIALOG_MESSAGE = "WatchDog could not open this url in a browser. Please open the following url manually: ";

	@Override
	public void widgetSelected(SelectionEvent event) {
		try {
			// Open default external browser
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL(event.text));
		} catch (Throwable exception) {
			// Browser could not be opened. We do nothing about it.
			Display.getDefault().asyncExec(() -> {
				Shell shell = Display.getDefault().getActiveShell();
				new InputDialog(shell, DIALOG_TITLE, DIALOG_MESSAGE, event.text, null).open();
			});
		}
	}
}
