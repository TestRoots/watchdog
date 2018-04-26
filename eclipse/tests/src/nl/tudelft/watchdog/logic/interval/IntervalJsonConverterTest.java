package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.mockito.Mockito;

import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IDEOpenInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;
import nl.tudelft.watchdog.eclipse.logic.document.EditorWrapper;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;
import nl.tudelft.watchdog.logic.network.JsonConverterTestBase;

/**
 * Test the transfer from {@link IntervalBase}s to JSon.
 */
public class IntervalJsonConverterTest extends JsonConverterTestBase {

	private JsonTransferer transferer = new JsonTransferer();

	/** Tests the format of the returned Json representation. */
	@Test
	public void reading_interval() {
		ReadingInterval interval = new ReadingInterval(null, new Date());
		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"re\",\"ts\":1,\"te\":2,\"ss\":\"\","
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	/**
	 * Tests the format of the returned Json representation, if one of the
	 * typing intervals does not have its ending document properly set.
	 */
	@Test
	public void typing_interval_missing_document() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(new EditorWrapper(editor), new Date());
		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"modCountDiff\":0,\"charLengthDiff\":0,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"ty\",\"ts\":1,\"te\":2,\"ss\":\"\","
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void typing_interval_two_different_intervals() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(new EditorWrapper(editor), new Date());
		interval.setDocument(new Document("Project", "filepath", "Production.java", "blah-document"));
		interval.setEndingDocument(new Document("Project", "Production.java", "filepath", "blah-document"));

		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"endingDocument\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"diff\":0,\"modCountDiff\":0,\"charLengthDiff\":0,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"ty\",\"ts\":1,\"te\":2,\"ss\":\"\","
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	@Test
	public void typing_intervals_twice_same_interval() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(new EditorWrapper(editor), new Date(1));
		interval.setDocument(new Document("Project", "filepath", "Production.java", "blah-document"));
		interval.setEndingDocument(new Document("Project", "filepath", "Production.java", "blah-document"));
		interval.close();
		sleepABit();

		ArrayList<WatchDogItem> intervals = new ArrayList<>();
		intervals.add(interval);

		assertEquals(
				"[{\"endingDocument\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"diff\":0,\"modCountDiff\":0,\"charLengthDiff\":0,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"it\":\"ty\",\"ts\":1,"
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	@Test
	public void typing_interval_diff_addition() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(new EditorWrapper(editor), new Date(1));
		interval.setDocument(new Document("Project", "filepath", "Production.java", "blah-document"));
		interval.setEndingDocument(new Document("Project", "filepath", "Production.java", "blah-document-add"));
		interval.close();
		sleepABit();

		ArrayList<WatchDogItem> intervals = new ArrayList<>();
		intervals.add(interval);

		assertEquals(
				"[{\"endingDocument\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"diff\":4,\"modCountDiff\":0,\"charLengthDiff\":4,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"it\":\"ty\",\"ts\":1,"
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	@Test
	public void typing_interval_diff_removal() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(new EditorWrapper(editor), new Date(1));
		interval.setDocument(new Document("Project", "filepath", "Production.java", "blah-document"));
		interval.setEndingDocument(new Document("Project", "filepath", "Production.java", "blah-doc"));
		interval.close();
		sleepABit();

		ArrayList<WatchDogItem> intervals = new ArrayList<>();
		intervals.add(interval);

		assertEquals(
				"[{\"endingDocument\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"diff\":5,\"modCountDiff\":0,\"charLengthDiff\":5,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"it\":\"ty\",\"ts\":1,"
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	@Test
	public void typing_interval_diff_modified() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(new EditorWrapper(editor), new Date(1));
		interval.setDocument(new Document("Project", "filepath", "Production.java", "blah-document"));
		interval.setEndingDocument(new Document("Project", "filepath", "Production.java", "blah-documens"));
		interval.close();
		sleepABit();

		ArrayList<WatchDogItem> intervals = new ArrayList<>();
		intervals.add(interval);

		assertEquals(
				"[{\"endingDocument\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"diff\":1,\"modCountDiff\":0,\"charLengthDiff\":0,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"e4afa075bb910c8ecb427e9950426a4599b21d7e\",\"sloc\":1,\"dt\":\"un\"},\"it\":\"ty\",\"ts\":1,"
						+ pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void session_interval() {
		IntervalBase interval = new IDEOpenInterval(new Date());
		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);

		assertEquals("[{\"it\":\"eo\",\"ts\":1,\"te\":2,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	@Test
	public void debug_interval() {
		IntervalBase interval = new DebugInterval(new Date());
		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);

		assertEquals("[{\"it\":\"db\",\"ts\":1,\"te\":2,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	/**
	 * Tests the format of the returned Json representation, manually setting an
	 * IDE host.
	 */
	@Test
	public void should_contain_ide_host() {
		IntervalBase interval = new IDEOpenInterval(new Date());
		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);

		assertEquals("[{\"it\":\"eo\",\"ts\":1,\"te\":2,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(intervals));
	}

	private ArrayList<WatchDogItem> createSampleIntervals(EditorIntervalBase interval) {
		interval.setDocument(new Document("Project", "Production.java", "filepath", "blah-document"));
		ArrayList<WatchDogItem> intervals = createSampleIntervals((IntervalBase) interval);
		return intervals;
	}

	private ArrayList<WatchDogItem> createSampleIntervals(IntervalBase interval) {
		ArrayList<WatchDogItem> intervals = new ArrayList<WatchDogItem>();
		interval.close();
		sleepABit();
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		interval.setSessionSeed("");
		intervals.add(interval);
		return intervals;
	}

	private void sleepABit() {
		Thread.yield();
		WatchDogUtils.sleep(200);
		Thread.yield();
	}

}
