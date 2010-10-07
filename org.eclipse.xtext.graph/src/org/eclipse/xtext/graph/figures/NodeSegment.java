package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.graph.figures.layouts.SequenceLayout;
import org.eclipse.xtext.graph.figures.primitives.AbstractNode;
import org.eclipse.xtext.graph.figures.primitives.NodeType;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

/**
 * A segment containing a single {@link Node}.
 * 
 * @author koehnlein
 */
public class NodeSegment extends AbstractSegmentFigure {

	public NodeSegment(EObject eObject, NodeType nodeType, String name, PrimitiveFigureFactory primitiveFactory, Region textRegion) {
		super(eObject);
		AbstractNode node = primitiveFactory.createNode(nodeType, eObject, name, this, textRegion);
		setEntry(node);
		setExit(node);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new SequenceLayout();
	}

}
