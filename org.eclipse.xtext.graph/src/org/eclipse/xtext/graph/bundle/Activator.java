package org.eclipse.xtext.graph.bundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Activates the plugin and initializes the Guice injector.
 * 
 * @author koehnlein
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.xtext.graph"; //$NON-NLS-1$

	private static Activator plugin;

	private Injector injector;

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		injector = Guice.createInjector(new RailroadModule(), new SharedStateModule());
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public Injector getInjector() {
		return injector;
	}
}
