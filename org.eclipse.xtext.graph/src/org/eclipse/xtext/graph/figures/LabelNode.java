package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.ecore.EObject;

public class LabelNode extends AbstractNode {

	public LabelNode(EObject grammarElement, String text) {
		super(grammarElement, text);
		setOpaque(false);
	}

	@Override
	protected Border createBorder() {
		return new MarginBorder(INSETS);
	}
}
