package nl.tudelft.watchdog.ui.handlers;

import java.util.Map;

import nl.tudelft.watchdog.ui.InfoDialog;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/** Handler for displaying an {@link InfoDialog}. */
public class InfoHandler extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InfoDialog infoDialog = new InfoDialog(
				HandlerUtil.getActiveShell(event));
		infoDialog.setBlockOnOpen(false);
		infoDialog.open();
		return null;
	}

	@Override
	public void updateElement(UIElement element,
			@SuppressWarnings("rawtypes") Map parameters) {
		if (WatchDogGlobals.isActive) {
			Preferences preferences = Preferences.getInstance();

			displayUpdateQuestionDialog(preferences);

			if (WatchDogGlobals.lastTransactionFailed
					|| preferences.isOldVersion()) {
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

	private void displayUpdateQuestionDialog(Preferences preferences) {
		if (preferences.isBigUpdateAvailable()
				&& !preferences.isBigUpdateAnswered()) {
			preferences.setBigUpdateAnswered(true);
			boolean wantsToUpdate = MessageDialog.openQuestion(null,
					"Major WatchDog Update Available!",
					"We improved WatchDog a lot for you! Update?");
			if (wantsToUpdate) {
				preferences.setBigUpdateAnswered(false);
				UIUtils.updateWatchDog();
			}
		}
	}

}
