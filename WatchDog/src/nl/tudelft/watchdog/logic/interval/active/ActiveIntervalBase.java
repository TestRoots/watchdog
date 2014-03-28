package nl.tudelft.watchdog.logic.interval.active;

import java.util.Date;
import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class ActiveIntervalBase {
	protected Timer checkForChangeTimer;
	protected Date timeOfCreation;
	protected ITextEditor editor;
	protected boolean isClosed;
	protected IWorkbenchPart part;

	/** Constructor. */
	public ActiveIntervalBase(IWorkbenchPart part) {
		this.part = part;
		this.editor = (ITextEditor) part;
		this.timeOfCreation = new Date();
		this.isClosed = false;
	}

	public IWorkbenchPart getPart() {
		return part;
	}

	public ITextEditor getEditor() {
		return editor;
	}

	public Date getTimeOfCreation() {
		return timeOfCreation;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public Timer getTimer() {
		return checkForChangeTimer;
	}

	public void closeInterval() {
		isClosed = true;
		checkForChangeTimer.cancel();
		listenForReactivation();
	}

	public abstract void listenForReactivation();

	public abstract ActivityType getActivityType();

	public abstract void addTimeoutListener(long timeout,
			OnInactiveCallBack callbackWhenFinished);
}
