package org.eclipse.xtext.graph.bundle;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

/**
 * Instantiates classes specified in the plugin.xml using the Guice injector.
 *  
 * @author koehnlein
 */
public class RailroadViewExecutableExtensionRegistry extends AbstractGuiceAwareExecutableExtensionFactory{

	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return Activator.getDefault().getInjector();
	}

}
