package nl.tudelft.watchdog.plugin.logging;


import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;



public class WDLogger {
	private static Logger log;
	
	static{
		log = Logger.getLogger(WDLogger.class.getName());
	}	
	
	public static void addHandler(Handler handler, Level l){			
		log.addHandler(handler);
		handler.setLevel(l);
	}
	
	public static void logInfo(String message){
		log.log(Level.INFO, message);
	}

	
	public static void logSevere(String message){
		log.log(Level.SEVERE, message);
	}
	
	public static void logSevere(Throwable e){		
		log.log(Level.SEVERE, e.getMessage(), e);
	}
	
	public static void log(Level level, String message){
		log.log(level, message);
	}
	
	public static void log(Level level, String message, Throwable throwable){
		log.log(level, message, throwable);
	}
	
	public static void closeAllHandlers(){
		for(Handler h : log.getHandlers()){			
			h.close();
		}
	}
}
