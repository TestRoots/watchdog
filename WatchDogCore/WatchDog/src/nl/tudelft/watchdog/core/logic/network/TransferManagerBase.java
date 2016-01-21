package nl.tudelft.watchdog.core.logic.network;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.ui.RegularCheckerBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * This manager takes care of the repeated transferal of all T's to the server.
 * When the transfer to the server was successful, the T's are immediately
 * deleted from the local database. Furthermore, it allows the immediate
 * execution of this regularly scheduled task, e.g. when it is needed on
 * exiting.
 */
public abstract class TransferManagerBase<T extends WatchDogTransferable> extends RegularCheckerBase {

	/**
	 * Constructor. Tries to immediately transfer all remaining T's, and sets up
	 * a scheduled timer to run every {@value #updateRate} milliseconds.
	 */
	public TransferManagerBase(final PersisterBase<T> persisterBase, String projectName, int updateRate) {
		super(updateRate);
		task = new TransferTimerTask(persisterBase, projectName);
		runSetupAndStartTimeChecker();
	}

	/** Immediately synchronizes the T's with the server. */
	public void sendItemsImmediately() {
		NetworkUtils.setConnectionTimeout(2000);
		NetworkUtils.cancelTransferAfter(2000);
		task.run();
		NetworkUtils.setConnectionTimeout(NetworkUtils.DEFAULT_TIMEOUT);
	}

	protected static void refreshUI() {	}
	
	protected abstract JsonTransferer<T> createTransferer();
	protected abstract void updatePreferences(int transferredItems);

	private class TransferTimerTask extends TimerTask {
		private final PersisterBase<T> persister;
		private final String projectName;

		private TransferTimerTask(PersisterBase<T> persisterBase, String projectName) {
			this.persister = persisterBase;
			this.projectName = projectName;
		}

		/**
		 * Transfers all T's from the persistence storage that are not yet on
		 * the server, to the server.
		 */
		@Override
		public void run() {
			if (persister.isClosed()) {
				return;
			}

			List<T> itemsToTransfer = new ArrayList<T>(persister.readItems());

			if (itemsToTransfer.isEmpty()) {
				return;
			}

			transferItems(itemsToTransfer);
			resetDatabase();
			refreshUI();
		}

		private void transferItems(List<T> itemsToTransfer) {
			JsonTransferer<T> transferer = createTransferer();

			Connection connection = transferer.sendItems(itemsToTransfer, projectName);
			switch (connection) {
			case SUCCESSFUL:
				persister.removeItems(itemsToTransfer);
				updatePreferences(itemsToTransfer.size());
				WatchDogGlobals.lastTransactionFailed = false;
				break;

			case NETWORK_ERROR:
				if (WatchDogGlobals.lastTransactionFailed) {
					// two transactions in a row failed. The user is likely
					// working without internet, so do not try to re-send
					// items
					return;
				}
				WatchDogGlobals.lastTransactionFailed = true;
				break;

			case UNSUCCESSFUL:
				WatchDogGlobals.lastTransactionFailed = true;
				int items = itemsToTransfer.size();

				if (items == 1) {
					WatchDogLogger.getInstance().logSevere("Could not transfer item and removed permanently!");
					persister.removeItems(itemsToTransfer);
					return;
				}

				// divide and conquer
				int halfOfItems = (int) Math.floor(items / 2);
				List<T> firstHalfItems = itemsToTransfer.subList(0, halfOfItems);
				List<T> secondHalfItems = itemsToTransfer.subList(halfOfItems, items);
				transferItems(firstHalfItems);
				transferItems(secondHalfItems);
				break;
			}

		}

		private void resetDatabase() {
			if (persister.getSize() <= 0) {
				persister.clearAndResetMap();
			}
		}
	}
}
