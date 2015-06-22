package nl.tudelft.watchdog.logic.network;

import nl.tudelft.watchdog.util.WatchDogGlobals;

import com.google.gson.annotations.SerializedName;

/** Base class for all data transfers to the server. */
public abstract class WatchDogTransferable {

	@SerializedName("wdv")
	private final String watchDogClientVersion = WatchDogGlobals.CLIENT_VERSION;


    @SerializedName("ide")
    private final WatchDogGlobals.IDE watchDogIDE = WatchDogGlobals.hostIDE;
}
