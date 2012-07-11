package nl.tudelft.watchdog.timeDistributionPlugin.commands;

import nl.tudelft.watchdog.interval.IntervalKeeper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class ResetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IntervalKeeper.getInstance().getRecordedIntervals().clear();
		return null;
	}

}
