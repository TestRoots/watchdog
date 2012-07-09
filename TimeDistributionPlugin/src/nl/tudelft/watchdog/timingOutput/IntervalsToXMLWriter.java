package nl.tudelft.watchdog.timingOutput;


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

import nl.tudelft.watchdog.interval.IInterval;
import nl.tudelft.watchdog.timeDistributionPlugin.logging.MyLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class IntervalsToXMLWriter implements IIntervalWriter {
	
	/* (non-Javadoc)
	 * @see nl.tudelft.watchdog.timingOutput.IIntervalWriter#intervalsToXML(java.util.List, java.io.OutputStream)
	 */
	@Override
	public void exportIntervals(List<IInterval> intervals, OutputStream stream) {		
		try {
			
			DOMSource source = transformIntervalsToXML(intervals);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			StreamResult result = new StreamResult(stream);
			transformer.transform(source, result); //print to stream
			
		} catch (TransformerConfigurationException e) {
			MyLogger.logSevere(e.getMessage());
		} catch (ParserConfigurationException e) {
			MyLogger.logSevere(e.getMessage());
		} catch (TransformerException e) {
			MyLogger.logSevere(e.getMessage());
		}
	}
	
	private DOMSource transformIntervalsToXML(List<IInterval> intervals) throws ParserConfigurationException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;	
		docBuilder = docFactory.newDocumentBuilder();	

		// root elements
		Document doc = docBuilder.newDocument();		
		Element rootElement = doc.createElement("Intervals");
		doc.appendChild(rootElement);
 
		for(IInterval interval : intervals){
			// interval elements
			Element intervalElement = doc.createElement("Interval");
			rootElement.appendChild(intervalElement); 
			
			// Document element
			Element documentElement = doc.createElement("Document");
			intervalElement.appendChild(documentElement);
			
				//filename element
				Element fileNameElement = doc.createElement("fileName");
				fileNameElement.appendChild(doc.createTextNode(interval.getDocument().getFileName()));
				documentElement.appendChild(fileNameElement);
				
				//documenttype element
				Element documentTypeElement = doc.createElement("documentType");
				documentTypeElement.appendChild(doc.createTextNode(interval.getDocument().getDocumentType().toString()));
				documentElement.appendChild(documentTypeElement);
				
			// Start element
			Element startElement = doc.createElement("Start");
			startElement.appendChild(doc.createTextNode(Long.toString(interval.getStart().getTime())));
			intervalElement.appendChild(startElement);	
			
			// end element
			Element endElement = doc.createElement("End");
			endElement.appendChild(doc.createTextNode(Long.toString(interval.getEnd().getTime())));
			intervalElement.appendChild(endElement);
			
			// end element
			Element durationElement = doc.createElement("duration");
			durationElement.appendChild(doc.createTextNode(interval.getDurationString()));
			intervalElement.appendChild(durationElement);
		}		
		
		DOMSource source = new DOMSource(doc);
		
		return source;		
	}
}
