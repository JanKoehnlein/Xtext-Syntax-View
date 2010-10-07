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

	private PolymorphicDispatcher<ISegmentFigure> transformer = new PolymorphicDispatcher<ISegmentFigure>(
			"transformInternal", 1, 1, Collections.singletonList(this),
			new PolymorphicDispatcher.ErrorHandler<ISegmentFigure>() {
				public ISegmentFigure handle(Object[] params, Throwable throwable) {
					EObject grammarElement = (params[0] instanceof EObject) ? (EObject) params[0] : null;
					return factory.createNodeSegment(grammarElement, throwable);
				}
			});

	@Inject
	private Xtext2RailroadFactory factory;

	public ISegmentFigure transform(EObject object) {
		return transformer.invoke(object);
	}

	protected ISegmentFigure transformInternal(Grammar grammar) {
		List<ISegmentFigure> children = transformChildren(grammar.getRules());
		ISegmentFigure diagram = factory.createDiagram(grammar, children);
		return diagram;
	}

	protected ISegmentFigure transformInternal(ParserRule parserRule) {
		ISegmentFigure body = transform(parserRule.getAlternatives());
		ISegmentFigure track = factory.createTrack(parserRule, body);
		return track;
	}

	protected ISegmentFigure transformInternal(EObject eObject) {
		return null;
	}

	protected ISegmentFigure transformInternal(Alternatives alternatives) {
		List<ISegmentFigure> children = transformChildren(alternatives.getElements());
		return factory.createParallel(alternatives, children);
	}

	protected ISegmentFigure transformInternal(Group group) {
		List<ISegmentFigure> children = transformChildren(group.getElements());
		return factory.createSequence(group, children);
	}

	protected ISegmentFigure transformInternal(UnorderedGroup unorderedGroup) {
		List<ISegmentFigure> children = transformChildren(unorderedGroup.getElements());
		return factory.createParallel(unorderedGroup, children);
	}

	protected ISegmentFigure transformInternal(Keyword keyword) {
		return factory.createNodeSegment(keyword);
	}

	protected ISegmentFigure transformInternal(RuleCall ruleCall) {
		return factory.createNodeSegment(ruleCall);
	}

	protected ISegmentFigure transformInternal(Assignment assignment) {
		return transform(assignment.getTerminal());
	}

	protected ISegmentFigure transformInternal(CrossReference crossReference) {
		return transform(crossReference.getTerminal());
	}

	private List<ISegmentFigure> transformChildren(List<? extends EObject> children) {
		List<ISegmentFigure> transformedChildren = Lists.newArrayList();
		for (EObject child : children) {
			ISegmentFigure transformedChild = transform(child);
			if (transformedChild != null)
				transformedChildren.add(transformedChild);
		}
		return transformedChildren;
	}

}
