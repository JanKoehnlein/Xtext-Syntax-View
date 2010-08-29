package org.eclipse.xtext.graph;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

public class RailroadSynchronizer implements IPartListener, IXtextModelListener {

	private IXtextDocument lastActiveDocument;
	private RailroadView view;
	private Font font;

	public RailroadSynchronizer(RailroadView view) {
		this.view = view;
		this.font = Display.getDefault().getSystemFont();
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		updateDiagram(part);
	}

	private void updateDiagram(IWorkbenchPart part) {
		if (part instanceof XtextEditor) {
			XtextEditor xtextEditor = (XtextEditor) part;
			IXtextDocument xtextDocument = xtextEditor.getDocument();
			final Diagram diagram = xtextDocument
					.readOnly(new IUnitOfWork<Diagram, XtextResource>() {
						@Override
						public Diagram exec(XtextResource state)
								throws Exception {
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

	private Diagram createDiagram(XtextResource state) {
		EList<EObject> contents = state.getContents();
		if (!contents.isEmpty()) {
			EObject rootObject = contents.get(0);
			if (rootObject instanceof Grammar)
				return new RailroadCreator().create((Grammar) rootObject, getFont());
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
