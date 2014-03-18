package nl.tudelft.watchdog.timingOutput;

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

import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.plugin.logging.WDLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writer for intervals to XML files.
 */
public class IntervalsToXMLWriter implements IIntervalWriter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.tudelft.watchdog.timingOutput.IIntervalWriter#intervalsToXML(java.
	 * util.List, java.io.OutputStream)
	 */
	@Override
	public void exportIntervals(List<IInterval> intervals, OutputStream stream) {
		try {

			DOMSource source = transformIntervalsToXML(intervals);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			StreamResult result = new StreamResult(stream);
			transformer.transform(source, result); // print to stream
			stream.close();

		} catch (TransformerConfigurationException e) {
			WDLogger.logSevere(e);
		} catch (ParserConfigurationException e) {
			WDLogger.logSevere(e);
		} catch (TransformerException e) {
			WDLogger.logSevere(e);
		} catch (IOException e) {
			WDLogger.logSevere(e);
		}
	}

	private DOMSource transformIntervalsToXML(List<IInterval> intervals)
			throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("intervals");
		doc.appendChild(rootElement);

		for (IInterval interval : intervals) {
			Element intervalElement = doc.createElement("interval");
			rootElement.appendChild(intervalElement);

			Element documentElement = doc.createElement("document");
			intervalElement.appendChild(documentElement);

			addElementWithValue(doc, documentElement, "projectName", interval
					.getDocument().getProjectName());
			addElementWithValue(doc, documentElement, "fileName", interval
					.getDocument().getFileName());
			addElementWithValue(doc, documentElement, "documentType", interval
					.getDocument().getDocumentType().toString());

			addElementWithValue(doc, intervalElement, "start",
					Long.toString(interval.getStart().getTime()));
			addElementWithValue(doc, intervalElement, "end",
					Long.toString(interval.getEnd().getTime()));
			addElementWithValue(doc, intervalElement, "duration",
					interval.getDurationString());
			addElementWithValue(doc, intervalElement, "activityType", interval
					.getActivityType().toString());
			addElementWithValue(doc, intervalElement, "debugMode",
					interval.isDebugMode() ? "1" : "0");
		}

		DOMSource source = new DOMSource(doc);

		return source;
	}

	private void addElementWithValue(Document doc, Element parent, String key,
			String value) {
		Element element = doc.createElement(key);
		element.appendChild(doc.createTextNode(value));
		parent.appendChild(element);
	}
}
