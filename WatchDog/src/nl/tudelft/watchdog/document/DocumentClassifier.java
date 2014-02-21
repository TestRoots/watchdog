package nl.tudelft.watchdog.document;

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
		// remove unnecessary spaces
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

	private static boolean isJavaFile(String title) {
		if (title.endsWith(".java")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean containsJUnitImports(String contents) {
		if (contents.contains("import org.junit")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean containsTestAnnotation(String contents) {
		if (contents.contains("@Test")) {
			return true;
		} else {
			return false;
		}
	}
}
