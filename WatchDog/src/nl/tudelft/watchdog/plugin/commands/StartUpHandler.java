package nl.tudelft.watchdog.plugin.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import nl.tudelft.watchdog.interval.IIntervalKeeper;
import nl.tudelft.watchdog.interval.IntervalKeeper;
import nl.tudelft.watchdog.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.interval.events.IIntervalListener;
import nl.tudelft.watchdog.interval.events.NewIntervalEvent;
import nl.tudelft.watchdog.plugin.logging.WDLogger;

import org.eclipse.ui.IStartup;

public class StartUpHandler implements IStartup {

	@Override
	public void earlyStartup() {
		setUpLogger();
		WDLogger.logInfo("Starting up...");
		IIntervalKeeper intervalKeeper = IntervalKeeper.getInstance();
		intervalKeeper.addIntervalListener(new IIntervalListener() {

			@Override
			public void onNewInterval(NewIntervalEvent evt) {
				WDLogger.logInfo("New interval: "
						+ evt.getInterval().getEditor().getTitle());
			}

			@Override
			public void onClosingInterval(ClosingIntervalEvent evt) {
				WDLogger.logInfo("Closing interval_ "
						+ evt.getInterval().getDocument().getFileName()
						+ " \n " + evt.getInterval().getStart() + " - "
						+ evt.getInterval().getEnd());
			}
		});
	}

	private void setUpLogger() {
		SimpleFormatter fmt = new SimpleFormatter();

		try {
			File parent = new File("watchdog/logs/");
			parent.mkdirs();

			FileHandler fileHandler = new FileHandler(
					"watchdog/logs/watchdoglog.log", true);
			fileHandler.setFormatter(fmt);
			WDLogger.addHandler(fileHandler, Level.ALL);
		} catch (SecurityException e) {
			WDLogger.logSevere(e.getMessage());
		} catch (IOException e) {
			WDLogger.logSevere(e.getMessage());
		}
	}
}