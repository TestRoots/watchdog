package nl.tudelft.watchdog.eclipse.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.core.logic.interval.IntervalTransferManagerBase;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

/**
 * IntervalTransferManager which adds specific command to execute in Timer task.
 */
public class IntervalTransferManager extends IntervalTransferManagerBase {

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public IntervalTransferManager(
			final IntervalPersisterBase intervalPersister, String projectName) {
		super(intervalPersister, projectName);
	}

	protected static void executeCommand() {
		UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
	}

}
