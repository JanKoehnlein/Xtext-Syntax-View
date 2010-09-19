package org.eclipse.xtext.graph.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.graph.RailroadViewPreferences;

import com.google.inject.Inject;

/**
 * @author koehnlein
 */
public class LinkWithEditorAction extends Action {

	private RailroadViewPreferences preferences;
	
	public LinkWithEditorAction() {
		setText("Link with editor");
		setDescription("Links this view to the editor");
		setToolTipText("Links this view to the editor");
		setImageDescriptor(getSharedImage(ISharedImages.IMG_ELCL_SYNCED));
		setDisabledImageDescriptor(getSharedImage(ISharedImages.IMG_ELCL_SYNCED_DISABLED));
	}
	
	protected ImageDescriptor getSharedImage(String name) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(name);
	}

	@Inject
	public void setPreferences(RailroadViewPreferences preferences) {
		this.preferences = preferences;	
		setChecked(preferences.isLinkWithEditor());
	}

	@Override
	public void run() {
		boolean newState = !preferences.isLinkWithEditor();
		preferences.setLinkWithEditor(newState);
		setChecked(newState);
	}
}
