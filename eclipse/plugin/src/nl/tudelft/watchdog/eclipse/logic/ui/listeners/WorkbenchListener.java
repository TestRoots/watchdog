package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.eclipse.logic.InitializationManager;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.DebugEventListener;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis.EclipseMarkupModelListener;

/**
 * Sets up the listeners for eclipse UI events and registers the shutdown
 * listeners.
 */
public class WorkbenchListener {
	/** The serialization manager. */
	private TransferManager transferManager;

	/** The debug event manager used to process debug events. */
	private TrackingEventManager trackingEventManager;

	/**
	 * The window listener. An Eclipse window is the whole Eclipse application
	 * window.
	 */
	private WindowListener windowListener;

	private IWorkbench workbench;

	private EclipseMarkupModelListener markupModelListener;

	/**
	 * Constructor.
	 *
	 * @param TrackingEventManager
	 */
	public WorkbenchListener(TrackingEventManager TrackingEventManager,
			TransferManager transferManager) {
		this.trackingEventManager = TrackingEventManager;
		this.transferManager = transferManager;
		this.workbench = PlatformUI.getWorkbench();

		WatchDogEventType.START_IDE.process(workbench);
	}

	public void attachListeners() {
		this.windowListener = new WindowListener(this.trackingEventManager);
		this.workbench.addWindowListener(windowListener);
		addListenersToAlreadyOpenWindows();
		new JUnitListener();
		new GeneralActivityListener(workbench.getDisplay());
		addDebuggerListeners();
		addStaticAnalysisListeners();
		addShutdownListeners();
	}

	/** Initializes the listeners for debug intervals and events. */
	private void addDebuggerListeners() {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		debugPlugin.addDebugEventListener(
				new DebuggerListener());
		debugPlugin.getBreakpointManager().addBreakpointListener(
				new BreakpointListener(trackingEventManager));
		debugPlugin.addDebugEventListener(
				new DebugEventListener(trackingEventManager));
	}

    private void addStaticAnalysisListeners() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        this.markupModelListener = new EclipseMarkupModelListener(this.trackingEventManager);
        workspace.addResourceChangeListener(this.markupModelListener, IResourceChangeEvent.POST_BUILD);
        try {
            workspace.getRoot().accept(this.markupModelListener.createVisitor(false));
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

	/** The shutdown listeners, executed when Eclipse is shutdown. */
	private void addShutdownListeners() {
		workbench.addWorkbenchListener(new IWorkbenchListener() {

			@Override
			public boolean preShutdown(final IWorkbench workbench,
					final boolean forced) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				workspace.removeResourceChangeListener(getMarkupModelListener());
				WatchDogEventType.END_IDE.process(workbench);
				InitializationManager.getInstance().getIntervalManager().closeAllIntervals();
				transferManager.sendItemsImmediately();
				return true;
			}

			@Override
			public void postShutdown(final IWorkbench workbench) {
				InitializationManager.getInstance().shutdown();
			}
		});
	}

	/**
	 * If windows are already open when the listener registration from WatchDog
	 * starts (e.g. due to saved Eclipse workspace state), add these listeners
	 * to already opened windows.
	 *
	 * This is usually the single Eclipse application window.
	 */
	private void addListenersToAlreadyOpenWindows() {
		for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
			windowListener.windowOpened(window);
		}
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		windowListener.windowActivated(activeWindow);
	}

	public EclipseMarkupModelListener getMarkupModelListener() {
		return markupModelListener;
	}

}
