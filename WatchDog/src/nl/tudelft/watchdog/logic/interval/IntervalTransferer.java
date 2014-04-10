package nl.tudelft.watchdog.logic.interval;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/** Transmits the currently recorded intervals to the WatchDog server. */
public class IntervalTransferer {

	/** The {@link GsonBuilder} for building the intervals. */
	private GsonBuilder gsonBuilder = new GsonBuilder();

	/** The Gson object for object serialization to Json. */
	private Gson gson;

	/** Constructor. */
	public IntervalTransferer() {
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gson = gsonBuilder.create();
	}

	/** Sends the recorded intervals to the server. */
	public void sendIntervals() {
		List<IntervalBase> recordedIntervals = IntervalManager.getInstance()
				.getClosedIntervals();
		String userid = WatchdogPreferences.getInstance().getUserid();
		String json = toJson(recordedIntervals);
		NetworkUtils.transferJson(NetworkUtils.buildIntervalsPostURL(userid),
				json);
	}

	/** Converts the intervals to Json. */
	public String toJson(List<IntervalBase> recordedIntervals) {
		return gson.toJson(recordedIntervals);
	}

	/** A JSon Serializer for Date. */
	private class DateSerializer implements JsonSerializer<Date> {

		@Override
		public JsonElement serialize(Date date, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
	}
}
