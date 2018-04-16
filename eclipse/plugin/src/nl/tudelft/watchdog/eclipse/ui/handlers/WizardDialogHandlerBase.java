package nl.tudelft.watchdog.eclipse.ui.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/** Base handler for displaying a blocking Wizards. */
public abstract class WizardDialogHandlerBase extends AbstractHandler implements
		IElementUpdater {

	/** Opens the wizard page. */
	protected Object execute(RegistrationWizard wizard, ExecutionEvent event)
			throws ExecutionException {
		WizardDialog wizardDialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		wizard.setDialog(wizardDialog);
		wizardDialog.setBlockOnOpen(true);
		wizardDialog.setMinimumPageSize(0, 500);
		return wizardDialog.open();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateElement(UIElement element, Map parameters) {
	}
}
