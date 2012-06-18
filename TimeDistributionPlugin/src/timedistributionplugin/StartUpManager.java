package timeDistributionPlugin;

import org.eclipse.ui.IStartup;

import eclipseUIReader.UIListener;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		MyLogger.logInfo("Plugin startup");
		new UIListener().attachListeners();
	}

}
