package nl.tudelft.watchdog.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A class that initializes the GSON factory with our custom types and returns
 * instances of GSON objects.
 */
public final class GSONUtil {

	/** The {@link GsonBuilder} for building the intervals. */
	private static GsonBuilder gsonBuilder;

	static {
		gsonBuilder = new GsonBuilder().registerTypeAdapter(Date.class,
				new DateSerializer()).registerTypeAdapter(Date.class,
				new DateDeserializer());
	}

	/** Get an instance of a GSON parser. */
	public static Gson gson() {
		return gsonBuilder.create();
	}

	/** A JSon serializer for Date. */
	private static class DateSerializer implements JsonSerializer<Date> {

		@Override
		public JsonElement serialize(Date date, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
	}

	/** A JSon de-serializer for Date. */
	private static class DateDeserializer implements JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			return (json == null) ? null : new Date(json.getAsLong());
		}
	};
}
