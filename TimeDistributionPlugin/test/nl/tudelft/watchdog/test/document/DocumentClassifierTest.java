package nl.tudelft.watchdog.test.document;

import junit.framework.Assert;
import nl.tudelft.watchdog.document.DocumentClassifier;
import nl.tudelft.watchdog.document.DocumentType;

import org.junit.Test;

public class DocumentClassifierTest {

	@Test
	public void testProductionDocumentClassification() {
		String contents = 
				"public class ProductionClass{" +
				"	public ProductionClass{" +
				"		//do something" +
				"	}" +
				"}";
		DocumentType type = DocumentClassifier.classifyDocument("ProductionClass.java", contents);
		Assert.assertEquals(DocumentType.PRODUCTION, type);
	}
	
	@Test
	public void testTestDocumentClassification() {
		String contents = 
				"public class TestClass{" +
				"	@Test" +
				"	public testSomething{" +
				"		//do something" +
				"	}" +
				"}";
		DocumentType type = DocumentClassifier.classifyDocument("TestClass.java", contents);
		Assert.assertEquals(DocumentType.TEST, type);
	}
	
	@Test
	public void testUndefinedDocumentClassification() {
		String contents = 
				"The quick brown fox... etc";
		DocumentType type = DocumentClassifier.classifyDocument("somefile.txt", contents);
		Assert.assertEquals(DocumentType.UNDEFINED, type);
	}
	

}
