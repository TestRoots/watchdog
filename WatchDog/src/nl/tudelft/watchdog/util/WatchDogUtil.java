package nl.tudelft.watchdog.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import nl.tudelft.watchdog.exceptions.ContentReaderException;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class WatchDogUtil {
	public static boolean isInDebugMode(){
		boolean isDebugMode = false;
		for(IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()){	
			isDebugMode = window.getActivePage().getPerspective().getId().equals("org.eclipse.debug.ui.DebugPerspective");				
			if(isDebugMode)
				return isDebugMode;
		}
		return isDebugMode;
	}

	/**
	 * 
	 * @param editor
	 * what you want the contents of
	 * @return
	 * the content of the editor
	 * @throws ContentReaderException
	 * Can throw this exception when a file is moved. When moving a file within the workspace, the document provider pointer is set to null to make room for a new document provider later in the moving phase
	 * @throws IllegalArgumentException
	 * Unexpected eclipse API behavior when Editor is null or the document in the document provider is null
	 */
	public static String getEditorContent(final ITextEditor editor) throws ContentReaderException, IllegalArgumentException{
		if(editor == null)
			throw new IllegalArgumentException("editor is null");
		if(editor.getDocumentProvider() == null)
			throw new ContentReaderException("doc provider is null");
		IDocumentProvider dp = editor.getDocumentProvider();
		if(dp.getDocument(editor.getEditorInput()) == null)
			throw new IllegalArgumentException("doc is null");
	    IDocument doc = dp.getDocument(editor.getEditorInput());
	    
	    return doc.get();
	}
	
	public static String getFileContentsFromEditor(ITextEditor editor){
		if(editor.getEditorInput() instanceof FileEditorInput){
			IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
			
	    	BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(editorInput.getFile().getContents()));
		    	StringBuilder sb = new StringBuilder();	 
		    	String line;
		    	while ((line = br.readLine()) != null) {
		    		sb.append(line);
		    	}
		    	br.close();
		    	String res = sb.toString();
		    	System.out.println(res);
		    	return res;
		 
			}catch(Exception e){
				throw new IllegalArgumentException("can't read resource file");
			}
		}else{
			throw new IllegalArgumentException("can't read resource file");
		}
	}
}
