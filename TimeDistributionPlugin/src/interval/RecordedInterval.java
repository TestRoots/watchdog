package interval;

import java.util.Date;

import document.IDocument;

public class RecordedInterval extends Interval {

	private IDocument document;
	private Date start;
	private Date end;
	
	public RecordedInterval(IDocument document, Date start, Date end) {
		this.document = document;
		this.start = start;
		this.end = end;
	}

	public IDocument getDocument() {
		return document;
	}
	
	public Date getStart(){
		return start;
	}
	
	public Date getEnd(){
		return end;
	}

}
