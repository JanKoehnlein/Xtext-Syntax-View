package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;

public class Connection extends PolylineConnection {

	public Connection(CrossPoint source, CrossPoint target,
			boolean isDecorateSource, boolean isDecorateTarget) {
		createAnchors(source, target);
		if(isDecorateSource) 
			setSourceDecoration(createDecoration());
		if(isDecorateTarget)
			setTargetDecoration(createDecoration());
		setLineCap(SWT.CAP_SQUARE);
	}

	private void createAnchors(CrossPoint source, CrossPoint target) {
		ConnectionAnchor sourceAnchor = new Anchor(source);
		ConnectionAnchor targetAnchor = new Anchor(target);
		setSourceAnchor(sourceAnchor);
		setTargetAnchor(targetAnchor);
	}

	private PolylineDecoration createDecoration() {
		PolylineDecoration decoration = new PolylineDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(-1, -1);
		decorationPointList.addPoint(0, 0);
		decorationPointList.addPoint(-1, 1);
		decoration.setTemplate(decorationPointList);
		return decoration;
	}
	
	public static class Anchor extends ChopboxAnchor {
		
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
