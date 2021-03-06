package org.eclipse.xtext.graph.figures.primitives;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.xtext.graph.figures.ILayoutConstants;

/**
 * @author koehnlein
 */
public class RoundedNode extends AbstractNode {

	public RoundedNode(EObject eObject, String text, Font font, Region textRegion) {
		super(eObject, text, font, textRegion);
		setOpaque(true);
	}

	@Override
	protected Border createBorder() {
		return new MarginBorder(PADDING);
	}

	@Override
	protected Color getUnselectedBackgroundColor() {
		return ColorConstants.buttonDarker;
	}

	@Override
	public void paintFigure(Graphics graphics) {
		float lineInset = .5f;
		int inset1 = (int) Math.floor(lineInset);
		int inset2 = (int) Math.ceil(lineInset);

		Rectangle r = Rectangle.SINGLETON.setBounds(getBounds());
		r.x += inset1;
		r.y += inset1;
		r.width -= inset1 + inset2;
		r.height -= inset1 + inset2;

		graphics.fillRoundRectangle(r, Math.max(0, ILayoutConstants.ROUNDED_RECTANGLE_RADIUS - (int) lineInset), Math.max(0, ILayoutConstants.ROUNDED_RECTANGLE_RADIUS - (int) lineInset));
		graphics.drawRoundRectangle(r, Math.max(0, ILayoutConstants.ROUNDED_RECTANGLE_RADIUS - (int) lineInset), Math.max(0, ILayoutConstants.ROUNDED_RECTANGLE_RADIUS - (int) lineInset));
	}
}
