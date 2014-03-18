package nl.tudelft.watchdog.document;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Data container which stores information about a document, including its
 * fileName, projectName and the {@link DocumentType}.
 */
public class Document implements Serializable {

	/** Serialization UID. */
	private static final long serialVersionUID = 2L;

	/** The project name. */
	@SerializedName("pn")
	private String projectName;

	/** The file's name. */
	@SerializedName("fn")
	private String fileName;

	/** The type of document. */
	@SerializedName("dt")
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
