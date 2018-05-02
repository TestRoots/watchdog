package nl.tudelft.watchdog.eclipse.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import nl.tudelft.watchdog.eclipse.ui.util.BrowserOpenerSelection;

import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.eclipse.ui.util.UIUtils.HEADER_FONT;

class UserWelcomeScreen extends WizardPage {

	protected UserWelcomeScreen() {
		super(WELCOME);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		this.createTopHeader(container);
		new Label(container, SWT.NONE);
		this.createConsentMessage(container);
		new Label(container, SWT.NONE);
		this.createProjectLogos(container);

		this.setControl(container);
	}

	private void createTopHeader(Composite parent) {
		Label headerText = new Label(parent, SWT.NONE);
		headerText.setText(WELCOME);
		headerText.setFont(HEADER_FONT);

		Label firstSentence = new Label(parent, SWT.NONE);
		firstSentence.setText(WIZARD_GUIDE);

		Label secondSentence = new Label(parent, SWT.NONE);
		secondSentence.setText(REGISTER);
	}

	private void createConsentMessage(Composite parent) {
		Composite consentMessages = new Composite(parent, SWT.NONE);
		consentMessages.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		Label headerText = new Label(consentMessages, SWT.NONE);
		headerText.setText(OPENSOURCE_PLUGIN);
		headerText.setFont(HEADER_FONT);

		Link firstLink = new Link(consentMessages, SWT.NONE);
		firstLink.setText(GATHER_NUMERICAL_DATA + Links.NUMERICAL_DATA.toHTMLURL() + ".");
		firstLink.addSelectionListener(new BrowserOpenerSelection());

		Link secondLink = new Link(consentMessages, SWT.NONE);
		secondLink.setText(DEVELOPMENT_BEHAVIOR + Links.DETAILED_REPORT.toHTMLURL() + ".");
		secondLink.addSelectionListener(new BrowserOpenerSelection());

		Label thirdSentence = new Label(consentMessages, SWT.NONE);
		thirdSentence.setText(REGISTRATION_CONSENT);

		Link fourthLink = new Link(consentMessages, SWT.NONE);
		fourthLink.setText(FOR_MORE_INFORMATION + Links.PRIVACY_STATEMENT.toHTMLURL() + ".");
		fourthLink.addSelectionListener(new BrowserOpenerSelection());
	}

	private void createProjectLogos(Composite parent) {
		Composite logos = new Composite(parent, SWT.NONE);
		logos.setLayout(new GridLayout(2, true));

		RegistrationStep.createLogo(logos, "resources/images/watchdog_small.png")
				.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, false));
		RegistrationStep.createLogo(logos, "resources/images/testroots_small.png")
				.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, false));
	}

}
