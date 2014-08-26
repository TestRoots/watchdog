package nl.tudelft.watchdog.logic.document;

/**
 * Estimates the nature of a document into one of {@link DocumentType}.
 */
public class DocumentClassifier {
	/**
	 * Classifies the document type of the given document, by analyzing its
	 * filename and its contents.
	 */
	public static DocumentType classifyDocument(String fileName,
			String fileContents) {
		// Collapse multiple spaces in a row to one
		String preparedContents = fileContents.replaceAll("\\s+", " ");

		if (isJavaFile(fileName)) {
			if (containsJUnitImports(preparedContents)
					&& containsTestAnnotation(preparedContents)) {
				return DocumentType.TEST;
			} else {
				return DocumentType.PRODUCTION;
			}
		} else {
			return DocumentType.UNDEFINED;
		}
	}

	/**
	 * @return <code>true</code> if fileName ends in .java
	 */
	private static boolean isJavaFile(String fileName) {
		if (fileName.endsWith(".java")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return <code>true</code> if there's an import for org.junit
	 */
	private static boolean containsJUnitImports(String fileContents) {
		if (fileContents.contains("import org.junit")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return <code>true</code> if the editor contains (at least one) @Test
	 *         annotation.
	 */
	private static boolean containsTestAnnotation(String contents) {
		if (contents.contains("@Test")) {
			return true;
		} else {
			return false;
		}
	}

}
