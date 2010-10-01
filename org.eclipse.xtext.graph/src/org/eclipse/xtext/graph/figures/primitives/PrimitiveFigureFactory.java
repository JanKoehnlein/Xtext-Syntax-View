package org.eclipse.xtext.graph.figures.primitives;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.graph.figures.layouts.RailroadConnectionRouter;

import com.google.inject.Inject;

public class PrimitiveFigureFactory {

	@Inject
	private RailroadConnectionRouter connectionRouter;

	private Font font;

	public AbstractNode createNode(NodeType nodeType, EObject grammarElement, String name,
			IFigure containerFigure) {
		AbstractNode node = newNode(nodeType, grammarElement, name);
		containerFigure.add(node);
		return node;
	}

	public CrossPoint createCrossPoint(IFigure containerFigure) {
		CrossPoint crossPoint = new CrossPoint();
		containerFigure.add(crossPoint);
		return crossPoint;
	}

	public Connection createConnection(CrossPoint source, CrossPoint target, IFigure containerFigure) {
		Connection connection = new Connection(source, target);
		containerFigure.add(connection);
		connection.setConnectionRouter(connectionRouter);
		return connection;
	}

	public Connection createConnection(CrossPoint source, CrossPoint target, IFigure containerFigure, RailroadConnectionRouter.BendConstraint bendConstraint) {
		Connection connection = new Connection(source, target);
		containerFigure.add(connection);
		connection.setConnectionRouter(connectionRouter);
		connectionRouter.setConstraint(connection, bendConstraint);
		return connection;
	}

	protected AbstractNode newNode(NodeType type, EObject grammarElement, String text) {
		switch (type) {
		case RECTANGLE:
			return new RectangleNode(grammarElement, text, getFont());
		case ROUNDED:
			return new RoundedNode(grammarElement, text, getFont());
		case ERROR:
			return new ErrorNode(grammarElement, text, getFont());
		case LABEL:
			return new LabelNode(grammarElement, text, getFont());
		default:
			throw new IllegalArgumentException("Unknown node type " + type);
		}
	}

	protected Font getFont() {
		if (font == null) {
			if (Display.getCurrent() != null) {
				font = Display.getCurrent().getSystemFont();
			} else {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						font = Display.getCurrent().getSystemFont();
					}
				});
			}
		}
		return font;
	}
}
