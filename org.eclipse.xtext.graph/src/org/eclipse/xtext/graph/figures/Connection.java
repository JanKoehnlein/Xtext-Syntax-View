package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;

public class Connection extends PolylineConnection {

	public Connection(CrossPoint source, CrossPoint target) {
		createAnchors(source, target);
		setLineCap(SWT.CAP_SQUARE);
	}

	private void createAnchors(CrossPoint source, CrossPoint target) {
		ConnectionAnchor sourceAnchor = new Anchor(source);
		ConnectionAnchor targetAnchor = new Anchor(target);
		setSourceAnchor(sourceAnchor);
		setTargetAnchor(targetAnchor);
	}

	protected static class Anchor extends ChopboxAnchor {

		public Anchor(IFigure owner) {
			super(owner);
		}

		@Override
		public Point getLocation(Point reference) {
			IFigure owner = getOwner();
			if (!(owner instanceof AbstractNode)) {
				Point center = owner.getBounds().getCenter().getCopy();
				owner.translateToAbsolute(center);
				return center;
			}
			return super.getLocation(reference);
		}

	}

}
