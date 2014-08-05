package nl.tudelft.watchdog.logic.eclipseuireader;

import java.util.LinkedList;
import java.util.List;

import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StartEditingEditorEvent;
import nl.tudelft.watchdog.logic.interval.IntervalManager;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/** Manages and attaches a list of change listeners for document modifications. */
public class DocumentChangeListenerAttacher {
	private static List<IWorkbenchPart> editorsWithChangeListeners = new LinkedList<IWorkbenchPart>();

	/**
	 * Adds a document change listener to the supplied editor. Fires a
	 * {@link StartEditingEditorEvent} when a change to a document is made.
	 * 
	 * @param partEditor
	 * @throws IllegalArgumentException
	 */
	public static void listenToDocumentChanges(final IWorkbenchPart partEditor)
			throws IllegalArgumentException {
		if (!editorsWithChangeListeners.contains(partEditor)) {
			if (partEditor instanceof ITextEditor) {
				editorsWithChangeListeners.add(partEditor);
				ITextEditor editor = (ITextEditor) partEditor;

				IDocumentProvider documentProvider = editor
						.getDocumentProvider();
				final IDocument document = documentProvider.getDocument(editor
						.getEditorInput());
				document.addDocumentListener(new IDocumentListener() {

					@Override
					public void documentChanged(DocumentEvent event) {
						IntervalManager
								.getInstance()
								.getEditorObserveable()
								.notifyObservers(
										new StartEditingEditorEvent(partEditor));
						// just listen 1 time for this event to prevent overflow
						// of events
						document.removeDocumentListener(this);
						editorsWithChangeListeners.remove(partEditor);
					}

					@Override
					public void documentAboutToBeChanged(DocumentEvent event) {
					}
				});
			} else {
				throw new IllegalArgumentException(
						"Part was not instance of ITextEditor");
			}
		}

	}
}
