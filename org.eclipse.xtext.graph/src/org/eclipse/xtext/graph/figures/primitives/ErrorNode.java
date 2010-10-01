package org.eclipse.xtext.graph.figures.primitives;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Node representing an erroneous grammar element.
 * 
 * @author koehnlein
 */
public class ErrorNode extends RectangleNode {

	public ErrorNode(EObject grammarElement, String text, Font font) {
		super(grammarElement, text, font);
	}

	@Override
	protected Color getUnselectedBackgroundColor() {
		return ColorConstants.red;
	}
}
