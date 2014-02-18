package nl.tudelft.watchdog.document;


public class Document implements IDocument{
	
	private static final long serialVersionUID = 2L;
	private String fileName;
	private String projectName;
	private DocumentType docType;
	
	public Document(String projectName, String fileName, DocumentType docType){		
		this.projectName = projectName;
		this.fileName = fileName;
		this.docType = docType;
	}
	
	@Override
	public String getProjectName(){
		return projectName;
	}
	
	@Override
	public String getFileName(){
		return fileName;
	}
	@Override
	public DocumentType getDocumentType(){
		return docType;
	}
	@Override
	public void setDocumentType(DocumentType type){
		this.docType = type;
	}
}
