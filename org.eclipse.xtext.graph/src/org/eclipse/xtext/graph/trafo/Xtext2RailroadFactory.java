package org.eclipse.xtext.graph.trafo;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.graph.figures.BypassSegment;
import org.eclipse.xtext.graph.figures.ICompositeFigure;
import org.eclipse.xtext.graph.figures.ISegmentFigure;
import org.eclipse.xtext.graph.figures.LoopSegment;
import org.eclipse.xtext.graph.figures.NodeSegment;
import org.eclipse.xtext.graph.figures.ParallelSegment;
import org.eclipse.xtext.graph.figures.RailroadDiagram;
import org.eclipse.xtext.graph.figures.SequenceSegment;
import org.eclipse.xtext.graph.figures.RailroadTrack;
import org.eclipse.xtext.graph.figures.primitives.NodeType;
import org.eclipse.xtext.graph.figures.primitives.PrimitiveFigureFactory;

import com.google.inject.Inject;

/**
 * Creates railrowad {@link ICompositeFigure}s and {@link ISegmentFigure}s for Xtext artifacts. 
 * 
 * @author koehnlein
 */
public class Xtext2RailroadFactory {

	@Inject
	private PrimitiveFigureFactory primitiveFactory;

	public ICompositeFigure createNodeSegment(Keyword keyword) {
		NodeSegment nodeSegment = new NodeSegment(keyword, NodeType.RECTANGLE, keyword.getValue(), primitiveFactory);
		return wrapCardinaltiySegments(keyword, nodeSegment);
	}

	public ICompositeFigure createNodeSegment(RuleCall ruleCall) {
		NodeSegment nodeSegment = new NodeSegment(ruleCall, NodeType.ROUNDED, ruleCall.getRule().getName(),
				primitiveFactory);
		return wrapCardinaltiySegments(ruleCall, nodeSegment);
	}

	public ICompositeFigure createNodeSegment(EObject grammarElement, Throwable throwable) {
		return new NodeSegment(grammarElement, NodeType.ERROR, "ERROR", primitiveFactory);
	}

	public ICompositeFigure createTrack(AbstractRule rule, ISegmentFigure body) {
		return new RailroadTrack(rule, rule.getName(), body, primitiveFactory);
	}

	public ICompositeFigure createDiagram(Grammar grammar, List<ICompositeFigure> children) {
		return new RailroadDiagram(grammar, children);
	}

	public ICompositeFigure createSequence(Group group, List<ISegmentFigure> children) {
		SequenceSegment sequence = new SequenceSegment(group, children, primitiveFactory);
		return wrapCardinaltiySegments(group, sequence);
	}

	public ICompositeFigure createParallel(Alternatives alternatives, List<ISegmentFigure> children) {
		ParallelSegment multiSwitch = new ParallelSegment(alternatives, children, primitiveFactory);
		return wrapCardinaltiySegments(alternatives, multiSwitch);
	}

	public ICompositeFigure createParallel(UnorderedGroup unorderedGroup, List<ISegmentFigure> children) {
		ParallelSegment multiSwitch = new ParallelSegment(unorderedGroup, children, primitiveFactory);
		return wrapCardinaltiySegments(unorderedGroup, multiSwitch);
	}

	protected ICompositeFigure wrapCardinaltiySegments(AbstractElement element, ISegmentFigure segment) {
		ISegmentFigure result = segment;
		if (GrammarUtil.isMultipleCardinality(element)) {
			result = new LoopSegment(element, result, primitiveFactory);
		}
		if (GrammarUtil.isOptionalCardinality(element)) {
			result = new BypassSegment(element, result, primitiveFactory);
		}
		return result;
	}

}
