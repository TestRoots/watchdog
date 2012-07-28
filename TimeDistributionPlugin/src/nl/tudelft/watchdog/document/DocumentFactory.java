package nl.tudelft.watchdog.document;

import nl.tudelft.watchdog.util.TextEditorContentReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class DocumentFactory {
	public static Document createDocument(IWorkbenchPart part){	
		if(part instanceof ITextEditor){
			ITextEditor editor = (ITextEditor) part;
			
			if(part instanceof IEditorPart){
				IEditorPart  editorPart = (IEditorPart) part;
			    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
			    IFile file = input.getFile();
			    IProject activeProject = file.getProject();
			    String activeProjectName = activeProject.getName();
			    return new Document(activeProjectName, editor.getTitle(), DocumentClassifier.classifyDocument(editor.getTitle(), TextEditorContentReader.getEditorContent(editor)));
			}else{
				throw new IllegalArgumentException("Part not an IEditorPart");
			}
		}else{
			throw new IllegalArgumentException("Part not an ITextEditor");
		}
	}
}
