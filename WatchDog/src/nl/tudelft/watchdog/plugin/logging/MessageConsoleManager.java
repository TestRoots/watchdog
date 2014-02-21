package nl.tudelft.watchdog.plugin.logging;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class MessageConsoleManager {
	private static MessageConsoleStream stream;

	private static void createConsoleStream() {
		MessageConsole console = new MessageConsole("WatchDog", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { console });
		stream = console.newMessageStream();
	}

	public static MessageConsoleStream getConsoleStream() {
		if (stream == null) {
			createConsoleStream();
		}
		return stream;
	}
}
