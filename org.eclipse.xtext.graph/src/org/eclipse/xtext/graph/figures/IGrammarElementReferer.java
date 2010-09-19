package org.eclipse.xtext.graph.figures;

import org.eclipse.emf.common.util.URI;

/**
 * Something that refers to a grammar element.
 * 
 * @author koehnlein
 */
public interface IGrammarElementReferer {

	URI getGrammarElementURI();
	
}
