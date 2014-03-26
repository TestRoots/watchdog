package nl.tudelft.watchdog.logic.interval;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writer for intervals to XML files.
 */
public class IntervalsToXMLWriter {

	/**
	 * Exports the given intervals to the supplied {@link OutputStream}.
	 */
	public void exportIntervals(List<RecordedInterval> intervals, OutputStream stream) {
		try {

			DOMSource source = transformIntervalsToXML(intervals);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			StreamResult result = new StreamResult(stream);
			transformer.transform(source, result); // print to stream
			stream.close();

		} catch (TransformerConfigurationException e) {
			WatchDogLogger.logSevere(e);
		} catch (ParserConfigurationException e) {
			WatchDogLogger.logSevere(e);
		} catch (TransformerException e) {
			WatchDogLogger.logSevere(e);
		} catch (IOException e) {
			WatchDogLogger.logSevere(e);
		}
	}

	/**
	 * Transforms the given interval list to XML.
	 * 
	 * @return {@link DOMSource} of the XML.
	 */
	private DOMSource transformIntervalsToXML(List<RecordedInterval> intervals)
			throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("intervals");
		doc.appendChild(rootElement);

		for (RecordedInterval interval : intervals) {
			Element intervalElement = doc.createElement("interval");
			rootElement.appendChild(intervalElement);

			Element documentElement = doc.createElement("document");
			intervalElement.appendChild(documentElement);

			createAndAddElementWithValue(doc, documentElement, "projectName",
					interval.getDocument().getProjectName());
			createAndAddElementWithValue(doc, documentElement, "fileName",
					interval.getDocument().getFileName());
			createAndAddElementWithValue(doc, documentElement, "documentType",
					interval.getDocument().getDocumentType().toString());

			createAndAddElementWithValue(doc, intervalElement, "start",
					Long.toString(interval.getStart().getTime()));
			createAndAddElementWithValue(doc, intervalElement, "end",
					Long.toString(interval.getEnd().getTime()));
			createAndAddElementWithValue(doc, intervalElement, "duration",
					interval.getDurationString());
			createAndAddElementWithValue(doc, intervalElement, "activityType",
					interval.getActivityType().toString());
			createAndAddElementWithValue(doc, intervalElement, "debugMode",
					interval.isDebugMode() ? "1" : "0");
		}

		DOMSource source = new DOMSource(doc);

		return source;
	}

	/**
	 * Creates an {@link Element} from the given key-vlaue pair and appends it
	 * to the parent in the {@link Document} doc.
	 */
	private void createAndAddElementWithValue(Document doc, Element parent,
			String key, String value) {
		Element element = doc.createElement(key);
		element.appendChild(doc.createTextNode(value));
		parent.appendChild(element);
	}
}
