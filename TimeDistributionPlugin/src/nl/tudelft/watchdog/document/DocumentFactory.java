package nl.tudelft.watchdog.document;

import nl.tudelft.watchdog.util.WatchDogUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class DocumentFactory implements IDocumentFactory {
	@Override
	public Document createDocument(IWorkbenchPart part){	
		if(part instanceof ITextEditor){
			ITextEditor editor = (ITextEditor) part;
			
			if(part instanceof IEditorPart){
				IEditorPart  editorPart = (IEditorPart) part;
				String activeProjectName;
				if(editorPart.getEditorInput() instanceof IFileEditorInput){
					IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
				    IFile file = input.getFile();
				    IProject activeProject = file.getProject();
				    activeProjectName = activeProject.getName();
			    }else{
			    	activeProjectName = "";
			    }
				
				
				return new Document(activeProjectName, editor.getTitle(), DocumentClassifier.classifyDocument(editor.getTitle(), WatchDogUtil.getEditorContent(editor)));
		    }else{
				throw new IllegalArgumentException("Part not an IEditorPart");
			}
		}else{
			throw new IllegalArgumentException("Part not an ITextEditor");
		}
	}
}
