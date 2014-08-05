package nl.tudelft.watchdog.logic.interval.activityCheckers;

import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActivityIntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/** A task for when the user is reading. */
public class ReadingCheckerTask extends CheckerTimerTask {

	/** The text. */
	private final StyledText styledText;

	/** Cursor listener. */
	private CaretListener caretListener;

	/** Paint redraw listener (e.g. move scrollbar). */
	private PaintListener paintListener;

	private boolean isActive;

	/** The editor. */
	private ITextEditor editor;

	/** Constructor. */
	public ReadingCheckerTask(IWorkbenchPart part,
			UserActivityIntervalBase interval) {
		super(interval);
		this.editor = (ITextEditor) part;
		this.styledText = (StyledText) editor.getAdapter(Control.class);

		createListeners();
	}

	/** Creates the listeners for reading. */
	private void createListeners() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// creates a listener for when the user moves the caret (cursor)
				caretListener = new CaretListener() {

					@Override
					public void caretMoved(CaretEvent event) {
						// cursor place changed
						isActive = true;
						// listen just once to not get millions of events fired
						styledText.removeCaretListener(this);
					}
				};

				// creates a listener for redraws of the view, e.g. when
				// scrolled
				paintListener = new PaintListener() {
					@Override
					public void paintControl(PaintEvent e) {
						isActive = true;
						// listen just once to not get millions of events fired
						styledText.removePaintListener(this);
					}
				};
			}
		});
	}

	@Override
	public void run() {
		if (isActive) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					isActive = false;
					// adds the created listeners to the editor, thus executing
					// them
					styledText.addCaretListener(caretListener);
					styledText.addPaintListener(paintListener);
				}
			});
		} else {
			cancel();
			removeListeners();
			interval.closeInterval();
			WatchDogLogger.getInstance().logInfo("closing reading interval.");
		}
	}

	/** Removes all listeners from the caret. */
	private void removeListeners() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				styledText.removeCaretListener(caretListener);
				styledText.removePaintListener(paintListener);
			}
		});
	}
}
