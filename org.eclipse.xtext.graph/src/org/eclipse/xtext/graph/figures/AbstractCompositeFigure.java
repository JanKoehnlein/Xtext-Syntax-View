package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.graph.figures.primitives.IGrammarElementReferer;

/**
 * Base class of all {@link ICompositeFigure}s.
 * 
 * @author koehnlein
 */
public abstract class AbstractCompositeFigure extends Figure implements ICompositeFigure, IGrammarElementReferer{

	private URI grammarElementURI;

	protected AbstractCompositeFigure(EObject grammarElement) {
		if(grammarElement != null)
			grammarElementURI = EcoreUtil2.getURI(grammarElement);
		setLayoutManager(createLayoutManager());
	}
	
	@Override
	public URI getGrammarElementURI() {
		return grammarElementURI;
	}

	protected abstract LayoutManager createLayoutManager();
	
}
