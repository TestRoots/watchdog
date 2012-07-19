package nl.tudelft.watchdog.timeDistributionPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PrefPage extends FieldEditorPreferencePage  implements
		IWorkbenchPreferencePage {
	static IPreferenceStore store;
	private final static String TIMEOUT_EDITING = "TIMEOUT_EDITING";
	private final static String TIMEOUT_READING = "TIMEOUT_READING";
	
	static{
		store = Activator.getDefault().getPreferenceStore();
		store.setDefault(TIMEOUT_EDITING, 3000);
		store.setDefault(TIMEOUT_READING, 5000);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(store);
		setDescription("Settings for Watchdog");
	}

	@Override
	protected void createFieldEditors() {
		addField(new IntegerFieldEditor(TIMEOUT_EDITING, "Editing time out (ms)", getFieldEditorParent()));
		addField(new IntegerFieldEditor(TIMEOUT_READING, "Reading time out (ms)", getFieldEditorParent()));
	}
	
	public static int getTimeOutEditing(){
		int ms = store.getInt(TIMEOUT_EDITING);
		if(ms < 1) ms = Activator.getDefault().getPreferenceStore().getDefaultInt(TIMEOUT_EDITING);
		return ms;
	}
	
	public static int getTimeOutReading(){
		int ms = store.getInt(TIMEOUT_READING);
		if(ms < 1) ms = Activator.getDefault().getPreferenceStore().getDefaultInt(TIMEOUT_READING);
		return ms;
	}

}
