package nl.tudelft.watchdog.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
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
		setPreferenceStore(PreferenceUtils.store);
		setDescription("Settings for WatchDog");
	}

	@Override
	protected void createFieldEditors() {
		addField(new IntegerFieldEditor(PreferenceUtils.TIMEOUT_TYPING,
				"Editing time out (ms)", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceUtils.TIMEOUT_READING,
				"Reading time out (ms)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceUtils.DEBUGGING_ENABLED,
				"Enable debugging", getFieldEditorParent()));
	}

}
