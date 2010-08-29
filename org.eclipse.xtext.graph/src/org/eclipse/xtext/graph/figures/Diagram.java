package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.Grammar;

public class Diagram extends Figure implements IGrammarElementReferer {

	private URI grammarElementURI;

	public Diagram(Grammar grammar) {
		grammarElementURI =  EcoreUtil.getURI(grammar);
		setOpaque(true);
		setBackgroundColor(ColorConstants.white);
	}
	
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	public URI getGrammarElementURI() {
		return grammarElementURI;
	}
}
