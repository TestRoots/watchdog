package nl.tudelft.watchdog.ui.handlers;

import java.util.Map;

import nl.tudelft.watchdog.ui.infoDialog.InfoStatisticsDialog;
import nl.tudelft.watchdog.ui.newUserWizard.NewUserWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/**
 * Handler for displaying a blocking {@link InfoStatisticsDialog}.
 */
public class UserWizardDialogHandler extends AbstractHandler implements
		IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog wizardDialog = new WizardDialog(
				HandlerUtil.getActiveShell(event), new NewUserWizard());
		wizardDialog.setBlockOnOpen(true);
		return wizardDialog.open();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateElement(UIElement element, Map parameters) {
	}
}
