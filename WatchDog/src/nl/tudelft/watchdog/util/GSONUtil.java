package nl.tudelft.watchdog.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A class that initializes the GSON factory with our custom types and returns
 * instances of GSON objects.
 */
public class GSONUtil {

	/** The {@link GsonBuilder} for building the intervals. */
	private static GsonBuilder gsonBuilder;

	static {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
	}

	/**
	 * Get an instance of a GSON parser.
	 * 
	 * @return An initialized GSON object.
	 */
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
}
