package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.ui.wizards.YesNoDontKnowChoice;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.ui.util.BrowserOpenerSelection;

public abstract class RegistrationStep extends WizardPage {

	private Composite dynamicContent;
	private Composite container;
	private boolean isPageComplete;
	private WizardDialog dialog;

	protected RegistrationStep(String pageName, WizardDialog dialog) {
		super(pageName);
		this.dialog = dialog;
	}
	
	@Override
	public boolean isPageComplete() {
		return isPageComplete;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new RowLayout(SWT.VERTICAL));

		this.createUserRegistrationIntroduction(container);
		this.createUserIsRegisteredQuestion(container);
		
		this.setControl(container);
	}

	abstract void createUserRegistrationIntroduction(Composite container);
	
	private void createUserIsRegisteredQuestion(Composite parent) {
		Composite questionContainer = new Composite(parent, SWT.NONE);
		questionContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		
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
					dynamicContent.dispose();
					dynamicContent = new Composite(container, SWT.NONE);
					dynamicContent.setLayout(new RowLayout(SWT.VERTICAL));
					
					compositeConstructor.accept(dynamicContent, (hasValidId) -> {
						isPageComplete = hasValidId;
						dialog.updateButtons();
						container.layout(true, true);
						container.redraw();
						container.update();
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

	static void createErrorMessageLabel(Composite container, Exception exception) {
		new Label(container, SWT.NONE).setText(exception.getMessage());
		new Label(container, SWT.NONE).setText("Are you connected to the internet, and is port 80 open?");
		Link link = new Link(container, SWT.NONE);
		link.setText("Please contact us via <a href=\"https://www.testroots.org\">our website</a>. We can help troubleshoot the issue!");
		link.addSelectionListener(new BrowserOpenerSelection());
	}
	
	static Text createLinkedLabelTextField(String labelText, String tooltip, Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		Text input = new Text(container, SWT.NONE);
		input.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		input.setTextLimit(150);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				input.forceFocus();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				input.forceFocus();
			}
		});
		return input;
	}
	
	static Label createLogo(Composite logoContainer, String imageLocation) {
		Label watchdogLogo = new Label(logoContainer, SWT.NONE);
		
		ImageDescriptor watchdogLogoImageDescriptor = Activator
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID, imageLocation);
		watchdogLogo.setImage(watchdogLogoImageDescriptor.createImage());
		
		return watchdogLogo;
	}

	public static YesNoDontknowButtonGroup createYesNoDontKnowQuestionWithLabel(String labelText, Composite container) {
		new Label(container, SWT.NONE).setText(labelText);
		
		YesNoDontknowButtonGroup buttons = new YesNoDontknowButtonGroup(container);
		buttons.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		buttons.addButton("yes", YesNoDontKnowChoice.Yes);
		buttons.addButton("no", YesNoDontKnowChoice.No);
		buttons.addButton("Don't know", YesNoDontKnowChoice.DontKnow);
		
		return buttons;
	}
	
	static class YesNoDontknowButtonGroup extends Composite {
		YesNoDontKnowChoice selected = YesNoDontKnowChoice.DontKnow;
		
		YesNoDontknowButtonGroup(Composite parent) {
			super(parent, SWT.NONE);
		}

		void addButton(String text, YesNoDontKnowChoice choice) {
			Button button = new Button(this, SWT.RADIO);
			button.setText(text);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					selected = choice;
				}
			});
		}
	}

}
