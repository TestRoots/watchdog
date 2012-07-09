package nl.tudelft.watchdog.interval;

import java.util.Date;

import nl.tudelft.watchdog.document.IDocument;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;


public class RecordedInterval implements IInterval {

	private static final long serialVersionUID = 1L;
	private IDocument document;
	private Date start;
	private Date end;
	
	public RecordedInterval(IDocument document, Date start, Date end) {
		this.document = document;
		this.start = start;
		this.end = end;
	}

	@Override
	public IDocument getDocument() {
		return document;
	}
	
	@Override
	public Date getStart(){
		return start;
	}
	
	@Override
	public Date getEnd(){
		return end;
	}
	
	@Override 
	public Duration getDuration(){
		return new Duration(start.getTime(), end.getTime());
	}
	@Override 
	public String getDurationString(){
		Duration d = new Duration(start.getTime(), end.getTime());
		Period period = d.toPeriod();
		return PeriodFormat.getDefault().print(period);
	}

}
