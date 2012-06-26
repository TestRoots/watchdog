package interval;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import interval.activityCheckers.RunCallBack;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ActiveIntervalTest {

	private ITextEditor mockedITextEditor;
	private IDocumentProvider mockedDocProvider;
	private IDocument mockedDocument;
	
	private Boolean isActive;	
	
	
	@Before
	public void setUp(){
		isActive = true;
		setUpMocks();
	}
	
	private void setUpMocks() {
		mockedITextEditor = mock(ITextEditor.class);
		mockedDocProvider = mock(IDocumentProvider.class);
		mockedDocument = mock(IDocument.class);
		
		when(mockedITextEditor.getDocumentProvider()).thenReturn(mockedDocProvider);
		when(mockedDocProvider.getDocument(anyObject())).thenReturn(mockedDocument);				
	}
	
	@Test
	public void testInActivityAfter200ms() throws InterruptedException {
		when(mockedDocument.get()).thenReturn("read when initialized").thenReturn("read after 100ms");
		
		ActiveInterval interval = new ActiveInterval(mockedITextEditor);
		interval.start(100, new RunCallBack() {
			
			@Override
			public void onInactive() {
				isActive = false;
			}
		});		
		
		Thread.sleep(250);
		Assert.assertFalse(isActive);
	}

	
	
	@Test
	public void testActivityAfter200ms() throws InterruptedException {		
		when(mockedDocument.get()).thenReturn("read when initialized").thenReturn("read after 100ms").thenReturn("read after 200ms");
		
		ActiveInterval interval = new ActiveInterval(mockedITextEditor);
		interval.start(100, new RunCallBack() {
			
			@Override
			public void onInactive() {
				isActive = false;
			}
		});		
		
		Thread.sleep(250);
		Assert.assertTrue(isActive);
	}
	

}
