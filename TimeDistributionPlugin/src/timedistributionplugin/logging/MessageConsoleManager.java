package timeDistributionPlugin.logging;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class MessageConsoleManager {
	public static MessageConsoleStream getConsoleStream(){
		MessageConsole console = new MessageConsole("WatchDog", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
		MessageConsoleStream stream = console.newMessageStream();
		return stream;
	}
}
