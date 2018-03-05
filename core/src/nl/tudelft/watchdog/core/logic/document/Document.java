package nl.tudelft.watchdog.core.logic.document;

import java.io.Serializable;

import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;

import com.google.gson.annotations.SerializedName;

/**
 * Data container which stores information about a document, including its
 * fileName, projectName and the {@link DocumentType}.
 */
public class Document implements Serializable {

	/** Serialization UID. */
	private static final long serialVersionUID = 3L;

	/** The project name. */
	private transient String projectName;

	@SerializedName("pn")
	private String projectNameHash;

	/** The file's name. */
	private transient String name;

	@SerializedName("fn")
	private String nameHash;

	/** The file's length, in LoC. */
	@SerializedName("sloc")
	private long sloc;

	/** The type of document. */
	@SerializedName("dt")
	private DocumentType docType;

	private transient String filePath;

	private transient String content;

	/** Constructor. */
	public Document(String projectName, String fileName, String filePath,
			String content) {
		this.projectName = projectName;
		this.name = fileName;
		this.filePath = filePath;
		this.content = content;
	}

	/** @return the project's name */
	public String getProjectName() {
		return projectName;
	}

	/** @return the file's name */
	public String getFileName() {
		return name;
	}

	/** @return the document type */
	public DocumentType getDocumentType() {
		return docType;
	}

	/** @return the contents of the document. */
	public String getContent() {
		return content;
	}

	/** Sets the document type to the supplied type. */
	public void setDocumentType(DocumentType type) {
		this.docType = type;
	}

	/** Prepares this document to extract statistics out of it. */
	public Document prepareDocument() {
		this.nameHash = WatchDogUtilsBase.createFileNameHash(name);
		if (projectName != null) {
			this.projectNameHash = WatchDogUtilsBase.createHash(projectName);
		}
		if (name != null && content != null) {
			this.sloc = WatchDogUtilsBase.countSLOC(content);
			this.docType = DocumentClassifier.classifyDocument(name, filePath,
					content);
		}
		return this;
	}
}
