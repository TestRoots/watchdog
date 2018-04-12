package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import nl.tudelft.watchdog.core.ui.wizards.RegistrationWizard;
import nl.tudelft.watchdog.core.ui.wizards.UserRegistrationInputPanel;
import nl.tudelft.watchdog.core.ui.wizards.UserRegistrationStep;

public class EclipseUserRegistrationPage extends WizardPage {

	private UserRegistrationStep delegate;
	private Composite composite;
	private Frame container;

	protected EclipseUserRegistrationPage(RegistrationWizard wizard) {
		super("User registration");
		
		this.delegate = new UserRegistrationStep(wizard) {
			
			@Override
			protected Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
				return callback -> new UserRegistrationInputPanel(callback) {
					
					private static final long serialVersionUID = -2309442398462470992L;

					@Override
					public JComboBox<String> createComboBox() {
						return new JComboBox<>();
					}
				};
			}
			
			@Override
			protected void repaintCallback() {
				System.out.println("repaint");
				Display.getDefault().syncExec(() -> {
					container.add(new JLabel());
					container.revalidate();
					new Button(composite, SWT.TOGGLE);
					container.repaint();
					composite.layout(true, true);
					composite.redraw();
					composite.update();
				});
			}
		};
	}

	@Override
	public void createControl(Composite parent) {
		this.composite = new Composite(parent, SWT.EMBEDDED);
		composite.setLayout(new FillLayout());

		this.container = SWT_AWT.new_Frame(composite);
		this.delegate._initWithPanel(container);
		
		this.setControl(composite);
	}

}
