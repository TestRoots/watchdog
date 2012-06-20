package timeDistributionPlugin;

import interval.IntervalManager;

import org.eclipse.ui.IStartup;

import eclipseUIReader.UIListener;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		MyLogger.logInfo("Plugin startup");
		
		
		
		IntervalManager manager = new IntervalManager();
		
	}

}
