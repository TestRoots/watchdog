package nl.tudelft.watchdog.uitests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PluginStartupTest {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// don't use SWTWorkbenchBot here which relies on Platform 3.x
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
	}

	@Test
	public void testCommandMenuContribution() {
		SWTBotMenu fileMenu = bot.menu("WatchDog");
		Assert.assertNotNull(fileMenu);
	}

	@AfterClass
	public static void sleep() {
		bot.sleep(2000);
	}

}