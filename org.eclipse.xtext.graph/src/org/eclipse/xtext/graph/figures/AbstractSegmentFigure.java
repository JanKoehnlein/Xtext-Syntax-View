package org.eclipse.xtext.graph.figures;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;

/**
 * Base class of all {@link ISegmentFigure}s.
 * 
 * @author koehnlein
 */

public abstract class AbstractSegmentFigure extends AbstractCompositeFigure implements ISegmentFigure {
	
	private CrossPoint entry;
	
	private CrossPoint exit;

	protected AbstractSegmentFigure(EObject eObject) {
		super(eObject);
	}
	
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
