package nl.tudelft.watchdog.logic.document;

import java.io.Serializable;

import nl.tudelft.watchdog.util.WatchDogUtils;

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
	private transient String fileName;

	@SerializedName("fn")
	private String fileNameHash;

	/** The file's length, in LoC. */
	@SerializedName("sloc")
	private long sloc;

	/** The type of document. */
	@SerializedName("dt")
	private DocumentType docType;

	private transient String content;

	/** Constructor. */
	public Document(String projectName, String fileName, DocumentType docType,
			String content) {
		this.projectName = projectName;
		this.fileName = fileName;

		String shortenedName = fileName.toLowerCase().replace(".java", "");
		if (shortenedName.startsWith("test") || shortenedName.endsWith("test")) {
			shortenedName = shortenedName.replace("test", "");
			this.fileNameHash = WatchDogUtils.createHash(shortenedName)
					+ "Test";
		} else {
			this.fileNameHash = WatchDogUtils.createHash(shortenedName);
		}
		projectNameHash = WatchDogUtils.createHash(projectName);

		this.docType = docType;
		this.content = content;
		this.sloc = WatchDogUtils.countSLOC(content);
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

	/** @return the contents of the document. */
	public String getContent() {
		return content;
	}

	/** Sets the document type to the supplied type. */
	public void setDocumentType(DocumentType type) {
		this.docType = type;
	}
}
