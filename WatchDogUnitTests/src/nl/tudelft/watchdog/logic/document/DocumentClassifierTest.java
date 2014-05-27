package nl.tudelft.watchdog.logic.document;

import static org.junit.Assert.assertEquals;
import nl.tudelft.watchdog.logic.document.DocumentClassifier;
import nl.tudelft.watchdog.logic.document.DocumentType;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the recognition of a file as a production, a development or an
 * undefined file, as done by the {@link DocumentClassifier}.
 */
public class DocumentClassifierTest {

    /**
     * Tests recognition of a production java class.
     */
    @Test
    public void testProductionDocumentClassification() {
	String contents = "public class ProductionClass{"
		+ "	public ProductionClass{" + "		//do something" + "	}" + "}";
	DocumentType type = DocumentClassifier.classifyDocument(
		"ProductionClass.java", contents);
	assertEquals(DocumentType.PRODUCTION, type);
    }

    /**
     * Tests the recognition of a normal JUnit test file as
     * {@link DocumentType#TEST}..
     */
    @Test
    public void testRealJunitTestDocumentClassification() {
	String contents = "import org.junit.assert;"
		+ "public class TestClass{" + "	@Test"
		+ "	public testSomething{" + "		//do something" + "	}" + "}";
	DocumentType type = DocumentClassifier.classifyDocument(
		"TestClass.java", contents);
	assertEquals(DocumentType.TEST, type);
    }

    /**
     * Tests recognition of a fake junit test class, that should actually be
     * {@link DocumentType#PRODUCTION}.
     */
    @Test
    @Ignore
    public void testFakeJunitDocumentClassification() {
	String contents = "//import org.junit.assert;"
		+ "public class TestClass{" + "//	@Test"
		+ "	public testSomething{" + "		//do something" + "	}" + "}";
	DocumentType type = DocumentClassifier.classifyDocument(
		"TestClass.java", contents);
	assertEquals(DocumentType.PRODUCTION, type);
    }

    /**
     * A class called "TestClass.java", which is not importing Junit, is
     * considered {@link DocumentType#PRODUCTION}.
     */
    @Test
    public void testTestDocumentClassificationWithoutJUnitImports() {
	String contents = "public class TestClass{" + "	@Test"
		+ "	public testSomething{" + "		//do something" + "	}" + "}";
	DocumentType type = DocumentClassifier.classifyDocument(
		"TestClass.java", contents);
	assertEquals(DocumentType.PRODUCTION, type);
    }

    /**
     * Tests whether an ordinary txt file is considered as
     * {@link DocumentType#UNDEFINED}.
     */
    @Test
    public void testUndefinedDocumentClassification() {
	String contents = "The quick brown fox... etc";
	DocumentType type = DocumentClassifier.classifyDocument("somefile.txt",
		contents);
	assertEquals(DocumentType.UNDEFINED, type);
    }

    /**
     * Tests whether a class called "TestClass.txt", which is not importing
     * Junit, is considered {@link DocumentType#UNDEFINED} (because of wrong
     * file extension).
     */
    @Test
    public void testJavaInTxtFile() {
	String contents = "public class TestClass{" + "	@Test"
		+ "	public testSomething{" + "		//do something" + "	}" + "}";
	DocumentType type = DocumentClassifier.classifyDocument(
		"TestClass.txt", contents);
	assertEquals(DocumentType.UNDEFINED, type);
    }

}
