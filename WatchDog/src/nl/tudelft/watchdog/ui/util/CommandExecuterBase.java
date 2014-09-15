package nl.tudelft.watchdog.ui.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/** Base class for executing commands in the Eclipse UI. */
public abstract class CommandExecuterBase implements Runnable {
	/** The command service. */
	protected ICommandService commandService;

	/** Execute command. */
	abstract protected void doCommand() throws ExecutionException,
			NotDefinedException, NotEnabledException, NotHandledException;

	/** Execute this command. */
	public void execute() {
		Display display = Display.getDefault();
		if (display != null) {
			display.syncExec(this);
		}
	}

	@Override
	public void run() {
		commandService = getCommandService();

		if (commandService != null) {
			try {
				doCommand();
			} catch (ExecutionException | NotDefinedException
					| NotEnabledException | NotHandledException exception) {
			}
		}
	}

	private ICommandService getCommandService() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		return (ICommandService) window.getService(ICommandService.class);
	}

	/** Refreshes the given command. */
	public static class CommandRefresher extends CommandExecuterBase {

		private final String command;

		/** Constructor. */
		public CommandRefresher(String command) {
			this.command = command;
		}

		@Override
		protected void doCommand() throws ExecutionException,
				NotDefinedException, NotEnabledException, NotHandledException {
			commandService.refreshElements(command, null);
		}

	}

	/** Executes the given command. */
	public static class CommandExecuter extends CommandExecuterBase {
		private final String command;

		/** Constructor. */
		public CommandExecuter(String command) {
			this.command = command;
		}

		protected void doCommand() throws ExecutionException,
				NotDefinedException, NotEnabledException, NotHandledException {
			commandService.getCommand(command).executeWithChecks(
					new ExecutionEvent());
		}

	}

}