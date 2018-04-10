package nl.tudelft.watchdog.intellij.ui.wizards;

import javax.swing.*;
import java.awt.*;

class UserWelcomeScreen extends WizardStep {

    @Override
    void _initWithPanel(Container panel) {
        panel.add(createTopHeader());
        panel.add(createConsentMessage());
        panel.add(createProjectLogos());
    }

    @Override
    boolean isFinishedWithStep() {
        return true;
    }

    private Component createTopHeader() {
        Container header = new JPanel(new GridLayout(0, 2));

        Container leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        header.add(leftColumn);

        Container rightColumn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 3));
        header.add(rightColumn);

        leftColumn.add(new JLabel("<html>" +
                "<h1>Welcome to WatchDog!</h1>" +
                "This wizard guides you through the setup of WatchDog Plugin.<br>" +
                "Please register, so you can access your personal online report."));

        rightColumn.add(createLogo("/images/tudelft_with_frame.png"));

        return header;
    }

    private Component createConsentMessage() {
        Container consentMessages = new JPanel();

        consentMessages.add(new JLabel("<html>" +
                "<h2>WatchDog is a free, open-source plugin that tells how you code your software</h2>" +
                "It measures how you write Java code and tests. We never do anything bad with <a href=\"http://www.testroots.org/testroots_watchdog.html#details\">your purely numerical data</a>.<br>" +
                "You can inspect a <a href=\"http://www.testroots.org/reports/sample_watchdog_report.pdf\">detailed report</a> on your development behaviour.<br>" +
                "By proceeding to use this plugin, you consent with us using your anonymous data to perform research and publish papers with.<br>"));

        return consentMessages;
    }

    private Component createProjectLogos() {
        Container logos = new JPanel(new GridLayout(0, 2));

        logos.add(this.createLogo("/images/watchdog_small.png"));
        logos.add(this.createLogo("/images/testroots_small.png"));

        return logos;
    }

}
