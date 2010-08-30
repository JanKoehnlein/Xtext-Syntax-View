package org.eclipse.xtext.graph.trafo;

import static org.eclipse.xtext.graph.figures.FigureFactory.NodeType.ERROR;
import static org.eclipse.xtext.graph.figures.FigureFactory.NodeType.LABEL;
import static org.eclipse.xtext.graph.figures.FigureFactory.NodeType.RECTANGLE;
import static org.eclipse.xtext.graph.figures.FigureFactory.NodeType.ROUNDED;

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
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.graph.RailroadConnectionRouter;
import org.eclipse.xtext.graph.RailroadLayout;
import org.eclipse.xtext.graph.figures.AbstractNode;
import org.eclipse.xtext.graph.figures.Connection;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.graph.figures.FigureFactory;
import org.eclipse.xtext.graph.util.GridData;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.inject.internal.Lists;

public class RailroadCreator {

	protected PolymorphicDispatcher<CrossPoint> transformer = new PolymorphicDispatcher<CrossPoint>(
			"transform", 3, 3, Collections.singletonList(this),
			new PolymorphicDispatcher.ErrorHandler<CrossPoint>() {
				@Override
				public CrossPoint handle(Object[] params, Throwable throwable) {
					EObject grammarElement = (params[0] instanceof EObject) ? (EObject) params[0]
							: null;
					GridData gridData = (GridData) params[2];
					if (params[1] instanceof CrossPoint)
						return createNode(ERROR, grammarElement, "ERROR",
								(CrossPoint) params[1], gridData);
					else
						return createNode(ERROR, grammarElement, "ERROR",
								gridData);
				}
			});

	private FigureFactory factory;

	private Font font;

	private RailroadConnectionRouter connectionRouter;

	private RailroadLayout layout;

	private Diagram diagram;

	private int currentTrack;

	public Font getFont() {
		return font;
	}

	public Diagram create(Grammar grammar, Font font) {
		this.font = font;
		factory = new FigureFactory();
		connectionRouter = new RailroadConnectionRouter();
		layout = new RailroadLayout();
		diagram = factory.createDiagram(grammar);
		diagram.setLayoutManager(layout);
		GridData gridData = new GridData();
		currentTrack = 0;
		for (AbstractRule rule : grammar.getRules()) {
			if (rule instanceof ParserRule) {
				gridData.resetColumn();
				createNode(LABEL, rule, rule.getName(), gridData);
				CrossPoint entryPoint = createCrossPoint(gridData);
				int entryRow = gridData.getRow();
				CrossPoint lastNode = transformer.invoke(
						rule.getAlternatives(), entryPoint, gridData);
				gridData.setRow(entryRow);
				CrossPoint exitPoint = createCrossPoint(gridData);
				createConnection(lastNode, exitPoint);
				gridData.setRow(gridData.getMaxRow());
				gridData.incRow();
				++currentTrack;
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
		return createNode(RECTANGLE, k, k.getValue(), predecessor, gridData);
	}

	protected CrossPoint transform(RuleCall r, CrossPoint predecessor,
			GridData gridData) {
		return createNode(ROUNDED, r, r.getRule().getName(), predecessor,
				gridData);
	}

	protected CrossPoint transform(Assignment assignment, CrossPoint predecessor,
			GridData gridData) {
		return transformRecursive(assignment, assignment.getTerminal(), predecessor, gridData);
	}

	protected CrossPoint transform(CrossReference crossReference, CrossPoint predecessor,
			GridData gridData) {
		return transformRecursive(crossReference, crossReference.getTerminal(), predecessor, gridData);
	}
	
	protected CrossPoint transformRecursive(AbstractElement element, AbstractElement child, CrossPoint predecessor, GridData gridData) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(this, element);
		predecessor = helper.createEntryPoints(predecessor, gridData);
		GridData subGridData = gridData.clone();
		CrossPoint successor = transformer.invoke(child, predecessor,
				subGridData);
		successor = helper.createAndConnectExitPoints(successor, subGridData);
		gridData.aggregateMax(subGridData);
		gridData.setColumn(subGridData.getMaxColumn());
		return successor;
	}

	protected CrossPoint createNode(FigureFactory.NodeType nodeType,
			EObject element, String name, CrossPoint predecessor,
			GridData gridData) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(this, element);
		predecessor = helper.createEntryPoints(predecessor, gridData);
		gridData.resetMax();
		AbstractNode node = createNode(nodeType, element, name, gridData);
		createConnection(predecessor, node);
		CrossPoint exitPoint = helper.createAndConnectExitPoints(node, gridData);
		gridData.aggregateMax(gridData);
		gridData.setColumn(gridData.getColumn());
		return exitPoint;
	}

