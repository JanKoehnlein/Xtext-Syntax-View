package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.graph.figures.layouts.SequenceLayout;
import org.eclipse.xtext.graph.figures.primitives.AbstractNode;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;
import org.eclipse.xtext.graph.figures.primitives.NodeType;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * A railroad track with a label and an exit node. Between these arbitrary
 * {@link ISegmentFigure}s can be inserted.
 * 
 * @author koehnlein
 */
public class RailroadTrack extends AbstractSegmentFigure {

	public RailroadTrack(EObject eObject, String name, ISegmentFigure body, PrimitiveFigureFactory primitiveFactory,
			Region textRegion) {
		super(eObject);
		AbstractNode label = primitiveFactory.createNode(NodeType.LABEL, eObject, name, this, textRegion);
		if (body != null)
			add(body);
		CrossPoint exit = primitiveFactory.createCrossPoint(this);
		if (body != null) {
			primitiveFactory.createConnection(label, body.getEntry(), this);
			primitiveFactory.createConnection(body.getExit(), exit, this);
		} else {
			primitiveFactory.createConnection(label, exit, this);
		}
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new SequenceLayout();
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}
}
