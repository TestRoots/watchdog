package nl.tudelft.watchdog.uitests;

import org.junit.Test;

/** A basic test opening and accessing the contributed preference page. */
public class PreferencesTest extends WatchDogTestBase {

	/** Opens Eclipse's preference menu and does some basic things in there. */
	@Test
	public void testBasicPreferences() {
		bot.menu("Window").menu("Preferences").click();
		bot.tree().getTreeItem("WatchDog").select();
		bot.checkBox("Enable Logs").click();
		bot.checkBox("Enable Logs").click();
		bot.button("Restore Defaults").click();
		bot.button("Cancel").click();
	}

}
