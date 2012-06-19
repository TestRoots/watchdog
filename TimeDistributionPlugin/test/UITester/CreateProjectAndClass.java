package UITester;

import org.junit.Assert;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;

import eclipseUIReader.Events.DocumentAttentionEvent;
import eclipseUIReader.Events.DocumentNotifier;
import eclipseUIReader.Events.IDocumentAttentionListener;

public class CreateProjectAndClass extends UITestCaseSWT {

	private boolean eventFired = false;
	
	/* @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
		ui.ensureThat(new WorkbenchLocator().hasFocus());
		ui.ensureThat(ViewLocator.forName("Welcome").isClosed());
		ui.ensureThat(new WorkbenchLocator().isMaximized());
	}

	/**
	 * Main test method.
	 */
	public void testCreateProjectAndClass() throws Exception {
		
		/*
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {
			
			@Override
			public void onDocumentAlert(DocumentAttentionEvent evt) {
				//eventFired = true;
				System.out.println("Event got fired!");
			}
		});
		*/
		
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(2, new FilteredTreeItemLocator("Java Project"));
		ui.enterText("TestProject");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellShowingCondition("Open Associated Perspective?"));
		ui.click(new ButtonLocator("&Yes"));
		ui.wait(new ShellDisposedCondition("Open Associated Perspective?"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		
		ui.contextClick(new TreeItemLocator("TestProject/src", new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "New/Class");
		ui.wait(new ShellShowingCondition("New Java Class"));
		ui.enterText("TestClassA");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Class"));
		
		Assert.assertTrue(eventFired);
		
	}

}