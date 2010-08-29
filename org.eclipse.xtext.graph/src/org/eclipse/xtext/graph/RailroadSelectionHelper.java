package org.eclipse.xtext.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.URI;
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
		Point location = me.getLocation();
		IFigure selectedFigure = view.findFigureAt(location);
		while (selectedFigure != null
				&& !(selectedFigure instanceof IGrammarElementReferer))
			selectedFigure = selectedFigure.getParent();
		if (selectedFigure != null) {
			IGrammarElementReferer element = (IGrammarElementReferer) selectedFigure;
			URI grammarElementURI = element.getGrammarElementURI();
			if(lastSelection!=null) {
				lastSelection.setSelected(false);
			}
			if(selectedFigure instanceof AbstractNode) {
				((AbstractNode) selectedFigure).setSelected(true);
				lastSelection = (AbstractNode) selectedFigure;
			} else {
				lastSelection = null;
			}
			if (grammarElementURI != null) {
				uriEditorOpener.open(grammarElementURI, selectedFigure instanceof AbstractNode);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
	}
}
