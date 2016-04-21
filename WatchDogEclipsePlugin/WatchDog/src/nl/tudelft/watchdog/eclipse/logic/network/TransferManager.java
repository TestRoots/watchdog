package nl.tudelft.watchdog.eclipse.logic.network;

import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

/**
 * {@inheritDoc}
 */
public class TransferManager extends TransferManagerBase {

	/**
	 * Constructor.
	 */
	public TransferManager(final PersisterBase persister,
			String projectName) {
		super(persister, projectName);
	}

	/** {@inheritDoc} */
	protected static void refreshUI() {
		UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
	}

}
