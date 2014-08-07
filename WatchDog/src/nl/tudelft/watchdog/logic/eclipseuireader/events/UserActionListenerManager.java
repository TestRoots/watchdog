package nl.tudelft.watchdog.logic.eclipseuireader.events;

import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StartEditingEditorEvent;

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

/** Enriches an {@link IEditorPart} for all user-triggered events. */
public class UserActionListenerManager {
	private IEditorPart editor;
	private UserActionManager userActionManager;
	private IDocument document;
	private IDocumentListener documentListener;
	private CaretListener caretListener;
	private FocusListener focusListener;
	private StyledText styledText;
	private PaintListener paintListener;

	/** Enriches the supplied editor with all suitable listeners. */
	public UserActionListenerManager(UserActionManager userActionManager,
			IEditorPart editor) {
		this.userActionManager = userActionManager;
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
		if (editor instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) this.editor;

			IDocumentProvider documentProvider = editor.getDocumentProvider();
			document = documentProvider.getDocument(editor.getEditorInput());
			documentListener = new IDocumentListener() {

				@Override
				public void documentChanged(DocumentEvent event) {
					userActionManager
							.update(new StartEditingEditorEvent(editor));
				}

				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
				}
			};
			document.addDocumentListener(documentListener);
		} else {
			throw new IllegalArgumentException(
					"Part was not instance of ITextEditor");
		}
	}

	private void listenToEditorScrolling() {
		if (!(editor instanceof ITextEditor)) {
			return;
		}

		styledText = (StyledText) editor.getAdapter(Control.class);
		if (styledText == null) {
			return;
		}
		// creates a listener for when the user moves the caret (cursor)
		caretListener = new CaretListener() {
			@Override
			public void caretMoved(CaretEvent event) {
				// TODO (MMB) event needs updating
				// cursor place changed
				userActionManager.update(new FocusStartEditorEvent(editor));
			}
		};
		styledText.addCaretListener(caretListener);

		// creates a listener for redraws of the view, e.g. when scrolled
		paintListener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				// TODO (MMB) event needs updating
				userActionManager.update(new FocusStartEditorEvent(editor));
			}
		};
		styledText.addPaintListener(paintListener);

		focusListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				userActionManager.update(new FocusEndEditorEvent(editor));
			}

			@Override
			public void focusGained(FocusEvent e) {
				userActionManager.update(new FocusStartEditorEvent(editor));
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
