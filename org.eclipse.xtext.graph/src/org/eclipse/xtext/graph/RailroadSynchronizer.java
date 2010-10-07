package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.graph.actions.RailroadSelectionLinker;
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

	public void partActivated(IWorkbenchPart part) {
		updateView(part);
	}

	private void updateView(IWorkbenchPart part) {
		if (part instanceof XtextEditor) {
			XtextEditor xtextEditor = (XtextEditor) part;
			IXtextDocument xtextDocument = xtextEditor.getDocument();
			if (xtextDocument != lastActiveDocument) {
				selectionLinker.setXtextEditor(xtextEditor);
				final IFigure contents = xtextDocument.readOnly(new IUnitOfWork<IFigure, XtextResource>() {
					public IFigure exec(XtextResource resource) throws Exception {
						return createFigure(resource);
					}
				});
				if (contents != null) {
					view.setContents(contents);
					if (lastActiveDocument != null) {
						lastActiveDocument.removeModelListener(this);
					}
					lastActiveDocument = xtextDocument;
					lastActiveDocument.addModelListener(this);
				}
			}
		}
	}

	private IFigure createFigure(XtextResource state) {
		EList<EObject> contents = state.getContents();
		if (!contents.isEmpty()) {
			EObject rootObject = contents.get(0);
			return transformer.transform(rootObject);
		}
		return null;
	}

	public void partBroughtToTop(IWorkbenchPart part) {
	}

	public void partClosed(IWorkbenchPart part) {
	}

	public void partDeactivated(IWorkbenchPart part) {
	}

	public void partOpened(IWorkbenchPart part) {
	}

	public void modelChanged(XtextResource resource) {
		view.setContents(createFigure(resource));
	}

	public Font getFont() {
		return font;
	}
}
