package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;

/**
 * A node showing a label only.
 * 
 * @author koehnlein
 */
public class LabelNode extends AbstractNode {

	public LabelNode(EObject grammarElement, String text, Font font) {
		super(grammarElement, text, font);
		setOpaque(false);
	}

	@Override
	protected Border createBorder() {
		return new MarginBorder(PADDING);
	}
}
