package nl.tudelft.watchdog.eclipse.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.core.logic.interval.IntervalTransferManagerBase;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

/**
 * {@inheritDoc}
 */
public class IntervalTransferManager extends IntervalTransferManagerBase {

	/**
	 * Constructor.
	 */
	public IntervalTransferManager(
			final IntervalPersisterBase intervalPersister, String projectName) {
		super(intervalPersister, projectName);
	}

	protected static void refreshUI() {
		UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
	}

}
