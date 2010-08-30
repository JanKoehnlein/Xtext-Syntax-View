package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.figures.IGrammarElementReferer;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.shared.Access;

public class RailroadSelectionHelper implements MouseListener {

	private RailroadView view;

	private IURIEditorOpener uriEditorOpener;

	private AbstractNode lastSelection;

	public RailroadSelectionHelper(RailroadView railroadView) {
		this.view = railroadView;
		uriEditorOpener = Access.getIURIEditorOpener().get();
	}

	@Override
	public void mousePressed(MouseEvent me) {
		selectElementAndOpenEditor(me, false);
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// do nothing
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		selectElementAndOpenEditor(me, true);
	}

	protected void selectElementAndOpenEditor(MouseEvent me, boolean isActivateEditor) {
		Point location = me.getLocation();
		IFigure selectedFigure = view.findFigureAt(location);
		while (selectedFigure != null && !(selectedFigure instanceof IGrammarElementReferer))
			selectedFigure = selectedFigure.getParent();
		if (selectedFigure != null) {
			IGrammarElementReferer element = (IGrammarElementReferer) selectedFigure;
			final URI grammarElementURI = element.getGrammarElementURI();
			if (lastSelection != null) {
				lastSelection.setSelected(false);
			}
			if (selectedFigure instanceof AbstractNode) {
				((AbstractNode) selectedFigure).setSelected(true);
				lastSelection = (AbstractNode) selectedFigure;
			} else {
				lastSelection = null;
			}
			if (grammarElementURI != null) {
				final boolean isSelectElement = selectedFigure instanceof AbstractNode;
				// enqueue to make sure the diagram is updated before
				if (isActivateEditor && isSelectElement) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							uriEditorOpener.open(grammarElementURI, isSelectElement);
						}
					});
				} else {
					uriEditorOpener.open(grammarElementURI, isSelectElement);
					view.getViewSite().getPage().activate(view);
				}
			}
		}
	}
}
