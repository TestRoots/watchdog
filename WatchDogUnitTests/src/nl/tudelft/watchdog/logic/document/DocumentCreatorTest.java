package nl.tudelft.watchdog.logic.document;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentCreator;
import nl.tudelft.watchdog.logic.document.DocumentType;

import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.mockito.Matchers;

/**
 * Mock tests for {@link DocumentCreator}.
 */
public class DocumentCreatorTest {

	/**
	 * Tests whether the creation of a Java production class via the
	 * {@link DocumentCreator} actually returns said class, and that the
	 * document classification returns the correct type.
	 */
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

		Document doc = new DocumentCreator().createDocument(mockedEditor);

		assertEquals("A.java", doc.getFileName());

		doc.prepareDocument();
		assertEquals(DocumentType.PRODUCTION, doc.getDocumentType());
	}

}
