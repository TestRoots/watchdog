package timeDistributionPlugin.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import timeDistributionPlugin.logging.MessageConsoleManager;

public class HelloHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		MessageConsoleManager.getConsoleStream().println("Wroof!");
		return null;
	}

	
}   