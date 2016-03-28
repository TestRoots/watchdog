package nl.tudelft.watchdog.core.logic.storage;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogGlobals.IDE;

import com.google.gson.annotations.SerializedName;

/** Base class for all data transfers to the server. */
public abstract class WatchDogItem {

	@SerializedName("wdv")
	private final String watchDogClientVersion = WatchDogGlobals.CLIENT_VERSION;

	@SerializedName("ide")
	private final IDE watchDogIDE = WatchDogGlobals.hostIDE;

}
