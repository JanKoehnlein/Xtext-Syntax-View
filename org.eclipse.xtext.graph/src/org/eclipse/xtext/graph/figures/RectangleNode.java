package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.ecore.EObject;

public class RectangleNode extends AbstractNode {

	public RectangleNode(EObject grammarElement, String text) {
		super(grammarElement, text);
		setOpaque(true);
	}

	@Override
	protected Border createBorder() {
		MarginBorder marginBorder = new MarginBorder(INSETS - 1);
		LineBorder lineBorder = new LineBorder(1);
		return new CompoundBorder(lineBorder, marginBorder);
	}

}
