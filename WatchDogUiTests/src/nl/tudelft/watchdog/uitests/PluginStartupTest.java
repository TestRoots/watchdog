package nl.tudelft.watchdog.uitests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for whether the plugin starts up and initializes the workbench with the
 * correct icons and menus.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PluginStartupTest {

	/** The {@link SWTWorkbenchBot} bot. */
	private static SWTWorkbenchBot bot;

	/** Dismisses the startup screen from eclipse and setups the bot. */
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
	}

	/** Tests whether the command menu contribution from WatchDog active. */
	@Test
	public void testCommandMenuContribution() {
		SWTBotMenu fileMenu = bot.menu("WatchDog");
		Assert.assertNotNull(fileMenu);
	}

}