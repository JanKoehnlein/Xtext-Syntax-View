package org.eclipse.xtext.graph.figures;

import java.util.List;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.graph.figures.layouts.ParallelLayout;
import org.eclipse.xtext.graph.figures.layouts.RailroadConnectionRouter;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * Connects all child segments to a common entry and a common exit {@link CrossPoint}.
 * 
 * @author koehnlein
 */
public class ParallelSegment extends AbstractSegmentFigure {

	public ParallelSegment(EObject eObject, List<ISegmentFigure> children, PrimitiveFigureFactory primitiveFactory) {
		super(eObject);
		setEntry(primitiveFactory.createCrossPoint(this));
		if (children.isEmpty()) {
			setExit(getEntry());
		} else {
			setExit(primitiveFactory.createCrossPoint(this));
			for (ISegmentFigure child : children) {
				add(child);
				primitiveFactory.createConnection(getEntry(), child.getEntry(), this, RailroadConnectionRouter.CONCAVE_START);
				primitiveFactory.createConnection(child.getExit(), getExit(), this, RailroadConnectionRouter.CONCAVE_END);
			}
		}
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new ParallelLayout();
	}

}
