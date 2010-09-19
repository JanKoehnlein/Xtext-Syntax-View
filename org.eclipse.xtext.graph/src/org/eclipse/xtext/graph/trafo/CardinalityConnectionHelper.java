package org.eclipse.xtext.graph.trafo;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.figures.Diagram;

/**
 * Helps to calculate connections resulting from cardinalities.
 * 
 * @author koehnlein
 */
public class CardinalityConnectionHelper {
	private RailroadFactory factory;
	private EObject grammarElement;
	private Diagram diagram;

	private CrossPoint loopEntry;
	private CrossPoint optionalEntry;

	private boolean isOptional;
	private boolean isMultiple;

	public CardinalityConnectionHelper(RailroadFactory factory,
			EObject grammarElement, Diagram diagram) {
		this.factory = factory;
		this.grammarElement = grammarElement;
		this.diagram = diagram;
	}

	public CrossPoint createEntryPoints(CrossPoint predecessor,
			GridPointer gridPointer) {
		CrossPoint currentPredecessor = predecessor;
		if (grammarElement instanceof AbstractElement) {
			AbstractElement element = (AbstractElement) grammarElement;
			isOptional = GrammarUtil.isOptionalCardinality(element);
			isMultiple = GrammarUtil.isMultipleCardinality(element)
					|| grammarElement instanceof UnorderedGroup;
			if (isMultiple) {
				loopEntry = createAndConnectCrossPoint(currentPredecessor,
						gridPointer);
				currentPredecessor = loopEntry;
			}
			if (isOptional) {
				optionalEntry = createAndConnectCrossPoint(currentPredecessor,
						gridPointer);
				// add an aditional crosspoint to avoid collisions with
				// following connections
				currentPredecessor = createAndConnectCrossPoint(optionalEntry,
						gridPointer);
			}
		}
		return currentPredecessor;
	}

	public CrossPoint createAndConnectExitPoints(CrossPoint predecesor,
			GridPointer gridPointer) {
		CrossPoint currentSuccessor = predecesor;
		if (isOptional) {
			CrossPoint optionalExit = createAndConnectCrossPoint(
					currentSuccessor, gridPointer);
			factory.createCardinalityConnection(optionalEntry, optionalExit,
					gridPointer, diagram);
			currentSuccessor = optionalExit;
		}
		if (isMultiple) {
			CrossPoint loopExit = createAndConnectCrossPoint(currentSuccessor,
					gridPointer);
			factory.createCardinalityConnection(loopExit, loopEntry,
					gridPointer, diagram);
			// add an aditional crosspoint to avoid collisions with following
			// connections
			currentSuccessor = createAndConnectCrossPoint(loopExit, gridPointer);
		}
		return currentSuccessor;

	}

	private CrossPoint createAndConnectCrossPoint(CrossPoint predecessor,
			GridPointer gridPointer) {
		CrossPoint crossPoint = factory.createCrossPoint(gridPointer, diagram);
		factory.createConnection(predecessor, crossPoint, diagram);
		return crossPoint;
	}

}