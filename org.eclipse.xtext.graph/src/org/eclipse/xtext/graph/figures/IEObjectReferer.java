package org.eclipse.xtext.graph.figures;

import org.eclipse.emf.common.util.URI;

/**
 * Something that refers to an EObject, e.g. an Xtext grammar element.
 * 
 * @author koehnlein
 */
public interface IEObjectReferer {

	URI getEObjectURI();
	
}
