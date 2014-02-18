package nl.tudelft.watchdog.document;

import org.eclipse.ui.IWorkbenchPart;

public interface IDocumentFactory {

	Document createDocument(IWorkbenchPart part);

}