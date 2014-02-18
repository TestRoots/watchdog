package nl.tudelft.watchdog.document;

public class DocumentClassifier {
	public static DocumentType classifyDocument(String title, String contents){
		String preparedContents = contents.replaceAll("\\s+", " "); //remove unnecessary spaces
		
		if(isJavaFile(title)){
			if(containsJUnitImports(preparedContents) && containsTestAnnotation(preparedContents)){
				return DocumentType.TEST;
			}
			else
				return DocumentType.PRODUCTION;
		}else
			return DocumentType.UNDEFINED;
	}

	private static boolean isJavaFile(String title) {
		if(title.endsWith(".java")){
			return true;
		}else{
			return false;
		}
	}

	private static boolean containsJUnitImports(String contents) {
		if(contents.contains("import org.junit")){
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean containsTestAnnotation(String contents){
		if(contents.contains("@Test")){
			return true;
		}else{
			return false;
		}
	}
}
