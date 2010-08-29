package org.eclipse.xtext.graph.figures;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Grammar;

public class FigureFactory {

	public static enum NodeType {
		ROUNDED, RECTANGLE, ERROR, LABEL
	};

	public AbstractNode createNode(NodeType type, EObject grammarElement,
			String text) {
		switch (type) {
		case RECTANGLE:
			return new RectangleNode(grammarElement, text);
		case ROUNDED:
			return new RoundedNode(grammarElement, text);
		case ERROR:
			return new ErrorNode(grammarElement, text);
		case LABEL:
			return new LabelNode(grammarElement, text);
		default:
			throw new IllegalArgumentException("Unknown node type " + type);
		}
	}
	
	public CrossPoint createCrossPoint() {
		return new CrossPoint();
	}
	
	public Diagram createDiagram(Grammar grammar) {
		return new Diagram(grammar);
	}
	
	public Connection createConnection(CrossPoint source, CrossPoint target) {
		return new Connection(source, target);
	}
	
}
