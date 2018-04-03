package nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CheckStyleChecksMessagesFetcher;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CoreMarkupModelListener;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

@SuppressWarnings("restriction")
public class EclipseMarkupModelListener extends CoreMarkupModelListener implements IResourceChangeListener {

    static final String CHECKSTYLE_MARKER_ID = "net.sf.eclipsecs.core.CheckstyleMarker";
    
    static {
        HashtableOfInt hashTable = DefaultProblemFactory.loadMessageTemplates(Locale.getDefault());

        for (int key : hashTable.keyTable) {
            Object value = hashTable.get(key);
            if (value != null && value instanceof String) {
                StaticAnalysisMessageClassifier.IDE_BUNDLE.addMessage(String.valueOf(key), (String) value);
            }
        }

        StaticAnalysisMessageClassifier.IDE_BUNDLE.sortList();

        try {
            CheckStyleChecksMessagesFetcher.addCheckStyleMessagesToBundle(EclipseMarkupModelListener.class.getClassLoader());

            StaticAnalysisMessageClassifier.CHECKSTYLE_BUNDLE.sortList();
        } catch (Exception ignored) {
            // CheckStyle apparently was not loaded, bail out
        }
    }

	private TrackingEventManager trackingEventManager;
	private final Map<IPath, List<MarkerHolder>> currentFileMarkers;

    public EclipseMarkupModelListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
        currentFileMarkers = new HashMap<>();
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(this.createVisitor(false));
        } catch (CoreException e) {
            WatchDogLogger.getInstance().logSevere(e.getMessage());
        }
    }

    public ResourceAndResourceDeltaVisitor createVisitor(boolean shouldCreateSnapshot) {
        return new ResourceAndResourceDeltaVisitor(this.trackingEventManager, currentFileMarkers, shouldCreateSnapshot);
    }
}
