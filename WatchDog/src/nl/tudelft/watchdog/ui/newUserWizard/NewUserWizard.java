package nl.tudelft.watchdog.ui.newUserWizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * A wizard that allows to register a new user or set an existing user. Does
 * some magic tricks to enable skipping of pages and recalculation of Finish
 * button.
 */
public class NewUserWizard extends Wizard {
	/** The first page in the wizard. */
	FirstPage firstPage;

	ExistingUserEndingPage existingUserEndingPage;

	/** Allows a shortcut to the finish button. */
	boolean shortcutToCanFinish = false;

	@Override
	public void addPages() {
		firstPage = new FirstPage();
		addPage(firstPage);
		addPage(new RegistrationPage());
		addPage(new TimeAllocationPage());
		existingUserEndingPage = new ExistingUserEndingPage();
		addPage(existingUserEndingPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == firstPage) {
			if (firstPage.hasValidUserId()) {
				return existingUserEndingPage;
			}
		}
		return super.getNextPage(page);
	}

	/**
	 * {@inheritDoc} Since we updated {@link #getNextPage(IWizardPage)}, the
	 * order from getPreviousPage must be updated, too.
	 */
	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page == existingUserEndingPage) {
			return firstPage;
		}
		return super.getPreviousPage(page);
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == existingUserEndingPage
				&& existingUserEndingPage.canFinish()) {
			return true;
		}
		return super.canFinish();
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
