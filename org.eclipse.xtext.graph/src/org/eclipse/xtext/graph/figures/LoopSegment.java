package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.graph.figures.layouts.ParallelLayout;
import org.eclipse.xtext.graph.figures.layouts.RailroadConnectionRouter;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * A segment with an additional recursive connection from the exit to the entry of the child segment.
 * 
 * @author koehnlein
 */
public class LoopSegment extends AbstractSegmentFigure {

	public LoopSegment(EObject eObject, ISegmentFigure child, PrimitiveFigureFactory primitiveFactory) {
		super(eObject);
		setEntry(primitiveFactory.createCrossPoint(this));
		CrossPointSegment crossPointSegment = new CrossPointSegment(eObject, primitiveFactory);
		if(ILayoutConstants.ROUTE_MULTIPLE_TOP) {
			add(crossPointSegment);
			add(child);
		} else {
			add(child);
			add(crossPointSegment);
		}
		setExit(primitiveFactory.createCrossPoint(this));
		primitiveFactory.createConnection(getExit(), crossPointSegment.getExit(), this, RailroadConnectionRouter.CONVEX_START);
		primitiveFactory.createConnection(crossPointSegment.getEntry(), getEntry(), this, RailroadConnectionRouter.CONVEX_END);
		primitiveFactory.createConnection(getExit(), child.getExit(), this, RailroadConnectionRouter.CONCAVE_START);
		primitiveFactory.createConnection(child.getEntry(), getEntry(), this, RailroadConnectionRouter.CONCAVE_END);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new ParallelLayout(ILayoutConstants.CONNECTION_RADIUS);
	}

}
