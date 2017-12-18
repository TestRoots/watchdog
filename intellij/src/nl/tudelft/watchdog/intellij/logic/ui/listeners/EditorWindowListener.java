package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionEngine;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ex.InspectionToolRegistrar;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditorWindowListener implements EditorFactoryListener {
    private final Logger logger = Logger.getInstance(EditorWindowListener.class);
    private final Project project;
    private WatchDogEventManager eventManager;

    private EditorFocusListener focusListener;

    private Map<Editor, EditorListener> editorListenerMap = new HashMap<Editor, EditorListener>();

    private String myProjectName;

    public EditorWindowListener (WatchDogEventManager eventManager, Project project) {
        this.eventManager = eventManager;
        myProjectName = project.getName();
        this.project = project;
    }

    @Override
    public void editorCreated(EditorFactoryEvent editorFactoryEvent) {
        if(!editorBelongsToThisProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        focusListener = new EditorFocusListener(eventManager, editor);
        editor.getContentComponent().addFocusListener(focusListener);
        editorListenerMap.put(editor, new EditorListener(eventManager, editor));

        DumbServiceImpl.getInstance(project).runReadActionInSmartMode(() -> {
            PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument()).getOriginalFile();
            final GlobalInspectionContext context = InspectionManager.getInstance(project).createNewGlobalContext(false);
            final List<ProblemDescriptor> problems = InspectionToolRegistrar.getInstance().get().stream()
                    .flatMap(wrapper -> InspectionEngine.runInspectionOnFile(file, wrapper, context).stream())
                    .collect(Collectors.toList());
            logger.warn(problems.toString());
        });
    }

    @Override
    public void editorReleased(EditorFactoryEvent editorFactoryEvent) {
        if(!editorBelongsToThisProject(editorFactoryEvent)) {
            return;
        }

        Editor editor = editorFactoryEvent.getEditor();
        editor.getContentComponent().removeFocusListener(focusListener);
        editorListenerMap.remove(editor).removeListeners();
    }

    private boolean editorBelongsToThisProject(EditorFactoryEvent editorFactoryEvent) {
        try {
            return editorFactoryEvent.getEditor().getProject().getName().equals(myProjectName);
        }
        catch(NullPointerException e) {
            return false;
        }
    }
}
