package nl.tudelft.watchdog.document;

import com.google.gson.annotations.SerializedName;

/** The different types a document may have. */
public enum DocumentType {
	/** A Unit test document */
	@SerializedName("te")
	TEST,

	/** A Production code document. */
	@SerializedName("pr")
	PRODUCTION,

	/** Unknown document type. */
	@SerializedName("un")
	UNDEFINED
}
