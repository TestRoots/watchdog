package nl.tudelft.watchdog.intellij.ui.preferences;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.Configurable;


import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;

import nl.tudelft.watchdog.intellij.ui.wizards.projectregistration.ProjectRegistrationWizard;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The WatchDog preference page in the IntelliJ preference settings.
 */
public class PreferencePage implements SearchableConfigurable, Configurable.NoScroll {
    private JComponent main;

    /**
     * The length of a WatchDog id.
     */
    private static final int ID_LENGTH = 40;

    /**
     * The project ID input field for this project.
     */
    private JTextField projectIDInput;

    /**
     * The user ID input field.
     */
    private JTextField userIDInput;

    /**
     * The server URL input field.
     */
    private JTextField serverURLInput;

    /**
     * Whether WatchDog should be enabled in this project.
     */
    private JCheckBox enableWatchdogInput;

    /**
     * Whether authentication is enabled.
     */
    private JCheckBox enableAuthentication;

    /**
     * Whether logging is enabled.
     */
    private JCheckBox enableLogging;

    /**
     * WatchDog preferences.
     */
    private Preferences preferences = Preferences.getInstance();

    /**
     * The name of this preference page.
     */
    public final static String name = "WatchDog Preferences";

    private boolean isModified = false;

    @Nls
    @Override
    public String getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        JPanel localGroup = UIUtils.createGroup(main, "Local Settings", 1);
        UIUtils.createLabel(localGroup, "Here you can define the local WatchDog settings for this project.");
        UIUtils.createLabel(localGroup, "");
        JPanel projectJPanel = UIUtils.createFlowJPanelLeft(localGroup);
        UIUtils.createLabel(projectJPanel, "Project-ID ");
        projectIDInput = UIUtils.createLimitedTextInputField(projectJPanel, ID_LENGTH);
        ProjectPreferenceSetting projectSetting = preferences.getOrCreateProjectSetting(WatchDogUtils.getProjectName());
        projectIDInput.setText(projectSetting.projectId);

        enableWatchdogInput = UIUtils.createCheckBox(localGroup, "Monitor this project with WatchDog ");
        enableWatchdogInput.setSelected(projectSetting.enableWatchdog);
        UIUtils.createLabel(main, "");

        JPanel globalGroup = UIUtils.createGroup(main, "Global Settings", 1);
        UIUtils.createLabel(globalGroup,
                "Here you can enter settings that will affect WatchDog no matter which project you have opened.  ");
        UIUtils.createLabel(globalGroup, "");

        JPanel userJPanel = UIUtils.createFlowJPanelLeft(globalGroup);
        UIUtils.createLabel(userJPanel, "User-ID ");
        userIDInput = UIUtils.createLimitedTextInputField(userJPanel, ID_LENGTH);
        userIDInput.setText(preferences.getUserId());

        JPanel serverJPanel = UIUtils.createFlowJPanelLeft(globalGroup);
        UIUtils.createLabel(serverJPanel, "Server-URL ");
        serverURLInput = UIUtils.createTextInputField(serverJPanel, WatchDogGlobals.DEFAULT_SERVER_URI.length());
        serverURLInput.setMinimumSize(serverURLInput.getPreferredSize());
        serverURLInput.setMaximumSize(serverURLInput.getPreferredSize());
        serverURLInput.setText(preferences.getServerURI());
        serverURLInput.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                isModified = true;
            }
        });

        enableAuthentication = UIUtils.createCheckBox(globalGroup, "Enable Authentication");
        enableAuthentication.setSelected(preferences.isAuthenticationEnabled());
        enableLogging = UIUtils.createCheckBox(globalGroup, "Enable Logging");
        enableLogging.setSelected(preferences.isLoggingEnabled());
        addModificationListeners();
        return main;
    }

    private void checkURLfield(String url) {
        url = url.trim();
        if (!url.endsWith("/")) {
            url = url.concat("/");
        }
        if (!UrlValidator.getInstance().isValid(url)) {
            Messages.showErrorDialog("<html>The URL you entered for the WatchDog server<br> seems to be invalid!", "Invalid Input");
            isModified = true;
        } else {
            preferences.setServerURI(url);
        }
    }

    private void addModificationListeners() {

        enableWatchdogInput.getAccessibleContext().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                isModified = true;
            }
        });

        projectIDInput.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                isModified = true;
            }
        });

        enableAuthentication.getAccessibleContext().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                isModified = true;
            }
        });

        userIDInput.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                isModified = true;
            }
        });

        enableLogging.getAccessibleContext().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                isModified = true;
            }
        });
    }


    @Override
    public boolean isModified() {
        return isModified;
    }

    @Override
    public void apply() throws ConfigurationException {
        isModified = false;
        preferences.setAuthenticationEnabled(enableAuthentication.isSelected());
        preferences.registerProjectUse(WatchDogUtils.getProjectName(), enableWatchdogInput.isSelected());
        checkAndUpdateProjectID(projectIDInput.getText());
        checkAndUpdateUserID(userIDInput.getText());
        checkURLfield(serverURLInput.getText());
        preferences.setLoggingEnabled(enableLogging.isSelected());
    }

    private void checkAndUpdateUserID(String userID) {
        if (!preferences.isAuthenticationEnabled()) {
            preferences.setUserId(userID);
            return;
        }
        if (userID.length() < ID_LENGTH) {
            Messages.showErrorDialog("User ID must have 40 characters!", "Invalid Input");
            isModified = true;
        } else {
            String url = NetworkUtils.buildExistingUserURL(userID);
            switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
                case SUCCESSFUL:
                    preferences.setUserId(userIDInput.getText());
                    break;
                case UNSUCCESSFUL:
                case NETWORK_ERROR:
                    Messages.showErrorDialog("User ID does not exist!", "Invalid Input");
                    isModified = true;
                    break;
            }
        }
    }

    private void checkAndUpdateProjectID(String projectID) {
        String projectName = WatchDogUtils.getProjectName();
        if (!preferences.isAuthenticationEnabled() || !preferences.getOrCreateProjectSetting(projectName).enableWatchdog) {
            preferences.registerProjectId(WatchDogUtils.getProjectName(), projectID);
            return;
        }
        if (projectID.length() < ID_LENGTH) {
            if(enableWatchdogInput.isSelected() && WatchDogUtils.isEmpty(projectIDInput.getText())) {
                new ProjectRegistrationWizard("Project Registration", WatchDogUtils.getProject()).show();
                return;
            }
            Messages.showErrorDialog("Project ID must have 40 characters!", "Invalid Input");
            isModified = true;
        } else {
            String url = NetworkUtils.buildExistingProjectURL(projectID);
            switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
                case SUCCESSFUL:
                    preferences.registerProjectId(WatchDogUtils.getProjectName(), projectID);
                    break;
                case UNSUCCESSFUL:
                case NETWORK_ERROR:
                    Messages.showErrorDialog("Project ID does not exist!", "Invalid Input");
                    isModified = true;
                    break;
            }
        }
    }

    @Override
    public void reset() {
        preferences.setDefaults();
    }

    @Override
    public void disposeUIResources() {

    }

    @NotNull
    @Override
    public String getId() {
        return name;
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }
}
