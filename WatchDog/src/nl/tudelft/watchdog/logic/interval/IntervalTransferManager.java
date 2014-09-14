package nl.tudelft.watchdog.logic.interval;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

/**
 * This manager takes care of the repeated transferal of all closed intervals to
 * the server. When the transfer to the server was successful, the intervals are
 * immediately deleted from the local database. Furthermore, it allows the
 * immediate execution of this regularly scheduled task, e.g. when it is needed
 * on exiting.
 */
public class IntervalTransferManager {

	private static final int UPDATE_RATE = 3 * 60 * 1000;

	private static final int intervalsPerRoundTransfer = 50;

	private Timer timer;

	private IntervalsTransferTimerTask task;

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public IntervalTransferManager(final IntervalPersister intervalPersister) {
		task = new IntervalsTransferTimerTask(intervalPersister);
		task.run();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(task, 0, UPDATE_RATE);
	}

	/** Immediately synchronizes the intervals with the server. */
	public void sendIntervalsImmediately() {
		task.run();
	}

	private static class IntervalsTransferTimerTask extends TimerTask {
		private final IntervalPersister intervalPersister;
		private long lastTransferedIntervalKey;
		private Preferences preferences;

		private IntervalsTransferTimerTask(IntervalPersister intervalPersister) {
			this.intervalPersister = intervalPersister;
			this.preferences = Preferences.getInstance();
		}

		/**
		 * Transfers all intervals from the persistence storage that are not yet
		 * on the server, to the server.
		 */
		@Override
		public void run() {
			lastTransferedIntervalKey = preferences
					.getOrCreateWorkspaceSetting(UIUtils.getWorkspaceName()).lastTransferedInterval;
			long databaseHighestKey = intervalPersister.getHighestKey();
			if (lastTransferedIntervalKey > databaseHighestKey) {
				// something is amiss, the reported last transfered key in the
				// preferences is higher than the actual last key in the
				// database
				preferences.registerLastTransferedInterval(
						UIUtils.getWorkspaceName(), databaseHighestKey);
			}

			List<IntervalBase> intervalsToTransfer = intervalPersister
					.readIntervals(lastTransferedIntervalKey + 1);

			if (intervalsToTransfer.isEmpty()) {
				return;
			}

			transferIntervals(intervalsToTransfer);

			UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
		}

		private void transferIntervals(List<IntervalBase> intervalsToTransfer) {
			JsonTransferer intervalTransferer = new JsonTransferer();

			int transferRounds = (int) Math.ceil((double) intervalsToTransfer
					.size() / intervalsPerRoundTransfer);
			for (int i = 0; i < transferRounds; i++) {
				int endIndex = i + 1 == transferRounds ? intervalsToTransfer
						.size() : i * intervalsPerRoundTransfer + 1;
				List<IntervalBase> intervalsToTransferInRound = intervalsToTransfer
						.subList(i * intervalsPerRoundTransfer, endIndex);

				if (intervalTransferer
						.sendIntervals(intervalsToTransferInRound)) {
					int roundIntervals = intervalsToTransferInRound.size();
					lastTransferedIntervalKey += roundIntervals;
					preferences.registerLastTransferedInterval(
							UIUtils.getWorkspaceName(),
							lastTransferedIntervalKey);
					preferences.addTransferedIntervals(roundIntervals);
					preferences.setLastTransferedInterval();
					WatchDogGlobals.lastTransactionFailed = false;
				} else {
					WatchDogGlobals.lastTransactionFailed = true;
					WatchDogLogger.getInstance().logSevere(
							"Could not transfer intervals to server!");
					break;
				}
			}

			if (!WatchDogGlobals.lastTransactionFailed) {
				resetDatabase();
			}
		}

		private void resetDatabase() {
			lastTransferedIntervalKey = 0;
			preferences.registerLastTransferedInterval(
					UIUtils.getWorkspaceName(), lastTransferedIntervalKey);
			intervalPersister.clearAndResetMap();
		}
	}
}
