package timingOutput;

import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBException;

import document.Document;
import document.DocumentType;

public class IntervalsToXMLWriter {
	public void intervalToXML() throws JAXBException, FileNotFoundException{
		
	        FileOutputStream os = new FileOutputStream("test.xml");
	        XMLEncoder encoder = new XMLEncoder(os);
	        Document d = new Document();
	        d.setFileName("tralala.x");
	        d.setDocType(DocumentType.TEST);
	        encoder.writeObject(d);
	        encoder.close();
	}
}
