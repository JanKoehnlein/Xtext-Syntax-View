package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.graph.figures.layouts.ParallelLayout;
import org.eclipse.xtext.graph.figures.layouts.RailroadConnectionRouter;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * A segment with an additional connection bypassing the child segment.
 * 
 * @author koehnlein
 */
public class BypassSegment extends AbstractSegmentFigure {

	public BypassSegment(EObject eObject, ISegmentFigure child, PrimitiveFigureFactory primitiveFactory) {
		super(eObject);
		setEntry(primitiveFactory.createCrossPoint(this));
		CrossPointSegment crossPointSegment = new CrossPointSegment(eObject, primitiveFactory);
		if(ILayoutConstants.ROUTE_OPTIONAL_TOP) {
			add(crossPointSegment);
			add(child);
		} else {
			add(child);
			add(crossPointSegment);
		}
		setExit(primitiveFactory.createCrossPoint(this));
		primitiveFactory.createConnection(getEntry(), crossPointSegment.getEntry(), this, RailroadConnectionRouter.CONCAVE_START);
		primitiveFactory.createConnection(crossPointSegment.getExit(), getExit(), this, RailroadConnectionRouter.CONCAVE_END);
		primitiveFactory.createConnection(getEntry(), child.getEntry(), this, RailroadConnectionRouter.CONCAVE_START);
		primitiveFactory.createConnection(child.getExit(), getExit(), this, RailroadConnectionRouter.CONCAVE_END);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new ParallelLayout();
	}

}
