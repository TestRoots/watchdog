package nl.tudelft.watchdog.eclipse.logic.ui.listeners;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

/** Enriches an {@link IEditorPart} for all user-triggered events. */
public class EditorListener {
	private final ITextEditor editor;

	private IDocument document;
	private IDocumentListener documentListener;
	private CaretListener caretListener;
	private FocusListener focusListener;
	private StyledText styledText;
	private PaintListener paintListener;

	/** Enriches the supplied editor with all suitable listeners. */
	public EditorListener(ITextEditor editor) {
		this.editor = editor;
		listenToDocumentChanges();
		listenToEditorScrolling();
	}

	/**
	 * Adds a document change listener to the supplied editor. Fires a
	 * {@link StartEditingEditorEvent} when a change to a document is made.
	 *
	 * @param partEditor
	 * @throws IllegalArgumentException
	 */
	private void listenToDocumentChanges() throws IllegalArgumentException {
		IDocumentProvider documentProvider = editor.getDocumentProvider();
		document = documentProvider.getDocument(editor.getEditorInput());
		documentListener = new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				/*
				 * Three events exist that can influence the Levenshtein
				 * distance:
				 * 1. Addition. In this case length=0 and text>0,  therefore max(length,text)=text=Levenshtein distance.
				 * 2. Removal. In this case length>0 and text=0, therefore max(length,text)=length=Levenshtein distance.
				 * 3. Modification. In this case length>0 and text>0, therefore max(length,text)>=Levenshtein distance.
				 *
				 * So, in general it holds that modCount >= Levenshtein
				 * distance.
				 */
				int textLength = 0;
				if (event.getText() != null) {
					textLength = event.getText().length();
				}

				int modCount = Math.max(event.getLength(), textLength);
				WatchDogEventType.SUBSEQUENT_EDIT.process(new WatchDogEventType.EditorWithModCount(editor, modCount));
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				WatchDogEventType.START_EDIT.process(editor);
			}
		};
		document.addDocumentListener(documentListener);
	}

	private void listenToEditorScrolling() {
		styledText = (StyledText) editor.getAdapter(Control.class);
		if (styledText == null) {
			return;
		}
		// creates a listener for when the user moves the caret (cursor)
		caretListener = new CaretListener() {
			@Override
			public void caretMoved(CaretEvent event) {
				WatchDogEventType.CARET_MOVED.process(editor);
				// cursor place changed
			}
		};
		styledText.addCaretListener(caretListener);

		// creates a listener for redraws of the view, e.g. when scrolled
		paintListener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				WatchDogEventType.PAINT.process(editor);
			}
		};
		styledText.addPaintListener(paintListener);

		focusListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				WatchDogEventType.ACTIVE_FOCUS.process(editor);
			}
		};
		styledText.addFocusListener(focusListener);
	}

	/** Removes all listeners registered with this editor. */
	public void removeListeners() {
		document.removeDocumentListener(documentListener);
		if (styledText == null) {
			return;
		}
		styledText.removePaintListener(paintListener);
		styledText.removeCaretListener(caretListener);
		styledText.removeFocusListener(focusListener);
	}
}
