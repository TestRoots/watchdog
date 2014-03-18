package nl.tudelft.watchdog.logic.interval.activityCheckers;

import java.util.TimerTask;

import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentActivateEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentNotifier;
import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.logic.logging.WDLogger;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class TypingCheckerTask extends TimerTask {

	private IUpdateChecker checker;
	private OnInactiveCallBack callback;
	private ITextEditor editor;
	private IWorkbenchPart part;

	public TypingCheckerTask(IWorkbenchPart part, OnInactiveCallBack callback) {
		this.editor = (ITextEditor) part;
		this.part = part;
		checker = new UpdateChecker(editor);
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			if (checker.hasChanged()) {
				// still an active document
			} else {
				// not active anymore
				this.cancel();// stop timer
				listenForReactivation();// listen to changes in open, inactive
										// documents
				callback.onInactive();// callback function
			}
		} catch (EditorClosedPrematurelyException e) {
			WDLogger.logInfo("Editor closed prematurely"); // this can happen
															// when eclipse is
															// closed while the
															// document is still
															// active
		} catch (ContentReaderException e) {
			WDLogger.logInfo("Unavailable doc provider"); // this can happen
															// when a file is
															// moved inside the
															// workspace
		}
	}

	public void listenForReactivation() {
		IDocumentProvider dp = editor.getDocumentProvider();
		final IDocument doc = dp.getDocument(editor.getEditorInput());

		final IDocumentListener docListener = new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				// listen to this event just once, notify that the document is
				// activated, then remove this listener
				DocumentNotifier
						.fireDocumentStartEditingEvent(new DocumentActivateEvent(
								part));
				doc.removeDocumentListener(this);
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		};

		doc.addDocumentListener(docListener);
	}
}
