package org.eclipse.xtext.graph.figures.primitives;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * A point where the railroad track could fork, i.e. start or end of
 * {@link Connection}.
 * 
 * @author koehnlein
 */
public class CrossPoint extends Figure {

	public CrossPoint() {
		if (this.getClass() == CrossPoint.class)
			setPreferredSize(new Dimension(4, 4));
		setOpaque(true);
		setBackgroundColor(ColorConstants.blue);
	}

}
