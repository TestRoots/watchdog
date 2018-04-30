package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.intellij.ui.util.UIUtils;

import javax.swing.*;
import java.awt.*;

import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;

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
                "<h1>" + WELCOME + "</h1>" +
                WIZARD_GUIDE+ "<br>" +
                REGISTER));

        rightColumn.add(createLogo("/images/tudelft_with_frame.png"));

        return header;
    }

    private Component createConsentMessage() {
        Container consentMessages = new JPanel();
        consentMessages.setLayout(new BoxLayout(consentMessages, BoxLayout.Y_AXIS));

        Container header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(header);
        header.add(new JLabel("<html>" +
                "<h2>" + OPENSOURCE_PLUGIN + "</h2>"));

        Container firstSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(firstSentence);
        firstSentence.add(new JLabel(GATHER_NUMERICAL_DATA));
        UIUtils.createHyperLinkLabel(firstSentence, Links.NUMERICAL_DATA);

        Container secondSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(secondSentence);
        secondSentence.add(new JLabel(DEVELOPMENT_BEHAVIOR));
        UIUtils.createHyperLinkLabel(secondSentence, Links.DETAILED_REPORT);

        Container thirdSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(thirdSentence);
        thirdSentence.add(new JLabel(REGISTRATION_CONSENT));

        Container fourthSentence = new JPanel(new FlowLayout(FlowLayout.LEFT));
        consentMessages.add(fourthSentence);
        fourthSentence.add(new JLabel(FOR_MORE_INFORMATION));
        UIUtils.createHyperLinkLabel(fourthSentence, Links.PRIVACY_STATEMENT);

        return consentMessages;
    }

    private Component createProjectLogos() {
        Container logos = new JPanel(new GridLayout(0, 2));

        logos.add(this.createLogo("/images/watchdog_small.png"));
        logos.add(this.createLogo("/images/testroots_small.png"));

        return logos;
    }

}
