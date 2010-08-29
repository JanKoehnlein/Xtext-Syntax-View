package org.eclipse.xtext.graph;

import java.util.Map;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

import com.google.common.collect.Maps;

public class RailroadConnectionRouter extends AbstractRouter {

	private Map<Connection, LoopConstraint> constraints = Maps.newHashMap();

	public static final int RADIUS = 5;

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
		if (constraint instanceof LoopConstraint) {
			int loopY = ((LoopConstraint) constraint).getLoopY();
			int direction = Integer.signum(endPoint.x - startPoint.x);
			points.addPoint(startPoint.x, loopY - RADIUS);
			points.addPoint(startPoint.x + direction * RADIUS, loopY);
			points.addPoint(endPoint.x - direction * RADIUS, loopY);
			points.addPoint(endPoint.x, loopY - RADIUS);
		} else if (startPoint.y < endPoint.y) {
			points.addPoint(startPoint.x, endPoint.y - RADIUS);
			points.addPoint(startPoint.x + RADIUS, endPoint.y);
		} else if (startPoint.y > endPoint.y) {
			points.addPoint(endPoint.x - RADIUS, startPoint.y);
			points.addPoint(endPoint.x, startPoint.y - RADIUS);
		}
		points.addPoint(endPoint);
		connection.setPoints(points);
	}

	@Override
	public void setConstraint(Connection connection, Object constraint) {
		if (constraint instanceof LoopConstraint) {
			constraints.put(connection, (LoopConstraint) constraint);
		}
	}

	public static class LoopConstraint {
		private int loopY = 0;

		public LoopConstraint(int loopY) {
			super();
			this.loopY = loopY;
		}

		public int getLoopY() {
			return loopY;
		}
	}

	@Override
	public Object getConstraint(Connection connection) {
		return constraints.get(connection);
	}

}
