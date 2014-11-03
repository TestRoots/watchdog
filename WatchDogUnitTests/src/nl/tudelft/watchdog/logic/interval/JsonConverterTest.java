package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.ui.wizards.Project;
import nl.tudelft.watchdog.ui.wizards.User;
import nl.tudelft.watchdog.util.WatchDogGlobals;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

/**
 * Test the transfer from {@link IInterval}s to JSon.
 */
public class JsonConverterTest {

	private JsonTransferer transferer = new JsonTransferer();

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonReadingIntervalRepresentation() {
		ReadingInterval interval = new ReadingInterval(null, new Date());
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"re\",\"ts\":1,\"te\":2,\"ss\":0,\"wdv\":\""
						+ WatchDogGlobals.CLIENT_VERSION + "\"}]",
				transferer.toJson(intervals));
	}

	/**
	 * Tests the format of the returned Json representation, if one of the
	 * typing intervals does not have its ending document properly set.
	 */
	@Test
	public void testJsonTypingIntervalMissingDocumentRepresentation() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(editor, new Date());
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"ty\",\"ts\":1,\"te\":2,\"ss\":0,\"wdv\":\""
						+ WatchDogGlobals.CLIENT_VERSION + "\"}]",
				transferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonTypingIntervalTwoSameIntervalsRepresentation() {
		ITextEditor editor = Mockito.mock(ITextEditor.class);
		TypingInterval interval = new TypingInterval(editor, new Date());
		interval.setDocument(new Document("Project", "filepath", "Production.java",
				"blah-document"));
		interval.setEndingDocument(new Document("Project", "Production.java", "filepath",
				"blah-document"));

		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"endingDocument\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"diff\":0,\"doc\":{\"pn\":\"f6f4da8d93e88a08220e03b7810451d3ba540a34\",\"fn\":\"90a8834de76326869f3e703cd61513081ad73d3c\",\"sloc\":1,\"dt\":\"pr\"},\"it\":\"ty\",\"ts\":1,\"te\":2,\"ss\":0,\"wdv\":\""
						+ WatchDogGlobals.CLIENT_VERSION + "\"}]",
				transferer.toJson(intervals));
	}

	/** Tests the format of the returned Json representation. */
	@Test
	public void testJsonSessionIntervalRepresentation() {
		IntervalBase interval = new IntervalBase(IntervalType.ECLIPSE_OPEN, new Date());
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);

		assertEquals(
				"[{\"it\":\"eo\",\"ts\":1,\"te\":2,\"ss\":0,\"wdv\":\""
						+ WatchDogGlobals.CLIENT_VERSION + "\"}]",
				transferer.toJson(intervals));
	}

	private ArrayList<IntervalBase> createSampleIntervals(
			EditorIntervalBase interval) {
		interval.setDocument(new Document("Project", "Production.java", "filepath",
				"blah-document"));
		ArrayList<IntervalBase> intervals = createSampleIntervals((IntervalBase) interval);
		return intervals;
	}

	private ArrayList<IntervalBase> createSampleIntervals(IntervalBase interval) {
		ArrayList<IntervalBase> intervals = new ArrayList<IntervalBase>();
		interval.close();
		sleepABit();
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		intervals.add(interval);
		return intervals;
	}

	private void sleepABit() {
		Thread.yield();
		WatchDogUtils.sleep(200);
		Thread.yield();
	}

	@Test
	public void testUserHasWatchDogVersion() {
		String gsonRepresentation = new Gson().toJson(new User());
		boolean containsWDVersion = gsonRepresentation.contains("\"wdv\":\""
				+ WatchDogGlobals.CLIENT_VERSION + "\"}");
		assertTrue(containsWDVersion);
	}

	@Test
	public void testProjectHasWatchDogVersion() {
		String gsonRepresentation = new Gson().toJson(new Project(""));
		boolean containsWDVersion = gsonRepresentation.contains("\"wdv\":\""
				+ WatchDogGlobals.CLIENT_VERSION + "\"}");
		assertTrue(containsWDVersion);
	}

}
