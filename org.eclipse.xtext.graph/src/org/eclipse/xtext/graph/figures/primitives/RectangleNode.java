package org.eclipse.xtext.graph.figures.primitives;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Font;

/** 
 * @author koehnlein
 */
public class RectangleNode extends AbstractNode {

	public RectangleNode(EObject eObject, String text, Font font, Region textRegion) {
		super(eObject, text, font, textRegion);
		setOpaque(true);
	}

	@Override
	protected Border createBorder() {
		MarginBorder marginBorder = new MarginBorder(PADDING - 1);
		LineBorder lineBorder = new LineBorder(1);
		return new CompoundBorder(lineBorder, marginBorder);
	}

}
