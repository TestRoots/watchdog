package nl.tudelft.watchdog.uitests;

import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for whether the plugin shows the correct usage data in the info dialog.
 */
public class InfoDialogTest extends WatchDogTestBase {

	/** Tests whether the statistics upon startup of the session are zeroed. */
	@Test
	public void testStatisticsBeginWithZero() {
		SWTBotToolbarButton button = bot
				.toolbarButtonWithTooltip("WatchDog is active and recording ...");
		button.click();
		assertExists("WatchDog is active and recording ...", 0);
		assertExists("Current Eclipse Session:", 0);
		assertExists("0 seconds", 0);
		assertExists("0 seconds", 1);
		assertExists("0 seconds", 2);
		bot.button("OK").click();
	}

	/** Asserts that the given label with the given index exists. */
	private void assertExists(String label, int index) {
		SWTBotLabel text = bot.label(label, index);
		Assert.assertNotNull(text);
	}

	/**
	 * Tests the time needed for the creation of one simple Java production
	 * class.
	 */
	@Test
	public void testSimpleProductionClass() {
		createSampleProject();
		SWTBotTreeItem item = bot.tree().getTreeItem("AJavaTestProject")
				.select();
		item.contextMenu("New").menu("Class").click();
		bot.textWithLabel("Na&me:").setText("AJavaProductionClass");
		bot.button("Finish").click();
		bot.sleep(1000);

		SWTBotEclipseEditor textEditor = bot.activeEditor().toTextEditor();
		textEditor.setFocus();
		textEditor.typeText(" ");
		textEditor.setText("");
		textEditor
				.setText("public class AJavaProductionClass {	\n			// This class is a production class		\n			private void testmethod() {				\n		// TODO Auto-generated method stub			\n		}				}");
		bot.sleep(100);
		textEditor.pressShortcut(SWT.CTRL | SWT.SHIFT, 'f');
		bot.sleep(4000);
		textEditor.typeText("  ");
		bot.sleep(4000);
		textEditor.typeText("  ");
		textEditor.pressShortcut(SWT.CTRL | SWT.SHIFT, 'f');
		bot.sleep(10000);
		// this should force-stop the watchdog recording interval
		bot.tree().getTreeItem("AJavaTestProject").select();
		bot.sleep(1000);
		bot.menu("WatchDog").click().menu("WatchDog Statistics").click();
		assertExists("27 seconds", 0);
		final SWTBotLabel measuredTimeTyping = bot.label("44% (12 seconds)");
		final SWTBotLabel measuredTimeReading = bot
				.label("56% (15 seconds)", 0);
		final SWTBotLabel measuredTimeOther = bot.label("0% (0 seconds)", 1);

		// this code asserts that we are comparing the correct order of results.
		// As we are accessing the widget to determine the order of the labels,
		// we need to run synchronous with the UI thread.
		Display.getDefault().syncExec(new Runnable() {
			public void run() {

				assertTrue(
						"Wrong order of measurements!",
						measuredTimeTyping.widget.getLocation().y < measuredTimeReading.widget
								.getLocation().y);
				assertTrue(
						"Wrong order of measurements!",
						measuredTimeReading.widget.getLocation().y < measuredTimeOther.widget
								.getLocation().y);
			}
		});

	}

	/**
	 * Creates a sample Java project.
	 */
	private void createSampleProject() {
		bot.menu("File").menu("New").menu("Project...").click();
		SWTBot activeWindowBot = bot.activeShell().bot();
		activeWindowBot.tree().getTreeItem("Java Project").select();
		activeWindowBot.button("Next >").click();
		activeWindowBot.textWithLabel("&Project name:").setText(
				"AJavaTestProject");
		activeWindowBot.button("Finish").click();
		activeWindowBot.sleep(1000);
		activeWindowBot = bot.activeShell().bot();
		activeWindowBot.button("Yes").click();
		activeWindowBot.sleep(1000);

	}
}