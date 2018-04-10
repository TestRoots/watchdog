package nl.tudelft.watchdog.core.ui.wizards;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends PlainDocument {
	private static final long serialVersionUID = 6885739047544402702L;
	private int limit;

    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String text, AttributeSet attributeSet) throws BadLocationException {
        if (text == null) {
            return;
        }

        if ((getLength() + text.length()) <= limit) {
            super.insertString(offset, text, attributeSet);
        }
    }
}
