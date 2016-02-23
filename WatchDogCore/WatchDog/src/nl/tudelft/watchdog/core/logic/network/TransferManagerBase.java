package nl.tudelft.watchdog.core.logic.network;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.ui.RegularCheckerBase;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * This manager takes care of the repeated transferal of all T's to the server.
 * When the transfer to the server was successful, the T's are immediately
 * deleted from the local database. Furthermore, it allows the immediate
 * execution of this regularly scheduled task, e.g. when it is needed on
 * exiting.
 */
public class TransferManagerBase extends RegularCheckerBase {

	private static final int UPDATE_RATE = 3 * 60 * 1000;
	
	/** Indicates the type of the items to be send to the server. */
	public enum ItemType {
		EVENT, INTERVAL;
	}

	/**
	 * Constructor. Tries to immediately transfer all remaining T's, and sets up
	 * a scheduled timer to run every {@value #UPDATE_RATE} milliseconds.
	 */
	public TransferManagerBase(final PersisterBase persisterBase, String projectName) {
		super(UPDATE_RATE);
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

	/** 
	 * Refreshes the InfoDialog in Eclipse to show updated transfer statistics. 
	 * 
	 * Note: To be implemented in IDE specific implementation 
	 */
	protected static void refreshUI() {	}
	
	/** Updates the statistics preferences after transferring the items to the server. */
	private void updateStatisticsPreferences(ItemType itemType, int transferredItems) {
		PreferencesBase prefs = WatchDogGlobals.getPreferences();
		switch (itemType) {
		case EVENT:
			prefs.setLastTransferedEvent();
			prefs.addTransferedEvents(transferredItems);			
			break;

		case INTERVAL:
			prefs.setLastTransferedInterval();
			prefs.addTransferedIntervals(transferredItems);		
			break;
		}
	}

	private class TransferTimerTask extends TimerTask {
		private final PersisterBase persister;
		private final String projectName;

		private TransferTimerTask(PersisterBase persisterBase, String projectName) {
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

			List<WatchDogTransferable> itemsToTransfer = new ArrayList<WatchDogTransferable>(persister.readItems());
			if (itemsToTransfer.isEmpty()) {
				return;
			}
			
			// Split events/intervals and send them separately to the correct URL
			List<WatchDogTransferable> eventsToTransfer = new ArrayList<>();
			List<WatchDogTransferable> intervalsToTransfer = new ArrayList<>();
			for (WatchDogTransferable item: itemsToTransfer) {
				if (item instanceof EventBase) {
					eventsToTransfer.add(item);
				} else if (item instanceof IntervalBase) {
					intervalsToTransfer.add(item);
				}
			}
			
			transferItems(eventsToTransfer, ItemType.EVENT);
			transferItems(intervalsToTransfer, ItemType.INTERVAL);			
			resetDatabase();
			refreshUI();
		}

		private void transferItems(List<WatchDogTransferable> itemsToTransfer, ItemType itemsToTransferType) {
			if (itemsToTransfer.isEmpty()) {
				return;
			}
			
			JsonTransferer transferer = new JsonTransferer();
			Connection connection = transferer.sendItems(itemsToTransfer, projectName, itemsToTransferType);
			switch (connection) {
			case SUCCESSFUL:
				persister.removeItems(itemsToTransfer);
				updateStatisticsPreferences(itemsToTransferType, itemsToTransfer.size());
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
				List<WatchDogTransferable> firstHalfItems = itemsToTransfer.subList(0, halfOfItems);
				List<WatchDogTransferable> secondHalfItems = itemsToTransfer.subList(halfOfItems, items);
				transferItems(firstHalfItems, itemsToTransferType);
				transferItems(secondHalfItems, itemsToTransferType);
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
