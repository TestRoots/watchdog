package nl.tudelft.watchdog.logic.interval.activityCheckers;

import java.util.TimerTask;

import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentActivateEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentNotifier;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class ReadingCheckerTask extends TimerTask {

	private final StyledText styledText;
	private boolean stillActive;
	private CaretListener caretListener;
	private PaintListener paintListener;
	private OnInactiveCallBack callback;
	private ITextEditor editor;
	private IWorkbenchPart part;

	public ReadingCheckerTask(IWorkbenchPart part, OnInactiveCallBack callback) {
		stillActive = true;
		this.callback = callback;
		this.part = part;
		this.editor = (ITextEditor) part;
		styledText = (StyledText) editor.getAdapter(Control.class);

		createListeners();
	}

	private void createListeners() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				caretListener = new CaretListener() { // cursor place changes
					@Override
					public void caretMoved(CaretEvent event) {
						stillActive = true;
						styledText.removeCaretListener(this); // listen just
																// once to not
																// get millions
																// of events
																// fired
					}
				};

				paintListener = new PaintListener() { // for redraws of the
														// view, e.g. when
														// scrolled
					@Override
					public void paintControl(PaintEvent e) {
						stillActive = true;
						styledText.removePaintListener(this); // listen just
																// once to not
																// get millions
																// of events
																// fired
					}
				};
			}
		});
	}

	@Override
	public void run() {
		if (stillActive) {
			stillActive = false;

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					styledText.addCaretListener(caretListener);
					styledText.addPaintListener(paintListener);
				}
			});

		} else {
			this.cancel();
			removeListeners();
			listenForReactivation();
			callback.onInactive();
		}
	}

	private void removeListeners() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				styledText.removeCaretListener(caretListener);
				styledText.removePaintListener(paintListener);
			}
		});
	}

	public void listenForReactivation() {
		if (!styledText.isDisposed()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!styledText.isDisposed()) {
						styledText.addCaretListener(new CaretListener() { // cursor
																			// place
																			// changes
									@Override
									public void caretMoved(CaretEvent event) {
										DocumentNotifier
												.fireDocumentStartFocusEvent(new DocumentActivateEvent(
														part));
										styledText.removeCaretListener(this); // listen
																				// just
																				// once
																				// to
																				// not
																				// get
																				// millions
																				// of
																				// events
																				// fired
									}
								});

						styledText.addPaintListener(new PaintListener() { // for
																			// redraws
																			// of
																			// the
																			// view,
																			// e.g.
																			// when
																			// scrolled
									@Override
									public void paintControl(PaintEvent e) {
										DocumentNotifier
												.fireDocumentStartFocusEvent(new DocumentActivateEvent(
														part));
										styledText.removePaintListener(this); // listen
																				// just
																				// once
																				// to
																				// not
																				// get
																				// millions
																				// of
																				// events
																				// fired
									}
								});
					}
				}
			});
		}
	}

}
