package org.eclipse.xtext.graph;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CompoundElement;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.graph.figures.Connection;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.util.GridData;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.inject.internal.Lists;

public class RailroadCreator {

	protected PolymorphicDispatcher<CrossPoint> transformer = new PolymorphicDispatcher<CrossPoint>(
			"transform", 3, 3, Collections.singletonList(this));

	private Font font;

	private RailroadConnectionRouter connectionRouter;

	private RailroadLayout layout;

	private Diagram diagram;

	private int currentTrack;
	
	public Font getFont() {
		return font;
	}

	public Diagram create(Grammar g, Font font) {
		this.font = font;
		connectionRouter = new RailroadConnectionRouter();
		layout = new RailroadLayout();
		diagram = new Diagram(g);
		diagram.setLayoutManager(layout);
		GridData gridData = new GridData();
		currentTrack=0;
		for (AbstractRule r : g.getRules()) {
			if (r instanceof ParserRule) {
				gridData.resetColumn();
				createNode(r, r.getName(), gridData);
				CrossPoint entryPoint = createCrossPoint(gridData);
				int entryRow = gridData.getRow();
				CrossPoint lastNode = transformer.invoke(r.getAlternatives(),
						entryPoint, gridData);
				gridData.setRow(entryRow);
				CrossPoint exitPoint = createCrossPoint(gridData);
				createConnection(lastNode, exitPoint, false, true);
				gridData.setRow(gridData.getMaxRow());
				gridData.incRow();
				++ currentTrack;
			}
		}
		return diagram;
	}

	protected CrossPoint transform(EObject o, CrossPoint predecessor,
			GridData gridData) {
		return predecessor;
	}

	protected CrossPoint transform(Alternatives a, CrossPoint predecessor,
			GridData gridData) {
		return createParallel(a, predecessor, gridData);
	}

	protected CrossPoint transform(Group g, CrossPoint predecessor,
			GridData gridData) {
		return createSequence(g, predecessor, gridData);
	}

	protected CrossPoint transform(UnorderedGroup g, CrossPoint predecessor,
			GridData gridData) {
		return createParallel(g, predecessor, gridData);
	}

	protected CrossPoint transform(Keyword k, CrossPoint predecessor,
			GridData gridData) {
		return createNode(k, k.getValue(), predecessor, gridData);
	}

	protected CrossPoint transform(RuleCall r, CrossPoint predecessor,
			GridData gridData) {
		return createNode(r, r.getRule().getName(), predecessor, gridData);
	}

	protected CrossPoint transform(Assignment a, CrossPoint predecessor,
			GridData gridData) {
		CrossPoint successor = transformer.invoke(a.getTerminal(), predecessor,
				gridData);
		addCardinalityConnections(predecessor, successor, a, gridData);
		return successor;
	}

	protected CrossPoint transform(CrossReference c, CrossPoint predecessor,
			GridData gridData) {
		CrossPoint successor = transformer.invoke(c.getTerminal(), predecessor,
				gridData);
		addCardinalityConnections(predecessor, successor, c, gridData);
		return successor;
	}

	protected CrossPoint createNode(AbstractElement element, String name,
			CrossPoint predecessor, GridData gridData) {
		CrossPoint entryPoint = createCrossPoint(gridData);
		createConnection(predecessor, entryPoint, false, false);
		AbstractNode node = createNode(element, name, gridData);
		createConnection(entryPoint, node, false, false);
		CrossPoint exitPoint = createCrossPoint(gridData);
		createConnection(node, exitPoint, false, false);
		addCardinalityConnections(entryPoint, exitPoint, element, gridData);
		return exitPoint;
	}

	private AbstractNode createNode(EObject element, String name,
			GridData gridData) {
		AbstractNode node = AbstractNode.create(element, name);
		gridData.incColumn();
		layout.setConstraint(
				node,
				new RailroadLayout.Constraint(gridData.getRow(), gridData
						.getColumn(), currentTrack));
		node.setFont(getFont());
		diagram.add(node);
		return node;
	}

