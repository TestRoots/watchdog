package timeDistributionPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
	private static Logger log;
	
	static{
		log = Logger.getLogger("TDPlogger");		
	}
	
	
	public static void logInfo(String message){
		log.log(Level.INFO, message);
	}
	
	public static void log(Level level, String message){
		log.log(level, message);
	}
	
	public static void log(Level level, String message, Throwable throwable){
		log.log(level, message, throwable);
	}
}
