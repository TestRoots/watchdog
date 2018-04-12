package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.awt.Component;
import java.awt.Container;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import nl.tudelft.watchdog.core.ui.wizards.UserWelcomePanel;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

public class EclipseUserWelcomePage extends WizardPage {

	protected EclipseUserWelcomePage() {
		super("User welcome");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.EMBEDDED);
		composite.setLayout(new FillLayout());

		Container container = SWT_AWT.new_Frame(composite);
		container.add(new UserWelcomePanel() {
			private static final long serialVersionUID = 3343597848123712399L;

			@Override
			protected Component createLogo(String iconLocation) {
				JLabel logo = new JLabel();

				logo.setIcon(new ImageIcon(UIUtils.getUrlFromImageLocation(iconLocation)));
				logo.setHorizontalAlignment(JLabel.CENTER);

				return logo;
			}
		});
		
		this.setControl(composite);
	}

}
