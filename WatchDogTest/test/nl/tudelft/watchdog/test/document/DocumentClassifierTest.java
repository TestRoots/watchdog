package nl.tudelft.watchdog.test.document;

import static org.junit.Assert.assertEquals;
import nl.tudelft.watchdog.document.DocumentClassifier;
import nl.tudelft.watchdog.document.DocumentType;

import org.junit.Test;

/**
 * Tests the recognition of a file as a production, a development or an
 * undefined file, as done by the {@link DocumentClassifier}.
 */
public class DocumentClassifierTest {

	@Test
	public void testProductionDocumentClassification() {
		String contents = "public class ProductionClass{"
				+ "	public ProductionClass{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"ProductionClass.java", contents);
		assertEquals(DocumentType.PRODUCTION, type);
	}

	@Test
	public void testTestDocumentClassification() {
		String contents = "import org.junit.assert" + "public class TestClass{"
				+ "	@Test" + "	public testSomething{" + "		//do something"
				+ "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", contents);
		assertEquals(DocumentType.TEST, type);
	}

	@Test
	public void testTestDocumentClassificationWithoutJUnitImports() {
		String contents = "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", contents);
		assertEquals(DocumentType.PRODUCTION, type);
	}

	@Test
	public void testUndefinedDocumentClassification() {
		String contents = "The quick brown fox... etc";
		DocumentType type = DocumentClassifier.classifyDocument("somefile.txt",
				contents);
		assertEquals(DocumentType.UNDEFINED, type);
	}

}
