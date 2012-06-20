package util;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class TextEditorContentReader {
	public static String getEditorContent(ITextEditor editor){
		IDocumentProvider dp = editor.getDocumentProvider();
        final IDocument doc = dp.getDocument(editor.getEditorInput());
        return doc.get();
	}
}
