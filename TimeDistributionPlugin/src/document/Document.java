package document;

public class Document implements IDocument {
	private String fileName;
	private DocumentType docType;	
	
	public Document(){}
	
	public Document(String fileName, DocumentType docType){
		this.fileName = fileName;
		this.docType = docType;
	}
	
	public void setFileName(String name){
		this.fileName = name;
	}
	
	public void setDocType(DocumentType docType){
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
