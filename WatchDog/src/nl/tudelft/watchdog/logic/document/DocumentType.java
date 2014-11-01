package nl.tudelft.watchdog.logic.document;

import com.google.gson.annotations.SerializedName;

/** The different types a document may have. */
public enum DocumentType {
	/** A test document */
	@SerializedName("te")
	TEST,

	/** Likely a test document */
	@SerializedName("lt")
	LIKELY_TEST,

	/** A Production code document. */
	@SerializedName("pr")
	PRODUCTION,

	/** Unknown document type. */
	@SerializedName("un")
	UNDEFINED,

	/** Test Framework like Mockito or Powermock. */
	@SerializedName("tf")
	TEST_FRAMEWORK
}
