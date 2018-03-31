package nl.tudelft.watchdog.eclipse.logic.ui.listeners.staticanalysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.joda.time.DateTime;
import nl.tudelft.watchdog.core.logic.document.Document;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.staticanalysis.FileWarningSnapshotEvent;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.CoreMarkupModelListener;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.StaticAnalysisMessageClassifier;
import nl.tudelft.watchdog.core.logic.ui.listeners.staticanalysis.Warning;
import nl.tudelft.watchdog.eclipse.logic.document.DocumentCreator;

public class ResourceAndResourceDeltaVisitor implements IResourceDeltaVisitor, IResourceVisitor {
	
	private static final Map<IPath, List<MarkerHolder>> currentFileMarkers = new HashMap<>();
	private final TrackingEventManager trackingEventManager;
	private final boolean shouldCreateSnapshot;

    public ResourceAndResourceDeltaVisitor(TrackingEventManager trackingEventManager,
    		boolean shouldCreateSnapshot) {
		this.trackingEventManager = trackingEventManager;
		this.shouldCreateSnapshot = shouldCreateSnapshot;
    }

    /**
     * Can be new resources, deleted or simply changed. In any case, even if we do not have
     * previous state in {@link EclipseMarkupModelListener#oldFileMarkers} we need to compute
     * a diff.
     */
    @Override
    public boolean visit(IResourceDelta delta) throws CoreException {
        return this.visit(delta.getResource(), true);
    }

    /**
     * Invoked on initial project open to setup our initial state for {@link EclipseMarkupModelListener#oldFileMarkers}.
     */
    @Override
    public boolean visit(IResource resource) throws CoreException {
        return this.visit(resource, false);
    }

    /**
     * The Eclipse API trashes and recreates all markers after a build.
     * This means that we have to handle two lists of markers and compute
     * the difference between them. Delegate this implementation to
     * {@link MarkerBackTrackingAlgorithm} which is an abstraction around
     * this algorithm.
     */
    private boolean visit(IResource resource, boolean shouldComputeDiff) throws CoreException {
        if (!resource.exists()) {
            return false;
        }

        if (resource instanceof IFile) {
            IFile file = (IFile) resource;
            IPath filePath = file.getFullPath();
            List<MarkerHolder> oldMarkers = currentFileMarkers.get(filePath);
            List<MarkerHolder> currentMarkers =
                    Arrays.stream(file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO))
                          .map(MarkerHolder::fromIMarker)
                          .sorted()
                          .collect(Collectors.toList());

            if (shouldComputeDiff && oldMarkers == null) {
                oldMarkers = Collections.emptyList();
            }
            
            Document document = DocumentCreator.createDocument(file.getName(), file).prepareDocument();
            
            if (this.shouldCreateSnapshot) {
            	createWarningSnapshotForMarkers(currentMarkers, document);
            }

            if (oldMarkers != null) {
                MarkerBackTrackingAlgorithm diffingAlgorithm = new MarkerBackTrackingAlgorithm(oldMarkers, currentMarkers);
                
                diffingAlgorithm.computeMemoizationTable()
                				.traverseMemoizationTable();
                
                CoreMarkupModelListener.addCreatedWarnings(this.trackingEventManager, diffingAlgorithm.createdWarningTypes.stream().map(this::createWarning), document);
                CoreMarkupModelListener.addRemovedWarnings(this.trackingEventManager, diffingAlgorithm.removedWarningTypes.stream().map(this::createWarning), document);
            }

            currentFileMarkers.put(filePath, currentMarkers);
        }

        return true;
    }
    
    private void createWarningSnapshotForMarkers(List<MarkerHolder> currentMarkers, Document document) {
        List<Warning<String>> warnings = currentMarkers.stream()
                .map(holder -> new Warning<>(
                        StaticAnalysisMessageClassifier.classify(holder.message),
                        holder.lineNumber,
                        DateTime.now().toDate()
                    ))
                .collect(Collectors.toList());

        this.trackingEventManager.addEvent(new FileWarningSnapshotEvent(document, warnings));
    }
    
    private Warning<String> createWarning(Warning<String> warning) {
        return new Warning<>(
                StaticAnalysisMessageClassifier.classify(warning.type),
                warning.lineNumber,
                warning.warningCreationTime,
                warning.secondsBetween
        );
    }
}