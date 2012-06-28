package nl.tudelft.watchdog.document;

public class Document implements IDocument{
	
	private String fileName;
	private DocumentType docType;	
	
	public Document(String fileName, DocumentType docType){
		this.fileName = fileName;
		this.docType = docType;
	}
	
	@Override
	public String getFileName(){
		return fileName;
	}
	@Override
	public DocumentType getDocumentType(){
		return docType;
	}
}
