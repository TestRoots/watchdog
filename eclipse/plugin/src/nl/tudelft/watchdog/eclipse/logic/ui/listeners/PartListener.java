package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis.ResourceAndResourceDeltaVisitor;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A listener on parts. Eclipse parts can be views or editors. We are only
 * interested in parts that are ITextEditors.
 */
public class PartListener implements IPartListener {

	private final TrackingEventManager trackingEventManager;

	PartListener(TrackingEventManager trackingEventManager) {
		this.trackingEventManager = trackingEventManager;
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editor = (ITextEditor) part;
			new EditorListener(editor);

			try {
				ResourceAndResourceDeltaVisitor visitor = new ResourceAndResourceDeltaVisitor(trackingEventManager, new HashMap<>(), true);
				WatchDogUtils.getFile(editor).accept(visitor);
			} catch (IllegalArgumentException | CoreException ignored) {
			}
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			WatchDogEventType.INACTIVE_FOCUS.process(part);
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			WatchDogEventType.INACTIVE_FOCUS.process(part);
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			WatchDogEventType.ACTIVE_FOCUS.process(part);
		}
	}

}
