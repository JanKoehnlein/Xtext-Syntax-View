package org.eclipse.xtext.graph.figures.layouts;

import java.util.Map;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.xtext.graph.figures.ILayoutConstants;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

/**
 * @author koehnlein
 */
@Singleton
public class RailroadConnectionRouter extends AbstractRouter {

	public static BendConstraint CONCAVE_START = new BendConstraint(true, false);
	public static BendConstraint CONCAVE_END = new BendConstraint(false, false);
	public static BendConstraint CONVEX_START = new BendConstraint(true, true);
	public static BendConstraint CONVEX_END = new BendConstraint(false, true);

	public static class BendConstraint {
		private boolean isStart = false;
		private boolean isConvex = false;

		private BendConstraint(boolean isStart, boolean isConvex) {
			this.isStart = isStart;
			this.isConvex = isConvex;
		}

		public boolean isStart() {
			return isStart;
		}

		public boolean isConvex() {
			return isConvex;
		}
	}

	private Map<Connection, BendConstraint> constraints = Maps.newHashMap();

	@Override
	public void route(Connection connection) {
		PointList points = connection.getPoints();
		points.removeAllPoints();
		Point startPoint = getStartPoint(connection);
		connection.translateToRelative(startPoint);
		points.addPoint(startPoint);
		Point endPoint = getEndPoint(connection);
		connection.translateToRelative(endPoint);
		Object constraint = getConstraint(connection);
		if (constraint instanceof BendConstraint) {
			int dx = Integer.signum(endPoint.x - startPoint.x) * ILayoutConstants.CONNECTION_RADIUS;
			int dy = Integer.signum(endPoint.y - startPoint.y) * ILayoutConstants.CONNECTION_RADIUS;
			// can be simplified but becomes unreadable
			if (((BendConstraint) constraint).isConvex()) {
				if (((BendConstraint) constraint).isStart()) {
					points.addPoint(startPoint.x - dx, startPoint.y + dy);
					points.addPoint(startPoint.x - dx, endPoint.y - dy);
					points.addPoint(startPoint.x , endPoint.y);
				} else {
					points.addPoint(endPoint.x, startPoint.y);
					points.addPoint(endPoint.x + dx, startPoint.y + dy);
					points.addPoint(endPoint.x + dx, endPoint.y - dy);
				}
			} else {
				if (((BendConstraint) constraint).isStart()) {
					points.addPoint(startPoint.x + dx, startPoint.y + dy);
					points.addPoint(startPoint.x + dx, endPoint.y - dy);
					points.addPoint(startPoint.x + 2 * dx, endPoint.y);
				} else {
					points.addPoint(endPoint.x - 2 * dx, startPoint.y);
					points.addPoint(endPoint.x - dx, startPoint.y + dy);
					points.addPoint(endPoint.x - dx, endPoint.y - dy);
				}
			}
		}
		points.addPoint(endPoint);
		connection.setPoints(points);
	}

	@Override
	public void setConstraint(Connection connection, Object constraint) {
		if (constraint instanceof BendConstraint) {
			constraints.put(connection, (BendConstraint) constraint);
		}
	}

	@Override
	public Object getConstraint(Connection connection) {
		return constraints.get(connection);
	}

}
