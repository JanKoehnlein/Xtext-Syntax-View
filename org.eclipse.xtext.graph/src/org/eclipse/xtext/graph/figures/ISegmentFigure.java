package org.eclipse.xtext.graph.figures;

import org.eclipse.xtext.graph.figures.primitives.CrossPoint;

/**
 * Basic building block for {@link RailroadTrack}s. Most segments can have child
 * segments, which are connected by their entry and exit points. This way, more
 * complicated structures can be composed by nesting segments.
 * 
 * @author koehnlein
 */
public interface ISegmentFigure extends ICompositeFigure {

	CrossPoint getEntry();

	CrossPoint getExit();

}
