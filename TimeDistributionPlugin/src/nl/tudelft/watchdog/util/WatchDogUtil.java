package nl.tudelft.watchdog.util;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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

	public static String getEditorContent(ITextEditor editor) throws IllegalArgumentException{
		if(editor == null)
			throw new IllegalArgumentException("editor is null");
		if(editor.getDocumentProvider() == null)
			throw new IllegalArgumentException("doc provider is null");
		IDocumentProvider dp = editor.getDocumentProvider();
		if(dp.getDocument(editor.getEditorInput()) == null)
			throw new IllegalArgumentException("doc is null");
	    IDocument doc = dp.getDocument(editor.getEditorInput());
	    
	    return doc.get();
	}
}
