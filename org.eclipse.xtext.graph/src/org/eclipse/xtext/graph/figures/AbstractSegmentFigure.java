package org.eclipse.xtext.graph.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.graph.figures.primitives.CrossPoint;

/**
 * Base class of all {@link ISegmentFigure}s.
 * 
 * @author koehnlein
 */
public abstract class AbstractSegmentFigure extends Figure implements ISegmentFigure {

	private URI eObjectURI;
	private CrossPoint entry;
	private CrossPoint exit;

	protected AbstractSegmentFigure(EObject eObject) {
		if(eObject != null)
			eObjectURI = EcoreUtil.getURI(eObject);
		setLayoutManager(createLayoutManager());
	}
	
	public URI getEObjectURI() {
		return eObjectURI;
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
	
	public boolean isSelectable() {
		return false;
	}
}
