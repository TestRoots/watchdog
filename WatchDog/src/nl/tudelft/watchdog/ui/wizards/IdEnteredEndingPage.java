package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.util.UIUtils;

import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the id exists on the server, or the
 * server is not reachable, the user can exit here. Can be subclassed with the
 * particular type of id.
 */
public abstract class IdEnteredEndingPage extends FinishableWizardPage {

	/** An encouraging message for the end of a sentence. */
	public static final String ENCOURAGING_END_MESSAGE = "\n\nHappy hours-collecting and prize-winning with WatchDog! \nThe longer you use WatchDog, the higher your chances of winning!";

	/** The top-level composite. */
	private Composite topComposite;

	/** The dynamic composite. */
	private Composite dynamicComposite;

	/**
	 * The user id (either as retrieved from the previous page or as freshly
	 * accepted from the server).
	 */
	protected String id;

	private String idType;

	/** Constructor. */
	protected IdEnteredEndingPage(String idType) {
		super("Existing " + idType + " page");
		this.idType = idType;
	}

	/**
	 * Connects to the server, querying for the id returned by {@link #getId()},
	 * and displays an according wizard page based on the result of the query to
	 * the server.
	 */
	private void connectToServer() {
		id = getId();
		String url = buildTransferURLforId();
		switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
		case SUCCESSFUL:
			setId();
			setTitle("Welcome back!");
			setDescription("Thanks for using your existing " + idType + "!");
			setPageComplete(true);
			dynamicComposite = createSuccessWizzard(topComposite);
			break;
		case UNSUCCESSFUL:
		case NETWORK_ERROR:
			setTitle("Wrong " + idType + " id");
			setErrorMessageAndPageComplete("This " + idType
					+ " id does not exist.");
			dynamicComposite = createIdNotFoundComposite(topComposite);
			break;
		}
	}

	/**
	 * @return the id this page operates on.
	 */
	abstract protected String getId();

	/**
	 * @return the URL to connect to create a new Id of the type.
	 */
	abstract protected String buildTransferURLforId();

	/**
	 * Sets the id in according wizard for further processing after this page
	 * has ended, so that it gets set in the preferences.
	 */
	abstract protected void setId();

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (dynamicComposite != null) {
				dynamicComposite.dispose();
			}
			connectToServer();
			dynamicComposite.layout(true);
			topComposite.layout(true);
		}
	};

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("", topComposite);
		setControl(topComposite);
	}

	/**
	 * Creates and returns a composite in case of successful verification of
	 * user existence.
	 */
	private Composite createSuccessWizzard(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel("Everything worked perfectly.", composite);
		UIUtils.createWrappingLabel(
				"Your "
						+ idType
						+ " id  "
						+ id
						+ "  has been registered with this Eclipse installation. You can change the id and other WatchDog settings in the Eclipse preferences."
						+ ENCOURAGING_END_MESSAGE, composite);
		return composite;
	}

	/** Creates and returns a composite in case of not finding the user. */
	private Composite createIdNotFoundComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel(
				idType.substring(0, 1).toUpperCase()
						.concat(idType.substring(1))
						+ " not found!", composite);
		UIUtils.createWrappingLabel(
				"We could not find the "
						+ idType
						+ " id  "
						+ id
						+ "  on our server. Did you miss-type the id? Or did something go wrong while copy-and-pasting your user id? Please, go back and correct it or retry.",
				composite);
		return composite;
	}

	@Override
	public boolean canFinish() {
		return isPageComplete();
	}

}