	protected CrossPoint createCrossPoint(GridData gridData) {
		CrossPoint crossPoint = new CrossPoint();
		diagram.add(crossPoint);
		gridData.incColumn();
		layout.setConstraint(
				crossPoint,
				new RailroadLayout.Constraint(gridData.getRow(), gridData
						.getColumn(), currentTrack));
		return crossPoint;
	}

	protected Connection createConnection(CrossPoint source, CrossPoint target,
			boolean isDecorateSource, boolean isDecorateTarget) {
		Connection connection = new Connection(source, target,
				isDecorateSource, isDecorateTarget);
		diagram.add(connection);
		connection.setConnectionRouter(connectionRouter);
		return connection;
	}

	protected CrossPoint createParallel(CompoundElement compound,
			CrossPoint predecessor, GridData gridData) {
		CrossPoint entryPoint = createCrossPoint(gridData);
		createConnection(predecessor, entryPoint, false, false);
		int startColumn = gridData.getColumn();
		CrossPoint exitPoint = createCrossPoint(gridData);
		GridData subGridData = gridData.clone();
		subGridData.resetMax();
		List<CrossPoint> subExitPoints = Lists.newArrayList();
		for (int i = 0; i < compound.getElements().size(); ++i) {
			AbstractElement e = compound.getElements().get(i);
			GridData currentGridData = subGridData.clone();
			currentGridData.setColumn(startColumn);
			CrossPoint subExitPoint = transformer.invoke(e, entryPoint,
					currentGridData);
			subExitPoints.add(subExitPoint);
			subGridData.aggregateMax(currentGridData);
			subGridData.setRow(currentGridData.getMaxRow());
			subGridData.incRow();
			createConnection(subExitPoint, exitPoint, false, false);
		}
		for (CrossPoint subExitPoint : subExitPoints) {
			layout.getRailroadLayoutConstraint(subExitPoint).setColumn(
					subGridData.getMaxColumn());
		}
		subGridData.incMaxColumn();
		layout.getRailroadLayoutConstraint(exitPoint).setColumn(
				subGridData.getMaxColumn());
		addCardinalityConnections(predecessor, exitPoint, compound, subGridData);
		gridData.aggregateMax(subGridData);
		gridData.setColumn(subGridData.getMaxColumn());
		return exitPoint;
	}

	protected CrossPoint createSequence(CompoundElement compound,
			CrossPoint predecessor, GridData gridData) {
		CrossPoint entryPoint = createCrossPoint(gridData);
		createConnection(predecessor, entryPoint, false, false);
		GridData subGridData = gridData.clone();
		subGridData.resetMax();
		CrossPoint currentPredecessor = entryPoint;
		for (AbstractElement e : compound.getElements()) {
			GridData currentGridData = subGridData.clone();
			currentPredecessor = transformer.invoke(e, currentPredecessor,
					currentGridData);
			subGridData.aggregateMax(currentGridData);
			subGridData.setColumn(currentGridData.getColumn());
		}
		addCardinalityConnections(entryPoint, currentPredecessor, compound,
				subGridData);
		gridData.aggregateMax(subGridData);
		gridData.setColumn(subGridData.getColumn());
		return currentPredecessor;
	}

	protected void addCardinalityConnections(CrossPoint entry, CrossPoint exit,
			AbstractElement element, GridData gridData) {
		boolean isMultiple = GrammarUtil.isMultipleCardinality(element);
		boolean isOptional = GrammarUtil.isOptionalCardinality(element);
		if (isMultiple || isOptional) {
			gridData.incMaxRow();
			Connection connection = createConnection(exit, entry, isMultiple,
					isOptional);
			layout.setConstraint(connection, new RailroadLayout.Constraint(
					gridData.getMaxRow(), gridData.getColumn(), currentTrack));
		}
	}

}
