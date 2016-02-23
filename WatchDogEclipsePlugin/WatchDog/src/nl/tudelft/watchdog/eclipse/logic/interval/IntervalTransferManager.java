package nl.tudelft.watchdog.eclipse.logic.interval;

import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

/**
 * {@inheritDoc}
 */
public class IntervalTransferManager extends TransferManagerBase {

	/**
	 * Constructor.
	 */
	public IntervalTransferManager(final PersisterBase intervalPersister,
			String projectName) {
		super(intervalPersister, projectName);
	}

	/** {@inheritDoc} */
	protected static void refreshUI() {
		UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
	}

}
