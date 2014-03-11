package nl.tudelft.watchdog.document;

import java.io.Serializable;

/**
 * Data container which stores information about a document, including its
 * fileName, projectName and the {@link DocumentType}.
 */
public class Document implements Serializable {

	/** Serialization UID. */
	private static final long serialVersionUID = 2L;

	/** The file's name. */
	private String fileName;

	/** The project name. */
	private String projectName;

	/** The type of document. */
	private DocumentType docType;

	/** Constructor. */
	public Document(String projectName, String fileName, DocumentType docType) {
		this.projectName = projectName;
		this.fileName = fileName;
		this.docType = docType;
	}

	/** @return the project's name */
	public String getProjectName() {
		return projectName;
	}

	/** @return the file's name */
	public String getFileName() {
		return fileName;
	}

	/** @return the document type */
	public DocumentType getDocumentType() {
		return docType;
	}

	/** Sets the document type to the supplied type. */
	public void setDocumentType(DocumentType type) {
		this.docType = type;
	}
}
