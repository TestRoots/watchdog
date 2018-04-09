package nl.tudelft.watchdog.logic.document;

import static org.junit.Assert.assertEquals;
import nl.tudelft.watchdog.core.logic.document.*;

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
	public void production() {
		String contents = "public class ProductionClass{"
				+ "	public ProductionClass{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"ProductionClass.java", "filepath", contents);
		assertEquals(DocumentType.PRODUCTION, type);
	}

	/**
	 * Tests the recognition of a normal JUnit4 test file as
	 * {@link DocumentType#TEST}.
	 */
	@Test
	public void junit_test() {
		String contents = "import org.junit.assert;"
				+ "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "filepath", contents);
		assertEquals(DocumentType.TEST, type);
	}

	/**
	 * Tests the recognition of a normal JUnit3 test file as
	 * {@link DocumentType#TEST}.
	 */
	@Test
	public void junit_3_test() {
		String contents = "import junit.framework.TestCase;"
				+ "public class TestClass extends TestCase {" + "	"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "filepath", contents);
		assertEquals(DocumentType.TEST, type);
	}

	/**
	 * Tests the recognition of a normal JUnit test file as
	 * {@link DocumentType#FILENAME_TEST}.
	 */
	@Test
	public void not_a_real_test_only_has_filename_test() {
		String contents = "import org.junit.assert;"
				+ "public class TestClass{" + "	@NotATest"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "filepath", contents);
		assertEquals(DocumentType.FILENAME_TEST, type);
	}

	/**
	 * Tests the recognition of a normal JUnit test file as
	 * {@link DocumentType#FILENAME_TEST}.
	 */
	@Test
	public void not_a_real_test_in_test_package() {
		String contents = "import org.junit.assert;"
				+ "public class TestClass{" + "	@NotATest"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"Class.java", "src/test/package/", contents);
		assertEquals(DocumentType.PATHNAMME_TEST, type);
	}

	/**
	 * Tests the recognition of a normal JUnit test file as
	 * {@link DocumentType#FILENAME_TEST}.
	 */
	@Test
	public void not_a_real_test_in_test_package_with_filename_test() {
		String contents = "import org.junit.assert;"
				+ "public class TestClass{" + "	@NotATest"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "src/test/package/", contents);
		assertEquals(DocumentType.FILENAME_TEST, type);
	}

	/**
	 * Tests the recognition of a static JUnit test file as
	 * {@link DocumentType#TEST}.
	 */
	@Test
	public void real_test_with_filename_test() {
		String contents = "import static org.junit.assert;"
				+ "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "", contents);
		assertEquals(DocumentType.TEST, type);
	}

	/**
	 * Tests the recognition of a normal JUnit test file as
	 * {@link DocumentType#TEST}.
	 */
	@Test
	public void real_testng_test_with_filename_test() {
		String contents = "import org.testng.assert;"
				+ "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "", contents);
		assertEquals(DocumentType.TEST, type);
	}

	/**
	 * Tests the recognition of a normal JUnit test file as
	 * {@link DocumentType#TEST}.
	 */
	@Test
	public void test_framework() {
		String contents = "import org.mockito.verify;"
				+ "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "", contents);
		assertEquals(DocumentType.TEST_FRAMEWORK, type);
	}

	/**
	 * Tests recognition of a fake junit test class, that should actually be
	 * {@link DocumentType#PRODUCTION}.
	 */
	@Test
	@Ignore
	public void fake_junit_is_production() {
		String contents = "//import org.junit.assert;"
				+ "public class TestClass{" + "//	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "", contents);
		assertEquals(DocumentType.PRODUCTION, type);
	}

	/**
	 * A class called "TestClass.java", which is not importing Junit, is
	 * considered {@link DocumentType#FILENAME_TEST}.
	 */
	@Test
	public void no_junit_imports_filename_test() {
		String contents = "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.java", "", contents);
		assertEquals(DocumentType.FILENAME_TEST, type);
	}

	/**
	 * Tests whether an ordinary txt file is considered as
	 * {@link DocumentType#UNDEFINED}.
	 */
	@Test
	public void no_java_class_unknown_classification() {
		String contents = "The quick brown fox... etc";
		DocumentType type = DocumentClassifier.classifyDocument("somefile.txt",
				"", contents);
		assertEquals(DocumentType.UNDEFINED, type);
	}

	/**
	 * Tests that a .java file with arbitrary content but which contains test in
	 * its filename is considered a test.
	 */
	@Test
	public void no_java_class_with_filename_test() {
		String contents = "The quick brown fox... etc";
		DocumentType type = DocumentClassifier.classifyDocument(
				"someTestfile.java", "", contents);
		assertEquals(DocumentType.FILENAME_TEST, type);
	}

	/**
	 * Tests that a .txt file with arbitrary content but which contains test in
	 * its filename is not considered a test.
	 */
	@Test
	public void no_java_class_with_no_filename_test() {
		String contents = "The quick brown fox... etc";
		DocumentType type = DocumentClassifier.classifyDocument(
				"someTestfile.txt", "", contents);
		assertEquals(DocumentType.UNDEFINED, type);
	}

	/**
	 * Tests whether a class called "TestClass.txt", which is not importing
	 * Junit, is considered {@link DocumentType#UNDEFINED} (because of wrong
	 * file extension).
	 */
	@Test
	public void no_java_extension_has_unknown_classification() {
		String contents = "public class TestClass{" + "	@Test"
				+ "	public testSomething{" + "		//do something" + "	}" + "}";
		DocumentType type = DocumentClassifier.classifyDocument(
				"TestClass.txt", "", contents);
		assertEquals(DocumentType.UNDEFINED, type);
	}

}
