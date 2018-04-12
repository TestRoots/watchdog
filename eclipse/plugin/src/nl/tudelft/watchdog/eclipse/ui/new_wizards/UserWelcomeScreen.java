package nl.tudelft.watchdog.eclipse.ui.new_wizards;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import nl.tudelft.watchdog.eclipse.ui.util.BrowserOpenerSelection;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

public class UserWelcomeScreen extends WizardPage {

	protected UserWelcomeScreen() {
		super("Welcome to WatchDog!");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.createTopHeader(container);
		this.createConsentMessage(container);
		this.createProjectLogos(container);
		
		this.setControl(container);
	}
	
	private void createTopHeader(Composite parent) {
		Composite header = new Composite(parent, SWT.NONE);
		header.setLayout(new GridLayout(2, true));

        Composite leftColumn = new Composite(header, SWT.NONE);
        leftColumn.setLayout(new RowLayout(SWT.VERTICAL));
        leftColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite rightColumn = new Composite(header, SWT.NONE);
        rightColumn.setLayout(new GridLayout(1, false));
        rightColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Label headerText = new Label(leftColumn, SWT.NONE);
        headerText.setText("Welcome to WatchDog!");
        headerText.setFont(JFaceResources.getFontRegistry().getBold(""));
        
        Label firstSentence = new Label(leftColumn, SWT.NONE);
        firstSentence.setText("This wizard guides you through the setup of WatchDog Plugin.");
        
        Label secondSentence = new Label(leftColumn, SWT.NONE);
        secondSentence.setText("Please register, so you can access your personal online report.");

        UIUtils.createLogo(rightColumn, "resources/images/tudelft_with_frame.png");
    }

    private void createConsentMessage(Composite parent) {
    	Composite consentMessages = new Composite(parent, SWT.NONE);
    	consentMessages.setLayout(new GridLayout(1, false));
		consentMessages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label headerText = new Label(consentMessages, SWT.NONE);
        headerText.setText("WatchDog is a free, open-source plugin that tells how you code your software");
        headerText.setFont(JFaceResources.getFontRegistry().getBold(""));
        
        Link firstLink = new Link(consentMessages, SWT.NONE);
        firstLink.setText("It measures how you write Java code and tests. We never do anything bad with <a href=\"http://www.testroots.org/testroots_watchdog.html#details\">your purely numerical data</a>");
        firstLink.addSelectionListener(new BrowserOpenerSelection());
        
        Link secondLink = new Link(consentMessages, SWT.NONE);
        secondLink.setText("Based on your development behavior, you can inspect <a href=\"http://www.testroots.org/reports/sample_watchdog_report.pdf\">a detailed report</a>");
        secondLink.addSelectionListener(new BrowserOpenerSelection());
        
        Label thirdSentence = new Label(consentMessages, SWT.NONE);
        thirdSentence.setText("By registering, you consent to us storing your data, perform scientific research, and publish it in a completely anonymized form.");
        
        Link fourthLink = new Link(consentMessages, SWT.NONE);
        fourthLink.setText("For more information, please read our <a href=\"https://testroots.org/testroots_watchdog.html#privacy\">privacy statement</a>");
        fourthLink.addSelectionListener(new BrowserOpenerSelection());
    }

    private void createProjectLogos(Composite parent) {
        Composite logos = new Composite(parent, SWT.NONE);
		logos.setLayout(new GridLayout(2, true));
		logos.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        UIUtils.createLogo(logos, "resources/images/watchdog_small.png");
        UIUtils.createLogo(logos, "resources/images/testroots_small.png");
    }


}
