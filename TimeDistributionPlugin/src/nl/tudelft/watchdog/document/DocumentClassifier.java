package nl.tudelft.watchdog.document;

public class DocumentClassifier {
	public static DocumentType classifyDocument(String title, String contents){
		if(title.endsWith(".java")){
			if(contents.contains("@Test")){
				return DocumentType.TEST;
			}else{
				return DocumentType.PRODUCTION;
			}
		}else{
			return DocumentType.UNDEFINED;
		}
	}
}
