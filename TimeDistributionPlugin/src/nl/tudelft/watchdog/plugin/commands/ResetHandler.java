package nl.tudelft.watchdog.plugin.commands;

import nl.tudelft.watchdog.interval.IntervalKeeper;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public class ResetHandler implements IWorkbenchWindowActionDelegate {


	@Override
	public void run(IAction action) {
		IntervalKeeper.getInstance().getRecordedIntervals().clear();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow window) {}

}
