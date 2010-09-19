package org.eclipse.xtext.graph.trafo;

import static org.eclipse.xtext.graph.trafo.NodeType.ERROR;
import static org.eclipse.xtext.graph.trafo.NodeType.LABEL;
import static org.eclipse.xtext.graph.trafo.NodeType.RECTANGLE;
import static org.eclipse.xtext.graph.trafo.NodeType.ROUNDED;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.graph.figures.CrossPoint;
import org.eclipse.xtext.graph.figures.Diagram;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.inject.Inject;

/**
 * Transforms an Xtext grammar model into a diagram with nodes ans edges.
 * 
 * @author koehnlein
 */
public class RailroadTransformer {

	private PolymorphicDispatcher<CrossPoint> transformer = new PolymorphicDispatcher<CrossPoint>("transformInternal",
			4, 4, Collections.singletonList(this), new PolymorphicDispatcher.ErrorHandler<CrossPoint>() {
				@Override
				public CrossPoint handle(Object[] params, Throwable throwable) {
					EObject grammarElement = (params[0] instanceof EObject) ? (EObject) params[0] : null;
					GridPointer gridPointer = (GridPointer) params[2];
					Diagram diagram = (Diagram) params[3];
					if (params[1] instanceof CrossPoint)
						return factory.createNode(ERROR, grammarElement, "ERROR", (CrossPoint) params[1], gridPointer,
								diagram);
					else
						return factory.createNode(ERROR, grammarElement, "ERROR", gridPointer, diagram);
				}
			});

	@Inject
	private RailroadFactory factory;

	public Diagram transform(Grammar grammar, GridPointer gridPointer, Diagram diagram) {
		for (AbstractRule rule : grammar.getRules()) {
			if (rule instanceof ParserRule) {
				gridPointer.resetColumn();
				factory.createNode(LABEL, rule, rule.getName(), gridPointer, diagram);
				CrossPoint entryPoint = factory.createCrossPoint(gridPointer, diagram);
				int entryRow = gridPointer.getRow();
				CrossPoint lastNode = transform(rule.getAlternatives(), entryPoint, gridPointer, diagram);
				gridPointer.setRow(entryRow);
				CrossPoint exitPoint = factory.createCrossPoint(gridPointer, diagram);
				factory.createConnection(lastNode, exitPoint, diagram);
				gridPointer.setRow(gridPointer.getMaxRow());
				gridPointer.incRow();
				gridPointer.incTrack();
			}
		}
		return diagram;
	}

	public CrossPoint transform(EObject object, CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		return transformer.invoke(object, predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(EObject o, CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		return predecessor;
	}

	protected CrossPoint transformInternal(Alternatives a, CrossPoint predecessor, GridPointer gridPointer,
			Diagram diagram) {
		return factory.createParallel(a, predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(Group g, CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		return factory.createSequence(g, predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(UnorderedGroup g, CrossPoint predecessor, GridPointer gridPointer,
			Diagram diagram) {
		return factory.createParallel(g, predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(Keyword k, CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		return factory.createNode(RECTANGLE, k, k.getValue(), predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(RuleCall r, CrossPoint predecessor, GridPointer gridPointer, Diagram diagram) {
		return factory.createNode(ROUNDED, r, r.getRule().getName(), predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(Assignment assignment, CrossPoint predecessor, GridPointer gridPointer,
			Diagram diagram) {
		return factory.createSubTrack(assignment, assignment.getTerminal(), predecessor, gridPointer, diagram);
	}

	protected CrossPoint transformInternal(CrossReference crossReference, CrossPoint predecessor,
			GridPointer gridPointer, Diagram diagram) {
		return factory.createSubTrack(crossReference, crossReference.getTerminal(), predecessor, gridPointer, diagram);
	}

}