	private AbstractNode createNode(FigureFactory.NodeType nodeType,
			EObject grammarElement, String name, GridData gridData) {
		AbstractNode node = factory.createNode(nodeType, grammarElement, name);
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
		CrossPoint crossPoint = factory.createCrossPoint();
		diagram.add(crossPoint);
		gridData.incColumn();
		layout.setConstraint(
				crossPoint,
				new RailroadLayout.Constraint(gridData.getRow(), gridData
						.getColumn(), currentTrack));
		return crossPoint;
	}

	protected Connection createConnection(CrossPoint source, CrossPoint target) {
		Connection connection = factory.createConnection(source, target);
		diagram.add(connection);
		connection.setConnectionRouter(connectionRouter);
		return connection;
	}

	protected Connection createCardinalityConnection(CrossPoint source,
			CrossPoint target, GridData gridData) {
		gridData.incMaxRow();
		Connection connection = createConnection(source, target);
		layout.setConstraint(
				connection,
				new RailroadLayout.Constraint(gridData.getMaxRow(), gridData
						.getColumn(), currentTrack));
		return connection;
	}

	protected CrossPoint createParallel(CompoundElement compound,
			CrossPoint predecessor, GridData gridData) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(this, compound);
		predecessor = helper.createEntryPoints(predecessor, gridData);
		CrossPoint entryPoint = createCrossPoint(gridData);
		createConnection(predecessor, entryPoint);
		int startColumn = gridData.getColumn();
		CrossPoint exitPoint = createCrossPoint(gridData);
		GridData subGridData = gridData.clone();
		subGridData.resetMax();
		List<CrossPoint> subExitPoints = Lists.newArrayList();
		for (int i = 0; i < compound.getElements().size(); ++i) {
			AbstractElement e = compound.getElements().get(i);
			subGridData.setColumn(startColumn);
			GridData currentGridData = subGridData.clone();
			CrossPoint subEntryPoint = createCrossPoint(currentGridData);
			createConnection(entryPoint, subEntryPoint);
			CrossPoint subPoint = transformer.invoke(e, subEntryPoint,
					currentGridData);
			CrossPoint subExitPoint = createCrossPoint(currentGridData);
			createConnection(subPoint, subExitPoint);
			subExitPoints.add(subExitPoint);
			subGridData.aggregateMax(currentGridData);
			subGridData.setRow(currentGridData.getMaxRow());
			subGridData.incRow();
			createConnection(subExitPoint, exitPoint);
		}
		for (CrossPoint subExitPoint : subExitPoints) {
			// place all sub ends at the outermost column
			layout.getRailroadLayoutConstraint(subExitPoint).setColumn(
					subGridData.getMaxColumn());
		}
		subGridData.incMaxColumn();
		layout.getRailroadLayoutConstraint(exitPoint).setColumn(
				subGridData.getMaxColumn());
		gridData.aggregateMax(subGridData);
		gridData.setColumn(subGridData.getMaxColumn());
		exitPoint = helper.createAndConnectExitPoints(exitPoint, gridData);
		return exitPoint;
	}

	protected CrossPoint createSequence(CompoundElement compound,
			CrossPoint predecessor, GridData gridData) {
		CardinalityConnectionHelper helper = new CardinalityConnectionHelper(this, compound);
		predecessor = helper.createEntryPoints(predecessor, gridData);
		GridData subGridData = gridData.clone();
		subGridData.resetMax();
		CrossPoint currentPredecessor = predecessor;
		for (AbstractElement e : compound.getElements()) {
			GridData currentGridData = subGridData.clone();
			currentPredecessor = transformer.invoke(e, currentPredecessor,
					currentGridData);
			subGridData.aggregateMax(currentGridData);
			subGridData.setColumn(currentGridData.getColumn());
		}
		currentPredecessor = helper.createAndConnectExitPoints(currentPredecessor, subGridData);
		gridData.aggregateMax(subGridData);
		gridData.setColumn(subGridData.getColumn());
		return currentPredecessor;
	}

}
