package org.eclipse.xtext.graph.trafo;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.CompoundElement;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.graph.RailroadConnectionRouter;
import org.eclipse.xtext.graph.RailroadLayout;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.figures.Connection;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.graph.figures.ErrorNode;
import org.eclipse.xtext.graph.figures.LabelNode;
import org.eclipse.xtext.graph.figures.RectangleNode;
import org.eclipse.xtext.graph.figures.RoundedNode;

import com.google.inject.internal.Lists;

public class RailroadFactory {
	
	private RailroadConnectionRouter connectionRouter;

	private RailroadLayout layout;

	private RailroadTransformer transformer;
	
	private Font font; 
	
	public RailroadFactory() {
		connectionRouter = new RailroadConnectionRouter();
		layout = new RailroadLayout();
		transformer = new RailroadTransformer(this);
	}

	protected Font getFont() {
		if(font == null) {
			if(Display.getCurrent() != null) {
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
	public Diagram createDiagram(Grammar grammar) {
		Diagram diagram = new Diagram(grammar);
		diagram.setLayoutManager(layout);
		GridPointer gridPointer = new GridPointer();
		transformer.transform(grammar, gridPointer, diagram);
		return diagram;
	}

	public CrossPoint createNode(NodeType nodeType,
			EObject element, String name, CrossPoint predecessor,
			GridPointer gridPointer, Diagram diagram) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(
				this, element, diagram);
		predecessor = helper.createEntryPoints(predecessor, gridPointer);
		gridPointer.resetMax();
		AbstractNode node = createNode(nodeType, element, name, gridPointer, diagram);
		createConnection(predecessor, node, diagram);
		CrossPoint exitPoint = helper
				.createAndConnectExitPoints(node, gridPointer);
		gridPointer.aggregate(gridPointer);
		return exitPoint;
	}

	public AbstractNode createNode(NodeType nodeType,
			EObject grammarElement, String name, GridPointer gridPointer, Diagram diagram) {
		AbstractNode node = createNode(nodeType, grammarElement, name);
		gridPointer.incColumn();
		layout.setConstraint(
				node,
				new RailroadLayout.Constraint(gridPointer.getRow(), gridPointer
						.getColumn(), gridPointer.getTrack()));
		diagram.add(node);
		return node;
	}

	public CrossPoint createCrossPoint(GridPointer gridPointer, Diagram diagram) {
		CrossPoint crossPoint = new CrossPoint();
		diagram.add(crossPoint);
		gridPointer.incColumn();
		layout.setConstraint(
				crossPoint,
				new RailroadLayout.Constraint(gridPointer.getRow(), gridPointer
						.getColumn(), gridPointer.getTrack()));
		return crossPoint;
	}

	public Connection createConnection(CrossPoint source, CrossPoint target, Diagram diagram) {
		Connection connection = new Connection(source, target);
		diagram.add(connection);
		connection.setConnectionRouter(connectionRouter);
		return connection;
	}

	public Connection createCardinalityConnection(CrossPoint source,
			CrossPoint target, GridPointer gridPointer, Diagram diagram) {
		gridPointer.incMaxRow();
		Connection connection = createConnection(source, target, diagram);
		layout.setConstraint(
				connection,
				new RailroadLayout.Constraint(gridPointer.getMaxRow(), gridPointer
						.getColumn(), gridPointer.getTrack()));
		return connection;
	}

	public CrossPoint createParallel(CompoundElement compound,
			CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(
				this, compound, diagram);
		predecessor = helper.createEntryPoints(predecessor, gridPointer);
		CrossPoint entryPoint = createCrossPoint(gridPointer, diagram);
		createConnection(predecessor, entryPoint, diagram);
		int startColumn = gridPointer.getColumn();
		CrossPoint exitPoint = createCrossPoint(gridPointer, diagram);
		GridPointer subGridPointer = gridPointer.clone();
		subGridPointer.resetMax();
		List<CrossPoint> subExitPoints = Lists.newArrayList();
		for (int i = 0; i < compound.getElements().size(); ++i) {
			AbstractElement e = compound.getElements().get(i);
			subGridPointer.setColumn(startColumn);
			GridPointer currentgridPointer = subGridPointer.clone();
			CrossPoint subEntryPoint = createCrossPoint(currentgridPointer, diagram);
			createConnection(entryPoint, subEntryPoint, diagram);
			CrossPoint subPoint = transformer.transform(e, subEntryPoint,
					currentgridPointer, diagram);
			CrossPoint subExitPoint = createCrossPoint(currentgridPointer, diagram);
			createConnection(subPoint, subExitPoint, diagram);
			subExitPoints.add(subExitPoint);
			subGridPointer.aggregate(currentgridPointer);
			subGridPointer.setRow(currentgridPointer.getMaxRow());
			subGridPointer.incRow();
			createConnection(subExitPoint, exitPoint, diagram);
		}
		for (CrossPoint subExitPoint : subExitPoints) {
			// place all sub ends at the outermost column
			layout.getRailroadLayoutConstraint(subExitPoint).setColumn(
					subGridPointer.getMaxColumn());
		}
		subGridPointer.incMaxColumn();
		layout.getRailroadLayoutConstraint(exitPoint).setColumn(
				subGridPointer.getMaxColumn());
		gridPointer.aggregate(subGridPointer);
		exitPoint = helper.createAndConnectExitPoints(exitPoint, gridPointer);
		return exitPoint;
	}

	public CrossPoint createSequence(CompoundElement compound,
			CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(
				this, compound, diagram);
		predecessor = helper.createEntryPoints(predecessor, gridPointer);
		GridPointer subGridPointer = gridPointer.clone();
		subGridPointer.resetMax();
		CrossPoint currentPredecessor = predecessor;
		for (AbstractElement e : compound.getElements()) {
			GridPointer currentgridPointer = subGridPointer.clone();
			currentPredecessor = transformer.transform(e, currentPredecessor,
					currentgridPointer, diagram);
			subGridPointer.aggregate(currentgridPointer);
		}
		currentPredecessor = helper.createAndConnectExitPoints(
				currentPredecessor, subGridPointer);
		gridPointer.aggregate(subGridPointer);
		return currentPredecessor;
	}
	
	protected CrossPoint createSubTrack(AbstractElement element,
			AbstractElement child, CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(
				this, element, diagram);
		predecessor = helper.createEntryPoints(predecessor, gridPointer);
		GridPointer subGridPointer = gridPointer.clone();
		CrossPoint successor = transformer.transform(child, predecessor,
				subGridPointer, diagram);
		successor = helper.createAndConnectExitPoints(successor, subGridPointer);
		gridPointer.aggregate(subGridPointer);
		return successor;
	}

	protected AbstractNode createNode(NodeType type, EObject grammarElement,
			String text) {
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
}
