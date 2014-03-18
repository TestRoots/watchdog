package nl.tudelft.watchdog.gui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The watchdog preference page in the Eclipse preference settings.
 */
// TODO (MMB) We do not want the user to be able to change the timeouts. Remove
// from preference page
public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(WatchdogPreferences.getInstance().getStore());
		setDescription("Settings for WatchDog");
	}

	@Override
	protected void createFieldEditors() {
		addField(new IntegerFieldEditor(WatchdogPreferences.TIMEOUT_TYPING,
				"Editing time out (ms)", getFieldEditorParent()));
		addField(new IntegerFieldEditor(WatchdogPreferences.TIMEOUT_READING,
				"Reading time out (ms)", getFieldEditorParent()));
		addField(new UserIDFieldEditor());
		addField(new BooleanFieldEditor(WatchdogPreferences.LOGGING_ENABLED,
				"Enable Logs", getFieldEditorParent()));
	}

	/** A specific field editor allowing input of valid user IDs only. */
	class UserIDFieldEditor extends StringFieldEditor {
		/** Constructor, delegating call to parent's constructor. */
		public UserIDFieldEditor() {
			super(WatchdogPreferences.USERID, "User Id", getFieldEditorParent());
		}

		@Override
		protected void createControl(Composite parent) {
			super.createControl(parent);
			setTextLimit(40);
		}

		@Override
		protected boolean doCheckState() {
			// TODO (MMB) query server with ID
			return super.doCheckState();
		}
	}

}
