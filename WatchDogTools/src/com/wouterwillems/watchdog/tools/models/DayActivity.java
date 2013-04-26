package com.wouterwillems.watchdog.tools.models;

import java.util.Date;

public class DayActivity {

	private ActivityType type;
	private Date date;
	private Long durationMilliseconds;
	
	public ActivityType getType() {
		return type;
	}
	public void setType(ActivityType type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getDurationMilliseconds() {
		return durationMilliseconds;
	}
	public void setDurationMilliseconds(Long durationMilliseconds) {
		this.durationMilliseconds = durationMilliseconds;
	}
	
	
}
