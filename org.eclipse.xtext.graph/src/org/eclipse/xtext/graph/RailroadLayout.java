package org.eclipse.xtext.graph;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.xtext.graph.figures.Connection;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.util.MaxSizeAggregator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RailroadLayout extends AbstractLayout {

	private static final int HSPACE = 5;
	private static final int VSPACE = 10;

	private Map<IFigure, Constraint> constraints = Maps.newHashMap();

	@Override
	public void layout(IFigure container) {
		if (container instanceof Diagram) {
			Diagram diagram = (Diagram) container;
			Map<Integer, MaxSizeAggregator> track2columnWidths = Maps.newHashMap();
			MaxSizeAggregator rowHeights = new MaxSizeAggregator();
			int maxTrack = -1;
			for (Object child : diagram.getChildren()) {
				Constraint constraint = getRailroadLayoutConstraint((IFigure) child);
				if (constraint != null) {
					int track = constraint.getTrack();
					maxTrack = Math.max(track, maxTrack);
					if (child instanceof CrossPoint) {
						CrossPoint crossPoint = (CrossPoint) child;
						Dimension preferredSize = crossPoint.getPreferredSize(
								-1, -1);
						MaxSizeAggregator columnWidths = track2columnWidths.get(track);
						if(columnWidths == null) {
							columnWidths = new MaxSizeAggregator();
							track2columnWidths.put(track, columnWidths);
						}
						columnWidths.aggregate(constraint.getColumn(), 2
								* HSPACE + preferredSize.width);
						rowHeights.aggregate(constraint.getRow(), VSPACE
								+ preferredSize.height);
					} else if (child instanceof Connection) {
						rowHeights.aggregate(
								((Constraint) constraint).getRow(), VSPACE);
					}
				}
			}

			List<int[]> x = Lists.newArrayList();
			for(int track=0; track<=maxTrack; ++ track) {
				x.add(track2columnWidths.get(track).getPositions());
			}
			int[] y = rowHeights.getPositions();
			Rectangle diagramBounds = new Rectangle();
			for (Object child : diagram.getChildren()) {
				Constraint constraint = getRailroadLayoutConstraint((IFigure) child);
				if (constraint != null) {
					int track = constraint.getTrack();
					int row = constraint.getRow();
					int rowPosition = y[row];
					int rowHeight = rowHeights.get(row); 
					
					if (child instanceof CrossPoint) {
						CrossPoint crossPoint = (CrossPoint) child;
						Dimension preferredSize = crossPoint.getPreferredSize(
								-1, rowHeight);
						int voffset = (child instanceof AbstractNode) ? 0
								: (rowHeight - VSPACE) / 2;
						Rectangle bounds = new Rectangle(
								x.get(track)[constraint.getColumn()], rowPosition + voffset,
								preferredSize.width, preferredSize.height);
						crossPoint.setBounds(bounds);
						diagramBounds.union(bounds);
					} else if (child instanceof Connection) {
						Connection connection = (Connection) child;
						ConnectionRouter connectionRouter = connection
								.getConnectionRouter();
						connectionRouter.setConstraint(connection,
								new RailroadConnectionRouter.LoopConstraint(
										rowPosition + (rowHeight - VSPACE) / 2));
						diagramBounds.union(connection.getBounds());
					}
				}
			}
			preferredSize = new Dimension(diagramBounds.width,
					diagramBounds.height);
		}
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		if (preferredSize == null) {
			layout(container);
		}
		return preferredSize;
	}

	@Override
	public Object getConstraint(IFigure child) {
		return constraints.get(child);
	}

	public Constraint getRailroadLayoutConstraint(IFigure child) {
		return constraints.get(child);
	}

	@Override
	public void setConstraint(IFigure child, Object constraint) {
		if (constraint instanceof Constraint)
			constraints.put(child, (Constraint) constraint);
	}

	public static class Constraint {

		private int row;
		private int column;
		private int track;

		public Constraint(int row, int column, int track) {
			super();
			this.row = row;
			this.column = column;
			this.track = track;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		public int getTrack() {
			return track;
		}

		public void setTrack(int track) {
			this.track = track;
		}

	}
}
