package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nl.tudelft.watchdog.logic.interval.active.TypingInterval;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Before;
import org.junit.Test;

/**
 * Mock tests for the editing interval.
 */
public class ActiveEditingIntervalTest {

	/**
	 * The mocked text editor.
	 */
	private ITextEditor mockedITextEditor;
	/**
	 * The mocked document Provider.
	 */
	private IDocumentProvider mockedDocProvider;
	/**
	 * The mocked document.
	 */
	private IDocument mockedDocument;

	/**
	 * Flag that simulates whether the editor is active.
	 */
	private Boolean isActive;

	/**
	 * Setup method run before every testcase execution.
	 */
	@Before
	public void setUp() {
		isActive = true;
		setUpMocks();
	}

	/**
	 * Sets up the mocks.
	 */
	private void setUpMocks() {
		mockedITextEditor = mock(ITextEditor.class);
		mockedDocProvider = mock(IDocumentProvider.class);
		mockedDocument = mock(IDocument.class);

		when(mockedITextEditor.getDocumentProvider()).thenReturn(
				mockedDocProvider);
		when(mockedDocProvider.getDocument(anyObject())).thenReturn(
				mockedDocument);
	}

	/**
	 * Tests for an active editing interval of 100ms, whether it really is
	 * inactive after 250ms.
	 */
	@Test
	public void testInActivityAfter250ms() throws InterruptedException {
		when(mockedDocument.get()).thenReturn("read when initialized")
				.thenReturn("read after 100ms");

		TypingInterval interval = new TypingInterval(mockedITextEditor, "123",
				0);
		interval.addTimeoutListener(100, new OnInactiveCallback() {

			@Override
			public void onInactive() {
				isActive = false;
			}
		});

		Thread.sleep(250);
		assertFalse(isActive);
	}

	/**
	 * Tests for an active editing interval of 100ms, whether it really is still
	 * active after 80ms, and inactive after 250ms.
	 */
	@Test
	public void testActivityAfter100ms() throws InterruptedException {
		when(mockedDocument.get()).thenReturn("read when initialized")
				.thenReturn("read after 100ms").thenReturn("read after 200ms");

		TypingInterval interval = new TypingInterval(mockedITextEditor, "123",
				0);
		interval.addTimeoutListener(100, new OnInactiveCallback() {

			@Override
			public void onInactive() {
				isActive = false;
			}
		});

		Thread.sleep(80);
		assertTrue(isActive);
		Thread.sleep(200);
		assertFalse(isActive);
	}

}
