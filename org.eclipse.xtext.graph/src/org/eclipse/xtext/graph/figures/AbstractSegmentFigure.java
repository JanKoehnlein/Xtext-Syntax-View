package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;
import org.eclipse.xtext.graph.figures.primitives.IGrammarElementReferer;

/**
 * Base class of all {@link ISegmentFigure}s.
 * 
 * @author koehnlein
 */
public abstract class AbstractSegmentFigure extends Figure implements ISegmentFigure, IGrammarElementReferer{

	private URI grammarElementURI;
	private CrossPoint entry;
	private CrossPoint exit;

	protected AbstractSegmentFigure(EObject grammarElement) {
		if(grammarElement != null)
			grammarElementURI = EcoreUtil2.getURI(grammarElement);
		setLayoutManager(createLayoutManager());
	}
	
	@Override
	public URI getGrammarElementURI() {
		return grammarElementURI;
	}

	protected abstract LayoutManager createLayoutManager();

	public CrossPoint getEntry() {
		return entry;
	}

	public CrossPoint getExit() {
		return exit;
	}

	protected void setEntry(CrossPoint entry) {
		this.entry = entry;
	}

	protected void setExit(CrossPoint exit) {
		this.exit = exit;
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}
	
}
