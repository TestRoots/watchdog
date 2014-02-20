package nl.tudelft.watchdog.test.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nl.tudelft.watchdog.interval.active.ActiveEditingInterval;
import nl.tudelft.watchdog.interval.activityCheckers.OnInactiveCallBack;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Before;
import org.junit.Test;

public class ActiveEditingIntervalTest {

	private ITextEditor mockedITextEditor;
	private IDocumentProvider mockedDocProvider;
	private IDocument mockedDocument;

	private Boolean isActive;

	public ActiveEditingIntervalTest() {
		setUpMocks();
	}

	@Before
	public void setUp() {
		isActive = true;
		setUpMocks();
	}

	private void setUpMocks() {
		mockedITextEditor = mock(ITextEditor.class);
		mockedDocProvider = mock(IDocumentProvider.class);
		mockedDocument = mock(IDocument.class);

		when(mockedITextEditor.getDocumentProvider()).thenReturn(
				mockedDocProvider);
		when(mockedDocProvider.getDocument(anyObject())).thenReturn(
				mockedDocument);
	}

	@Test
	/** Tests for an active editing interval of 100ms, whether it really is inactive after 250ms. */
	public void testInActivityAfter250ms() throws InterruptedException {
		when(mockedDocument.get()).thenReturn("read when initialized")
				.thenReturn("read after 100ms");

		ActiveEditingInterval interval = new ActiveEditingInterval(
				mockedITextEditor);
		interval.addTimeoutListener(100, new OnInactiveCallBack() {

			@Override
			public void onInactive() {
				isActive = false;
			}
		});

		Thread.sleep(250);
		assertFalse(isActive);
	}

	@Test
	/** Tests for an active editing interval of 100ms, whether it really is still active after 80ms, and inactive after 250ms. */
	public void testActivityAfter100ms() throws InterruptedException {
		when(mockedDocument.get()).thenReturn("read when initialized")
				.thenReturn("read after 100ms").thenReturn("read after 200ms");

		ActiveEditingInterval interval = new ActiveEditingInterval(
				mockedITextEditor);
		interval.addTimeoutListener(100, new OnInactiveCallBack() {

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
