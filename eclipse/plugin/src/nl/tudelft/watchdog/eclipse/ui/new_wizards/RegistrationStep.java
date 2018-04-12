package nl.tudelft.watchdog.eclipse.ui.new_wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.eclipse.ui.util.BrowserOpenerSelection;

public abstract class RegistrationStep extends WizardPage {

	private Composite dynamicContent;
	private Composite container;

	protected RegistrationStep(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.createUserRegistrationIntroduction(container);
		this.createUserIsRegisteredQuestion(container);
		
		this.setControl(container);
	}

	abstract void createUserRegistrationIntroduction(Composite container);
	
	private void createUserIsRegisteredQuestion(Composite parent) {
		Composite questionContainer = new Composite(parent, SWT.NONE);
		questionContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		questionContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		Label question = new Label(questionContainer, SWT.NONE);
		question.setText("Do you have a WatchDog registration?");
		
		Composite buttons = new Composite(questionContainer, SWT.NONE);
		buttons.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button yes = new Button(buttons, SWT.RADIO);
		yes.setText("yes");
		whenSelectedCreatePanelAndUpdateUI(yes, getIdInputPanel());
		
		Button no = new Button(buttons, SWT.RADIO);
		no.setText("no");
		whenSelectedCreatePanelAndUpdateUI(no, getRegistrationPanel());
		
		dynamicContent = new Composite(container, SWT.NONE);
		dynamicContent.setLayout(new GridLayout(1, false));
	}

	private void whenSelectedCreatePanelAndUpdateUI(Button button,
			BiConsumer<Composite, Consumer<Boolean>> compositeConstructor) {
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection()) {
					System.out.println(button);
					dynamicContent.dispose();
					dynamicContent = new Composite(container, SWT.NONE);
					dynamicContent.setLayout(new GridLayout(1, false));
					dynamicContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
					
					compositeConstructor.accept(dynamicContent, (hasValidUserId) -> {
						System.out.println(hasValidUserId);
					});
					
					container.layout(true, true);
					container.redraw();
					container.update();
				}
			}
		});
	}
	
	abstract BiConsumer<Composite, Consumer<Boolean>> getIdInputPanel();

	abstract BiConsumer<Composite, Consumer<Boolean>> getRegistrationPanel();

	public static void createErrorMessageLabel(Composite container, Exception exception) {
		new Label(container, SWT.NONE).setText(exception.getMessage());
		new Label(container, SWT.NONE).setText("Are you connected to the internet, and is port 80 open?");
		Link link = new Link(container, SWT.NONE);
		link.setText("Please contact us via <a href=\"https://www.testroots.org\">our website</a>. We can help troubleshoot the issue!");
		link.addSelectionListener(new BrowserOpenerSelection());
	}

}
