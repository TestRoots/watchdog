package nl.tudelft.watchdog.logic.interval;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;

/**
 * This manager takes care of the repeated transferal of all closed intervals to
 * the server. When the transfer to the server was successful, the intervals are
 * immediately deleted from the local database. Furthermore, it allows the
 * immediate execution of this regularly scheduled task, e.g. when it is needed
 * on exiting.
 */
public class IntervalTransferManager {

	private static int UPDATE_RATE = 3 * 60 * 1000;

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

		private IntervalsTransferTimerTask(IntervalPersister intervalPersister) {
			this.intervalPersister = intervalPersister;
		}

		/**
		 * Transfers all intervals from the persistence storage that are not yet
		 * on the server, to the server.
		 */
		@Override
		public void run() {
			lastTransferedIntervalKey = Preferences.getInstance()
					.getOrCreateWorkspaceSetting(UIUtils.getWorkspaceName()).lastTransferedInterval;
			long databaseHighestKey = intervalPersister.getHighestKey();
			if (lastTransferedIntervalKey > databaseHighestKey) {
				// something is amiss, the reported last transfered key in the
				// preferences is higher than the actual last key in the
				// database
				Preferences.getInstance().registerLastTransferedInterval(
						UIUtils.getWorkspaceName(), databaseHighestKey);
			}

			List<IntervalBase> intervalsToTransfer = intervalPersister
					.readIntervals(lastTransferedIntervalKey + 1);

			if (intervalsToTransfer.isEmpty()) {
				return;
			}

			JsonTransferer intervalTransferer = new JsonTransferer();
			if (intervalTransferer.sendIntervals(intervalsToTransfer)) {
				intervalPersister.clearAndResetDatabase();
				lastTransferedIntervalKey = 0;
				Preferences.getInstance().registerLastTransferedInterval(
						UIUtils.getWorkspaceName(), lastTransferedIntervalKey);
			} else {
				WatchDogLogger.getInstance().logSevere(
						"Could not transfer intervals to server!");
			}
		}
	}
}
