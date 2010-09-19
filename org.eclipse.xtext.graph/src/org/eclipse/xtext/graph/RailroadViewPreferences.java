package org.eclipse.xtext.graph;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.inject.Singleton;

/**
 * Handles preferences of the railroad diagram view.
 * 
 * @author koehnlein
 */
@Singleton
public class RailroadViewPreferences extends AbstractPreferenceInitializer {

	private ScopedPreferenceStore preferenceStore;
	
	public static final String LINK_WITH_EDITOR_KEY = "linkWithEditor";

	public RailroadViewPreferences() {
		preferenceStore = new ScopedPreferenceStore(new ConfigurationScope(), "Xtext Grammar View");
	}

	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.setDefault(LINK_WITH_EDITOR_KEY, false);
	}

	public boolean isLinkWithEditor() {
		return preferenceStore.getBoolean(LINK_WITH_EDITOR_KEY);
	}
	
	public void setLinkWithEditor(boolean isLinkWithEditor) {
		preferenceStore.setValue(LINK_WITH_EDITOR_KEY, isLinkWithEditor);
	}
	
	public ScopedPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}
}
