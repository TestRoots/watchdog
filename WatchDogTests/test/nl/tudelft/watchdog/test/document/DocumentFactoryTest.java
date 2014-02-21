package nl.tudelft.watchdog.test.document;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import nl.tudelft.watchdog.document.Document;
import nl.tudelft.watchdog.document.DocumentFactory;

import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.mockito.Matchers;

public class DocumentFactoryTest {

    @Test
    public void testCreateDocument() {
	String contents = "public class A {}";

	ITextEditor mockedEditor = mock(ITextEditor.class);
	IDocumentProvider mockedProvider = mock(IDocumentProvider.class);
	org.eclipse.jface.text.IDocument mockedDocument = mock(org.eclipse.jface.text.IDocument.class);

	when(mockedEditor.getDocumentProvider()).thenReturn(mockedProvider);
	when(mockedProvider.getDocument(Matchers.anyObject())).thenReturn(
		mockedDocument);
	when(mockedDocument.get()).thenReturn(contents);

	when(mockedEditor.getTitle()).thenReturn("A.java");

	Document doc = new DocumentFactory().createDocument(mockedEditor);

	Assert.assertEquals("A.java", doc.getFileName());
    }

}
