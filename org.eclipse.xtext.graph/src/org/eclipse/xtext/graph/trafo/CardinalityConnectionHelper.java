package org.eclipse.xtext.graph.trafo;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.util.GridData;

public class CardinalityConnectionHelper {
	private RailroadCreator creator;
	private CrossPoint loopEntry;
	private CrossPoint optionalEntry;

	private boolean isOptional;
	private boolean isMultiple;
	private EObject grammarElement;

	public CardinalityConnectionHelper(RailroadCreator creator,
			EObject grammarElement) {
		this.grammarElement = grammarElement;
		this.creator = creator;
	}

	public CrossPoint createEntryPoints(CrossPoint predecessor,
			GridData gridData) {
		CrossPoint currentPredecessor = predecessor;
		if (grammarElement instanceof AbstractElement) {
			AbstractElement element = (AbstractElement) grammarElement;
			isOptional = GrammarUtil.isOptionalCardinality(element);
			isMultiple = GrammarUtil.isMultipleCardinality(element);
			if (isMultiple) {
				loopEntry = createAndConnectCrossPoint(currentPredecessor,
						gridData);
				currentPredecessor = loopEntry;
			}
			if (isOptional) {
				optionalEntry = createAndConnectCrossPoint(
						currentPredecessor, gridData);
				currentPredecessor = createAndConnectCrossPoint(optionalEntry, gridData);
			}
		}
		return currentPredecessor;
	}

	public CrossPoint createAndConnectExitPoints(CrossPoint predecesor,
			GridData gridData) {
		CrossPoint currentSuccessor = predecesor;
		if (isOptional) {
			CrossPoint optionalExit = createAndConnectCrossPoint(
					currentSuccessor, gridData);
			creator.createCardinalityConnection(optionalEntry,
					optionalExit, gridData);
			currentSuccessor = optionalExit;
		}
		if (isMultiple) {
			CrossPoint loopExit = createAndConnectCrossPoint(
					currentSuccessor, gridData);
			creator.createCardinalityConnection(loopExit, loopEntry,
					gridData);
			// add an aditional crosspoint to avoid collisions with following connections 
			currentSuccessor = createAndConnectCrossPoint(loopExit, gridData);
		}
		return currentSuccessor;

	}

	private CrossPoint createAndConnectCrossPoint(CrossPoint predecessor,
			GridData gridData) {
		CrossPoint crossPoint = creator.createCrossPoint(gridData);
		creator.createConnection(predecessor, crossPoint);
		return crossPoint;
	}

}