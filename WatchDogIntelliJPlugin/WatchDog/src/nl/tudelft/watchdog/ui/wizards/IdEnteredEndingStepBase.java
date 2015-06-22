package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.util.UIUtils;

import javax.swing.*;


/**
 * Possible finishing step in the wizard. If the id exists on the server, or the
 * server is not reachable, the user can exit here. Can be subclassed with the
 * particular type of id.
 */
public abstract class IdEnteredEndingStepBase extends WizardStep {

    /**
     * An encouraging message for the end of a sentence.
     */
    public static final String ENCOURAGING_END_MESSAGE = "\n\nHappy hours-collecting and prize-winning with WatchDog! \nThe longer you use WatchDog, the higher your chances of winning!";

    /**
     * The dynamic composite.
     */
    private JPanel dynamicPanel;

    /**
     * The user id (either as retrieved from the previous step or as freshly
     * accepted from the server).
     */
    protected String id;

    private String idType;

    /**
     * Constructor.
     */
    protected IdEnteredEndingStepBase(String idType, int stepNumber, RegistrationWizardBase wizard) {
        super("Existing " + idType + " step", stepNumber, wizard);
        this.idType = idType;
    }

    /**
     * Connects to the server, querying for the id returned by {@link #getId()},
     * and displays an according wizard step based on the result of the query to
     * the server.
     */
    private void connectToServer() {
        id = getId();
        String url = buildTransferURLforId();
        switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
            case SUCCESSFUL:
                setId();
                setTitle("Welcome back!");
                descriptionText = "Thanks for using your existing " + idType + "-ID!";
                setErrorMessageAndStepComplete(null);
                dynamicPanel = createSuccessfulExistingID(topPanel);
                break;
            case UNSUCCESSFUL:
            case NETWORK_ERROR:
                setTitle("Wrong " + idType + " id");
                setErrorMessageAndStepComplete("This " + idType + " id does not exist.");
                dynamicPanel = createIdNotFoundJPanel(topPanel);
                break;
        }

    }

    /**
     * @return the id this step operates on.
     */
    abstract protected String getId();

    /**
     * @return the URL to connect to create a new Id of the type.
     */
    abstract protected String buildTransferURLforId();

    /**
     * Sets the id in according wizard for further processing after this step
     * has ended, so that it gets set in the preferences.
     */
    abstract protected void setId();

    /**
     * Creates and returns a composite in case of successful verification of
     * user existence.
     */
    private JPanel createSuccessfulExistingID(JPanel parent) {
        JPanel panel = UIUtils.createGridedJPanel(parent, 1);
        String title = "Everything worked perfectly.";
        String message = "You are using an existing " + idType + " id: ";
        WizardStep.createSuccessMessage(panel, title, message, id);
        UIUtils.createLabel(
                panel,
                "Your "
                        + idType
                        + " id has been registered. You can change the id and other WatchDog preferences in the IntelliJ settings.");
        return panel;
    }

    /**
     * Creates and returns a composite in case of not finding the user.
     */
    private JPanel createIdNotFoundJPanel(JPanel parent) {
        JPanel panel = UIUtils.createGridedJPanel(parent, 1);
        String title = "Problem registering existing user!";
        String message = idType.substring(0, 1).toUpperCase()
                .concat(idType.substring(1))
                + " not found!";
        WizardStep.createFailureMessage(panel, title, message);
        UIUtils.createLabel(
                panel,
                "<html>We could not find the "
                        + idType
                        + " id on our server. <br>Did you miss-type the id? Or did something go wrong while copy-and-pasting your user id? <br><br>Please, go back and correct it or retry.");
        return panel;
    }

    @Override
    public void _init() {
        super._init();
        if (dynamicPanel != null) {
            dynamicPanel.removeAll();
        }
        connectToServer();
        topPanel.updateUI();
    }
}
