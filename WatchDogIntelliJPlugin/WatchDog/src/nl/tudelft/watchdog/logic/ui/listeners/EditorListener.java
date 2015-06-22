package nl.tudelft.watchdog.logic.ui.listeners;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.events.EditorEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;

/** Editor listener for all user-triggered events. */
public class EditorListener {
	private final Editor editor;
	private final EventManager eventManager;

	private Document document;
	private DocumentListener documentListener;
	private CaretListener caretListener;
	private FocusListener focusListener;
	private VisibleAreaListener  visibleAreaListener;

	/** Enriches the supplied editor with all suitable listeners. */
	public EditorListener(EventManager eventManager, Editor editor) {
		this.eventManager = eventManager;
		this.editor = editor;
        this.document = editor.getDocument();
		listenToDocumentChanges();
		listenToEditorScrolling();
	}

	/**
	 * Adds a document change listener to the supplied editor. Fires a
	 * {@link EditorEvent} when a change to a document is made.
	 * 
	 * @throws IllegalArgumentException
	 */
	private void listenToDocumentChanges() throws IllegalArgumentException {
		documentListener = new DocumentListener() {

            @Override
            public void beforeDocumentChange(DocumentEvent event) {
                eventManager.update(new EditorEvent(editor, EventType.START_EDIT));
            }

            @Override
			public void documentChanged(DocumentEvent event) {
                eventManager.update(new EditorEvent(editor, EventType.SUBSEQUENT_EDIT));
			}

		};
		document.addDocumentListener(documentListener);
	}

	private void listenToEditorScrolling() {
		// creates a listener for when the user moves the caret (cursor)
		caretListener = new CaretListener() {
            @Override
            public void caretPositionChanged(CaretEvent e) {
                eventManager.update(new EditorEvent(editor, EventType.CARET_MOVED));
                // cursor place changed
            }

            @Override
            public void caretAdded(CaretEvent e) {
                // Intentionally left blank
            }

            @Override
            public void caretRemoved(CaretEvent e) {
                // Intentionally left blank
            }
		};
		editor.getCaretModel().addCaretListener(caretListener);

		// creates a listener for redraws of the view, e.g. when scrolled
		visibleAreaListener = new VisibleAreaListener() {
            @Override
            public void visibleAreaChanged(VisibleAreaEvent e) {
                if(e.getEditor().isViewer()) {
                    eventManager.update(new EditorEvent(editor, EventType.PAINT));
                }
            }
        };
		editor.getScrollingModel().addVisibleAreaListener(visibleAreaListener);

		focusListener = new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                eventManager.update(new EditorEvent(editor, EventType.ACTIVE_FOCUS));
            }

            @Override
			public void focusLost(FocusEvent e) {
			}

		};
		editor.getContentComponent().addFocusListener(focusListener);
	}

	/** Removes all listeners registered with this editor. */
	public void removeListeners() {
		document.removeDocumentListener(documentListener);
		if (editor == null) {
			return;
		}
		editor.getCaretModel().removeCaretListener(caretListener);
        editor.getScrollingModel().removeVisibleAreaListener(visibleAreaListener);
        editor.getContentComponent().removeFocusListener(focusListener);
	}
}
