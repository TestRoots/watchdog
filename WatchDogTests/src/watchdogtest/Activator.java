package watchdogtest;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Activator for the test plugin.
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "WatchDogTest"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /**
     * Initialize plugin.
     */
    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
    }

    /**
     * Stop plugin.
     */
    @Override
    public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
    }

    /**
     * Returns the shared instance
     */
    public static Activator getDefault() {
	return plugin;
    }

}
