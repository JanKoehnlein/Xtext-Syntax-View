package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.graph.figures.layouts.SequenceLayout;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * A segment containing a single {@link CrossPoint}.
 * 
 * @author koehnlein
 */
public class CrossPointSegment extends AbstractSegmentFigure {

	public CrossPointSegment(EObject grammarElement, PrimitiveFigureFactory primitiveFactory) {
		super(grammarElement);
		CrossPoint crossPoint = primitiveFactory.createCrossPoint(this);
		setEntry(crossPoint);
		setExit(crossPoint);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new SequenceLayout(ILayoutConstants.MIN_SEGMENT_HEIGHT);
	}

}
