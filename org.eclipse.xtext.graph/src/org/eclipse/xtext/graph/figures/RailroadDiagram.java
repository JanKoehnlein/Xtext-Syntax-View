package org.eclipse.xtext.graph.figures;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.emf.ecore.EObject;

/**
 * The railroad diagram figure. A railroad diagram consists of {@link RailroadTrack}s
 * 
 * @author koehnlein
 */
public class RailroadDiagram extends AbstractCompositeFigure {

	public RailroadDiagram(EObject grammarElement, List<ICompositeFigure> children) {
		super(grammarElement);
		setOpaque(true);
		setBackgroundColor(ColorConstants.white);
		for (ICompositeFigure child : children) 
			add(child);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setSpacing(ILayoutConstants.VSPACE_BETWEEN_TRACKS);
		return layout;
	}
	
}
