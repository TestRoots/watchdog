package timeDistributionPlugin.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
	private static Logger log;
	
	static{
		log = Logger.getLogger("TDPlogger");
		log.setUseParentHandlers(false);
	}	
	
	public static void addHandler(Handler handler, Level l){			
		log.addHandler(handler);
		handler.setLevel(l);
	}
	
	public static void logInfo(String message){
		log.log(Level.INFO, message);
		flushAllHandlers();
	}

	
	public static void logSevere(String message){
		log.log(Level.SEVERE, message);
		flushAllHandlers();
	}
	
	public static void log(Level level, String message){
		log.log(level, message);
		flushAllHandlers();
	}
	
	public static void log(Level level, String message, Throwable throwable){
		log.log(level, message, throwable);
		flushAllHandlers();
	}
	
	public static void closeAllHandlers(){
		for(Handler h : log.getHandlers()){			
			h.flush();
			h.close();
		}
	}
	
	private static void flushAllHandlers() {
		for(Handler h : log.getHandlers()){
			h.flush();
		}
	}
}
