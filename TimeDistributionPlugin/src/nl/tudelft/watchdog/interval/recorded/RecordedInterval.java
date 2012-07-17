package nl.tudelft.watchdog.interval.recorded;

import java.util.Date;

import nl.tudelft.watchdog.document.IDocument;
import nl.tudelft.watchdog.interval.ActivityType;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;


public class RecordedInterval implements IInterval {

	private static final long serialVersionUID = 2L;
	private IDocument document;
	private Date start;
	private Date end;
	private ActivityType activityType;
	
	public RecordedInterval(IDocument document, Date start, Date end, ActivityType activityType) {
		this.document = document;
		this.start = start;
		this.end = end;
		this.activityType = activityType;
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

	@Override
	public ActivityType getActivityType() {
		return activityType;
	}

}