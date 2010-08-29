package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;


public class ErrorNode extends RectangleNode {

	public ErrorNode(String text) {
		this(null, text);
	}

	public ErrorNode(EObject grammarElement, String text) {
		super(grammarElement, text);
		setBackgroundColor(ColorConstants.red);
	}

	
}
