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

public class DocumentChangeListenerAttacher {
	private static List<IWorkbenchPart> editorsWithChangeListeners = new LinkedList<IWorkbenchPart>();

	public static void listenToDocumentChanges(final IWorkbenchPart part)
			throws IllegalArgumentException {
		if (!editorsWithChangeListeners.contains(part)) {
			if (part instanceof ITextEditor) {
				editorsWithChangeListeners.add(part);
				ITextEditor editor = (ITextEditor) part;

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
										new StartEditingEditorEvent(part));
						// just listen 1 time for this event to prevent overflow
						// of events
						document.removeDocumentListener(this);
						editorsWithChangeListeners.remove(part);
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
