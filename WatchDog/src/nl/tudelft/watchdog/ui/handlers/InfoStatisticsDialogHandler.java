package nl.tudelft.watchdog.ui.handlers;

import java.util.Map;

import nl.tudelft.watchdog.ui.infoDialog.InfoStatisticsDialog;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/** Handler for displaying an {@link InfoStatisticsDialog}. */
public class InfoStatisticsDialogHandler extends AbstractHandler implements
		IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InfoStatisticsDialog infoDialog = new InfoStatisticsDialog(
				HandlerUtil.getActiveShell(event));
		infoDialog.setBlockOnOpen(false);
		infoDialog.open();
		return null;
	}

	@Override
	public void updateElement(UIElement element,
			@SuppressWarnings("rawtypes") Map parameters) {
		if (WatchDogGlobals.isActive) {
			if (WatchDogGlobals.lastTransactionFailed
					|| Preferences.getInstance().isOldVersion()) {
				element.setTooltip(WatchDogGlobals.activeWatchDogUIText);
				element.setIcon(UIUtils.watchDogIconWarning);
			} else {
				element.setTooltip(WatchDogGlobals.activeWatchDogUIText);
				element.setIcon(UIUtils.watchDogIcon);
			}
		} else {
			element.setTooltip(WatchDogGlobals.inactiveWatchDogUIText);
			element.setIcon(UIUtils.watchDogIconDisabled);
		}
	}

}
