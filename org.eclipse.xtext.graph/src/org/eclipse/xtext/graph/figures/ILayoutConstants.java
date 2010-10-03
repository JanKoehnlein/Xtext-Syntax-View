package org.eclipse.xtext.graph.figures;

import org.eclipse.swt.graphics.Color;

/**
 * All constants used for layouting and rendering.
 * @author koehnlein
 *
 */
public interface ILayoutConstants {

	// common spacings
	int HSPACE = 10;
	
	int VSPACE = 10;

	int VSPACE_BETWEEN_TRACKS = 25;
	
	
	// nodes
	int ROUNDED_RECTANGLE_RADIUS = 7;
	
	Color NODE_SELECTION_COLOR = new Color(null, 115, 158, 227);
	

	// connections
	int CONNECTION_RADIUS = 5;
	
	boolean ROUTE_OPTIONAL_TOP = false;
	
	boolean ROUTE_MULTIPLE_TOP = true;


	// segments
	int MIN_SEGMENT_HEIGHT = 20;

	int PARALLEL_SEGMENT_HSPACE = 20;

}
