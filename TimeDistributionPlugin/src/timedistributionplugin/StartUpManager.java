package timedistributionplugin;

import org.eclipse.ui.IStartup;

import eclipseUIReader.UIListener;


public class StartUpManager implements IStartup {

	@Override
	public void earlyStartup() {
		System.out.println("startup ready");
		new UIListener().attachListeners();
	}

}
