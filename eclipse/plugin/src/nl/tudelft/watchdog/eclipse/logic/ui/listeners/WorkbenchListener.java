package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.jfree.chart.event.MarkerChangeListener;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.eclipse.logic.InitializationManager;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.BreakpointListener;
import nl.tudelft.watchdog.eclipse.logic.event.listeners.DebugEventListener;
import nl.tudelft.watchdog.eclipse.logic.network.TransferManager;
import nl.tudelft.watchdog.eclipse.logic.ui.WatchDogEventManager;

/**
 * Sets up the listeners for eclipse UI events and registers the shutdown
 * listeners.
 */
public class WorkbenchListener {
	/** The serialization manager. */
	private TransferManager transferManager;

	/** The editorObservable. */
	private WatchDogEventManager watchDogEventManager;

	/** The debug event manager used to process debug events. */
	private TrackingEventManager TrackingEventManager;

	/**
	 * The window listener. An Eclipse window is the whole Eclipse application
	 * window.
	 */
	private WindowListener windowListener;

	private IWorkbench workbench;

	/**
	 * Constructor.
	 *
	 * @param TrackingEventManager
	 */
	public WorkbenchListener(WatchDogEventManager userActionManager,
			TrackingEventManager TrackingEventManager,
			TransferManager transferManager) {
		this.watchDogEventManager = userActionManager;
		this.TrackingEventManager = TrackingEventManager;
		this.transferManager = transferManager;
		this.workbench = PlatformUI.getWorkbench();
	}

	/**
	 * Adds listeners to Workbench including already opened windows and
	 * registers shutdown and debugger listeners.
	 */
	public void attachListeners() {
		watchDogEventManager
				.update(new WatchDogEvent(workbench, EventType.START_IDE));
		windowListener = new WindowListener(watchDogEventManager);
		workbench.addWindowListener(windowListener);
		addListenersToAlreadyOpenWindows();
		new JUnitListener(watchDogEventManager);
		new GeneralActivityListener(watchDogEventManager,
				workbench.getDisplay());
		addDebuggerListeners();
		addShutdownListeners();
		// addStaticAnalysisListeners();
	}

	private void addStaticAnalysisListeners() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new IResourceChangeListener() {

			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					event.getDelta().accept(new IResourceDeltaVisitor() {

						@Override
						public boolean visit(IResourceDelta delta) throws CoreException {
							for (IMarkerDelta markerDelta : delta.getMarkerDeltas()) {
								IMarker marker = markerDelta.getMarker();
								if (marker.exists()) {
//									System.out.println(marker.getType());
//									System.out.println(marker.getAttributes().entrySet().stream()
//											.reduce("", (a,b) -> a + b.getKey() + "  " + b.getValue(), (a,b) -> a + "   " + b));
//									System.out.println(marker.getId() + ": " + marker.getAttribute(IMarker.MESSAGE).toString());
//									System.out.println(marker.getId() + ": " + marker.getAttribute("arguments").toString());
									System.out.println(interpolateArguments(marker.getResource().getParent().getName(), marker.getAttribute(IMarker.MESSAGE, ""), marker.getAttribute("arguments", "")));
								} else {
									System.out.println(marker.getId());
								}
							}
							return true;
						}

						private String interpolateArguments(String packageName, String message, String arguments) {
							String[] numberAndActualArguments = arguments.split(":");
							int numArguments = Integer.parseInt(numberAndActualArguments[0]);
							String[] individualArguments = numberAndActualArguments[1].split("#");
							String sanitizedMessage = message;
							for (int i = 0; i < numArguments; i++) {
								sanitizedMessage = sanitizedMessage.replaceFirst(individualArguments[i].replaceAll(packageName,  ""), "");
							}
							return sanitizedMessage;
						}
					});
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}, IResourceChangeEvent.POST_CHANGE);
	}

	/** Initializes the listeners for debug intervals and events. */
	private void addDebuggerListeners() {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		debugPlugin.addDebugEventListener(
				new DebuggerListener(watchDogEventManager));
		debugPlugin.getBreakpointManager().addBreakpointListener(
				new BreakpointListener(TrackingEventManager));
		debugPlugin.addDebugEventListener(
				new DebugEventListener(TrackingEventManager));
	}

	/** The shutdown listeners, executed when Eclipse is shutdown. */
	private void addShutdownListeners() {
		workbench.addWorkbenchListener(new IWorkbenchListener() {

			private InitializationManager initializationManager;

			@Override
			public boolean preShutdown(final IWorkbench workbench,
					final boolean forced) {
				initializationManager = InitializationManager.getInstance();
				watchDogEventManager.update(
						new WatchDogEvent(workbench, EventType.END_IDE));
				initializationManager.getIntervalManager().closeAllIntervals();
				transferManager.sendItemsImmediately();
				return true;
			}

			@Override
			public void postShutdown(final IWorkbench workbench) {
				initializationManager.shutdown();
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

}
