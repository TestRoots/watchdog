package nl.tudelft.watchdog.logic.interval.active;

import java.util.HashMap;
import java.util.Map;

import nl.tudelft.watchdog.util.GSONUtil;

/**
 * Construct intervals from textual representations.
 */
public final class IntervalFactory {

	static Map<IntervalType, Class<? extends IntervalBase>> intervalTypes = new HashMap<>();

	static {
		intervalTypes.put(IntervalType.Reading, ReadingInterval.class);
		intervalTypes.put(IntervalType.Session, SessionInterval.class);
		intervalTypes.put(IntervalType.Typing, TypingInterval.class);
	}

	/**
	 * Construct an IntervalBase subtype using a JSON string as input.
	 */
	public static <T extends IntervalBase> T fromJSON(String json) {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp = GSONUtil.gson().fromJson(json, tmp.getClass());

		Class<? extends IntervalBase> type = intervalTypes.get(IntervalType
				.fromMnemonic(tmp.get("at")));
		return (T) fromJSON(json, type);
	}

	/**
	 * Construct an IntervalBase subtype using a JSON string as input and
	 */
	public static <T extends IntervalBase> T fromJSON(String json, Class<T> type) {
		return GSONUtil.gson().fromJson(json, type);
	}
}
