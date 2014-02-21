package nl.tudelft.watchdog.test.document;

import static org.junit.Assert.assertEquals;
import nl.tudelft.watchdog.document.DocumentClassifier;
import nl.tudelft.watchdog.document.DocumentType;

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
     * The recognition of a Java file that just contains the text "
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
     * production.
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
     * considered production code.
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
     * Tests whether a txt file is considered as undefined.
     */
    @Test
    public void testUndefinedDocumentClassification() {
	String contents = "The quick brown fox... etc";
	DocumentType type = DocumentClassifier.classifyDocument("somefile.txt",
		contents);
	assertEquals(DocumentType.UNDEFINED, type);
    }

    /**
     * A class called "TestClass.txt", which is not importing Junit, is
     * considered undefined (because of wrong file extension).
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
