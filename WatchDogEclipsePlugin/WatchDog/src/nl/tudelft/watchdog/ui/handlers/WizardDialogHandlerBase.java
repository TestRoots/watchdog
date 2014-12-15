package nl.tudelft.watchdog.ui.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/** Base handler for displaying a blocking Wizards. */
public abstract class WizardDialogHandlerBase extends AbstractHandler implements
		IElementUpdater {

	/** Opens the wizard page. */
	protected Object execute(IWizard wizard, ExecutionEvent event)
			throws ExecutionException {
		WizardDialog wizardDialog = new NoCancelOnFinishablePageWizardDialog(
				HandlerUtil.getActiveShell(event), wizard);
		wizardDialog.setBlockOnOpen(true);
		return wizardDialog.open();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateElement(UIElement element, Map parameters) {
	}
}
