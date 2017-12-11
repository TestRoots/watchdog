package nl.tudelft.watchdog.eclipse;

import java.lang.Thread.UncaughtExceptionHandler;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/** The activator class controls the plug-in life cycle */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "nl.tudelft.WatchDog.eclipse";

	/** The shared instance */
	private static Activator plugin;

	/** Our preferenceStore. */
	private ScopedPreferenceStore preferenceStore;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Thread.setDefaultUncaughtExceptionHandler(
				new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						WatchDogLogger.getInstance().logSevere(e);
					}
				});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		WatchDogLogger.getInstance().logInfo("Shutting down Plugin...");
		plugin = null;
		WatchDogGlobals.isActive = false;
		preferenceStore.save();
		super.stop(context);
	}

	/** Returns the shared instance. */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * We are overriding the default-wise returned preference store which has an
	 * {@link InstanceScope} only saving the workbench state to the
	 * {@link ConfigurationScope}, which has an Eclipse-wide configuration
	 * scope.
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new ScopedPreferenceStore(
					ConfigurationScope.INSTANCE, getBundle().getSymbolicName());
		}
		return preferenceStore;
	}

}
