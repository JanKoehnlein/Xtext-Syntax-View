package org.eclipse.xtext.graph.figures;

import java.util.List;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.graph.figures.layouts.SequenceLayout;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * A sequence of segments.
 * 
 * @author koehnlein
 */
public class SequenceSegment extends AbstractSegmentFigure {

	public SequenceSegment(EObject element, List<ISegmentFigure> body, PrimitiveFigureFactory primitiveFactory) {
		super(element);
		if (body.isEmpty()) {
			setEntry(primitiveFactory.createCrossPoint(this));
			setExit(getEntry());
		} else {
			boolean isFirst = true;
			CrossPoint currentEnd = null;
			for (ISegmentFigure child : body) {
				if (isFirst) {
					setEntry(child.getEntry());
					currentEnd = child.getExit();
					isFirst = false;
				} else {
					primitiveFactory.createConnection(currentEnd, child.getEntry(), this);
					currentEnd = child.getExit();
				}
				add(child);
			}
			setExit(currentEnd);
		}
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new SequenceLayout();
	}

}
