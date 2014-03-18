package nl.tudelft.watchdog.ui.commands;

import nl.tudelft.watchdog.logic.interval.IntervalManager;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ResetHandler implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		IntervalManager.getInstance().getRecordedIntervals().clear();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
	}

}
