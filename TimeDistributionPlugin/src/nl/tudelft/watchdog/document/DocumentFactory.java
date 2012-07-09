package nl.tudelft.watchdog.document;

import nl.tudelft.watchdog.util.TextEditorContentReader;

import org.eclipse.ui.texteditor.ITextEditor;

public class DocumentFactory {
	public static Document createDocument(ITextEditor editor){
		return new Document(editor.getTitle(), DocumentClassifier.classifyDocument(editor.getTitle(), TextEditorContentReader.getEditorContent(editor)));
	}
}
