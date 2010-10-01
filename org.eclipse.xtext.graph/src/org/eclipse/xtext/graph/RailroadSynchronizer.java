package org.eclipse.xtext.graph;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.graph.actions.RailroadSelectionLinker;
import org.eclipse.xtext.graph.figures.RailroadDiagram;
import org.eclipse.xtext.graph.trafo.Xtext2RailroadTransformer;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Synchronizes the railroad diagram view with the active editor.
 * 
 * @author koehnlein
 */
@Singleton
public class RailroadSynchronizer implements IPartListener, IXtextModelListener {

	@Inject
	private RailroadView view;
	
	@Inject 
	private Xtext2RailroadTransformer transformer;
	
	@Inject 
	private RailroadSelectionLinker selectionLinker;
	
	private IXtextDocument lastActiveDocument;

	private Font font;

	@Override
	public void partActivated(IWorkbenchPart part) {
		updateView(part);
	}

	private void updateView(IWorkbenchPart part) {
		if (part instanceof XtextEditor) {
			XtextEditor xtextEditor = (XtextEditor) part;
			IXtextDocument xtextDocument = xtextEditor.getDocument();
			if (xtextDocument != lastActiveDocument) {
				selectionLinker.setXtextEditor(xtextEditor);
				final RailroadDiagram diagram = xtextDocument.readOnly(new IUnitOfWork<RailroadDiagram, XtextResource>() {
					@Override
					public RailroadDiagram exec(XtextResource state) throws Exception {
						return createDiagram(state);
					}
				});
				if (diagram != null) {
					view.setDiagram(diagram);
					if (lastActiveDocument != null) {
						lastActiveDocument.removeModelListener(this);
					}
					lastActiveDocument = xtextDocument;
					lastActiveDocument.addModelListener(this);
				}
			}
		}
	}

	private RailroadDiagram createDiagram(XtextResource state) {
		EList<EObject> contents = state.getContents();
		if (!contents.isEmpty()) {
			EObject rootObject = contents.get(0);
			if (rootObject instanceof Grammar)
				return (RailroadDiagram) transformer.transform(rootObject);
		}
		return null;
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
	}

	@Override
	public void modelChanged(XtextResource resource) {
		view.setDiagram(createDiagram(resource));
	}

	public Font getFont() {
		return font;
	}
}
