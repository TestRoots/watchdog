package nl.tudelft.watchdog.ui.preferences;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The WatchDog preference page in the Eclipse preference settings.
 */
public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private Text projectInput;
	private Button monitorWatchdog;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Preferences.getInstance().getStore());
		setDescription("Settings for WatchDog");
	}

	@Override
	protected Control createContents(Composite parent) {
		UIUtils.createLabel(
				"These are WatchDog's settings. They are for advanced users only.",
				parent);
		UIUtils.createLabel("", parent);
		Group localGroup = createGroup(parent, "Local Settings", 1);
		UIUtils.createLabel(
				"Here you can define the local WatchDog settings for this workspace.",
				localGroup);
		UIUtils.createLabel("", localGroup);

		Composite projectComposite = UIUtils.createGridedComposite(localGroup,
				2);
		projectComposite.setLayoutData(UIUtils.createFullGridUsageData());
		Label projectIdLabel = UIUtils.createLabel("Project-ID ",
				projectComposite);
		projectInput = UIUtils.createTextInput(projectComposite);

		monitorWatchdog = new Button(localGroup, SWT.CHECK);
		monitorWatchdog.setText("Monitor this workspace with WatchDog ");

		UIUtils.createLabel("", parent);

		Group globalGroup = createGroup(parent, "Global Settings", 1);
		UIUtils.createLabel(
				"Here you can enter settings that will affect WatchDog no matter which workspace you have opened.  ",
				globalGroup);
		UIUtils.createLabel("", globalGroup);

		return super.createContents(globalGroup);
	}

	@Override
	public boolean performOk() {
		return super.performOk();
	}

	@Override
	protected void createFieldEditors() {
		addField(new IDFieldEditor(Preferences.USERID_KEY, "User-ID"));
		addField(new BooleanFieldEditor(Preferences.LOGGING_ENABLED_KEY,
				"Enable Logging", getFieldEditorParent()));
	}

	/** A specific field editor allowing input of valid user IDs only. */
	class IDFieldEditor extends StringFieldEditor {
		/** Constructor, delegating call to parent's constructor. */
		public IDFieldEditor(String key, String description) {
			super(key, description, getFieldEditorParent());
		}

		@Override
		protected void createControl(Composite parent) {
			super.createControl(parent);
			setTextLimit(40);
		}
	}

	/**
	 * Creates and returns a {@link Group} with an enclosed GridData layout with
	 * the given number of columns.
	 */
	private Group createGroup(Composite area, String name, int columns) {
		Group group = new Group(area, SWT.None);
		group.setText(name);
		group.setLayout(new GridLayout(columns, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL));
		return group;
	}
}
