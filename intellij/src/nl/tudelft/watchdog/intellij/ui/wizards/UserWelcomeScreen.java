package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.intellij.ui.util.UIUtils;

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
        consentMessages.setLayout(new BoxLayout(consentMessages, BoxLayout.Y_AXIS));

        Container header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(header);
        header.add(new JLabel("<html>" +
                "<h2>WatchDog is a free, open-source plugin that tells how you code your software</h2>"));

        Container firstSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(firstSentence);
        firstSentence.add(new JLabel("It measures how you write Java code and tests. We never do anything bad with"));
        UIUtils.createHyperLinkLabel(firstSentence, "your purely numerical data", "http://www.testroots.org/testroots_watchdog.html#details");

        Container secondSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(secondSentence);
        secondSentence.add(new JLabel("Based on your development behavior, you can inspect"));
        UIUtils.createHyperLinkLabel(secondSentence, "a detailed report", "http://www.testroots.org/reports/sample_watchdog_report.pdf");

        Container thirdSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(thirdSentence);
        thirdSentence.add(new JLabel("By registering, you consent to us storing your data, perform scientific research, and publish it in a completely anonymized form."));

        Container fourthSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(fourthSentence);
        fourthSentence.add(new JLabel("For more information, please read our"));
        UIUtils.createHyperLinkLabel(fourthSentence, "privacy statement", "https://testroots.org/testroots_watchdog.html#privacy");

        return consentMessages;
    }

    private Component createProjectLogos() {
        Container logos = new JPanel(new GridLayout(0, 2));

        logos.add(this.createLogo("/images/watchdog_small.png"));
        logos.add(this.createLogo("/images/testroots_small.png"));

        return logos;
    }

}
