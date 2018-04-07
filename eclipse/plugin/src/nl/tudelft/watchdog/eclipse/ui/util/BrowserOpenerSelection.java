package nl.tudelft.watchdog.eclipse.ui.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/** Opens the system browser if selected. */
public class BrowserOpenerSelection extends SelectionAdapter {
	@Override
	public void widgetSelected(SelectionEvent event) {
		try {
			// Open default external browser
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL(event.text));
		} catch (PartInitException | MalformedURLException exception) {
			// Browser could not be opened. We do nothing about it.
		}
	}
}
