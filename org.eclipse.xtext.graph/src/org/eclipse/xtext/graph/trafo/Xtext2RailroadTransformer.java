package org.eclipse.xtext.graph.trafo;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.graph.figures.ICompositeFigure;
import org.eclipse.xtext.graph.figures.ISegmentFigure;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Transforms an Xtext grammar model into a diagram consisting of composite
 * figures.
 * 
 * @author koehnlein
 */
public class Xtext2RailroadTransformer {

	private PolymorphicDispatcher<ICompositeFigure> transformer = new PolymorphicDispatcher<ICompositeFigure>(
			"transformInternal", 1, 1, Collections.singletonList(this),
			new PolymorphicDispatcher.ErrorHandler<ICompositeFigure>() {
				@Override
				public ICompositeFigure handle(Object[] params, Throwable throwable) {
					EObject grammarElement = (params[0] instanceof EObject) ? (EObject) params[0] : null;
					return factory.createNodeSegment(grammarElement, throwable);
				}
			});

	@Inject
	private Xtext2RailroadFactory factory;

	public ICompositeFigure transform(EObject object) {
		return transformer.invoke(object);
	}

	protected ICompositeFigure transformInternal(Grammar grammar) {
		List<ICompositeFigure> children = transformChildren(grammar.getRules());
		ICompositeFigure diagram = factory.createDiagram(grammar, children);
		return diagram;
	}

	protected ICompositeFigure transformInternal(ParserRule parserRule) {
		ISegmentFigure body = (ISegmentFigure) transform(parserRule.getAlternatives());
		ICompositeFigure track = factory.createTrack(parserRule, body);
		return track;
	}

	protected ICompositeFigure transformInternal(EObject eObject) {
		return null;
	}

	protected ICompositeFigure transformInternal(Alternatives alternatives) {
		List<ISegmentFigure> children = transformChildrenToSegments(alternatives.getElements());
		return factory.createParallel(alternatives, children);
	}

	protected ICompositeFigure transformInternal(Group group) {
		List<ISegmentFigure> children = transformChildrenToSegments(group.getElements());
		return factory.createSequence(group, children);
	}

	protected ICompositeFigure transformInternal(UnorderedGroup unorderedGroup) {
		List<ISegmentFigure> children = transformChildrenToSegments(unorderedGroup.getElements());
		return factory.createParallel(unorderedGroup, children);
	}

	protected ICompositeFigure transformInternal(Keyword keyword) {
		return factory.createNodeSegment(keyword);
	}

	protected ICompositeFigure transformInternal(RuleCall ruleCall) {
		return factory.createNodeSegment(ruleCall);
	}

	protected ICompositeFigure transformInternal(Assignment assignment) {
		return transform(assignment.getTerminal());
	}

	protected ICompositeFigure transformInternal(CrossReference crossReference) {
		return transform(crossReference.getTerminal());
	}

	private List<ICompositeFigure> transformChildren(List<? extends EObject> children) {
		List<ICompositeFigure> transformedChildren = Lists.newArrayList();
		for (EObject child : children) {
			ICompositeFigure transformedChild = transform(child);
			if (transformedChild != null)
				transformedChildren.add(transformedChild);
		}
		return transformedChildren;
	}

	private List<ISegmentFigure> transformChildrenToSegments(List<? extends EObject> children) {
		List<ISegmentFigure> transformedChildren = Lists.newArrayList();
		for (EObject child : children) {
			ICompositeFigure transformedChild = transform(child);
			if (transformedChild instanceof ISegmentFigure)
				transformedChildren.add((ISegmentFigure) transformedChild);
		}
		return transformedChildren;
	}
}
