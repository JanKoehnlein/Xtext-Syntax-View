package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;

/**
 * Interface for all building blocks of a railroad diagram.
 * 
 * @author koehnlein
 */
public interface ISegmentFigure extends IFigure, IEObjectReferer {

	CrossPoint getEntry();

	CrossPoint getExit();
}
